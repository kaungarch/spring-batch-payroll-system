package com.kaung.payroll.batch;

import com.kaung.payroll.batch.domain.MonthlyPayroll;
import com.kaung.payroll.batch.domain.Payroll;
import com.kaung.payroll.batch.listener.PayrollItemReadListener;
import com.kaung.payroll.batch.listener.ReportListener;
import com.kaung.payroll.batch.processor.PayrollItemProcessor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
public class PayrollConfig {

    @Bean
    public Job job(
            JobRepository jobRepository,
            Step step1,
            Step step2,
            Step step3
    )
    {
        return new JobBuilder("monthlyPayrollJob", jobRepository)
                .start(step1)
                .next(step2)
                .next(step3)
                .build();
    }

    @Bean
    public CheckFileTasklet checkFileTasklet()
    {
        return new CheckFileTasklet();
    }

    @Bean
    public Step step1(
            JobRepository jobRepository,
            JdbcTransactionManager transactionManager,
            CheckFileTasklet tasklet)
    {
        return new StepBuilder("checkInputFile", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step2(
            JobRepository jobRepository,
            JdbcTransactionManager transactionManager,
            FlatFileItemReader<Payroll> payrollFlatFileItemReader,
            PayrollItemProcessor payrollItemProcessor,
            JdbcBatchItemWriter<MonthlyPayroll> jdbcBatchItemWriter,
            ItemReadListener<Payroll> itemReadListener,
            ReportListener reportListener
    )
    {
        return new StepBuilder("fileIngestion", jobRepository)
                .<Payroll, MonthlyPayroll>chunk(50)
                .transactionManager(transactionManager)
                .reader(payrollFlatFileItemReader)
                .processor(payrollItemProcessor)
                .writer(jdbcBatchItemWriter)
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(10)
                .listener(itemReadListener)
                .listener(reportListener)
                .build();
    }

    @Bean
    public Step step3(
            JobRepository jobRepository,
            ReportTasklet reportTasklet
    )
    {
        return new StepBuilder("reportStep", jobRepository)
                .tasklet(reportTasklet)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Payroll> payrollFlatFileItemReader(
            @Value("#{jobParameters['input.file']}") String inputFile
    )
    {
        return new FlatFileItemReaderBuilder<Payroll>()
                .name("payRollFlatFileReader")
                .resource(new FileSystemResource(inputFile))
                .linesToSkip(1)
                .delimited()
                .names(
                        "employeeId",
                        "name",
                        "jobPosition",
                        "baseSalary",
                        "workDays",
                        "bonus"
                )
                .targetType(Payroll.class)
                .build();
    }

    @Bean
    @StepScope
    public PayrollItemProcessor payRollItemProcessor(
            @Value("#{jobParameters['data.month']}") String month,
            @Value("#{jobParameters['data.year']}") String year
    )
    {
        return new PayrollItemProcessor(Integer.parseInt(month), Integer.parseInt(year));
    }

    @Bean
    public JdbcBatchItemWriter<MonthlyPayroll> jdbcBatchItemWriter(
            DataSource dataSource
    )
    {
        String sql =
                "insert into monthly_payroll (" +
                        "employee_id, name, job_position, base_salary, work_days, bonus, " +
                        "monthly_salary, month, year" +
                        ") values (" +
                        ":payroll.employeeId, :payroll.name, :payroll.jobPosition, " +
                        ":payroll.baseSalary, :payroll.workDays, :payroll.bonus, " +
                        ":monthlySalary, :month, :year" +
                        ")";
        return new JdbcBatchItemWriterBuilder<MonthlyPayroll>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    @StepScope
    public ItemReadListener<Payroll> itemReadListener(
            @Value("#{jobParameters['error.file']}") String errorFilePath
    )
    {
        return new PayrollItemReadListener(errorFilePath);
    }

    @Bean
    public ReportListener reportListener()
    {
        return new ReportListener();
    }

    @Bean
    @StepScope
    public ReportTasklet reportTasklet(
            @Value("#{jobParameters['report.file']}") String reportFile
    )
    {
        return new ReportTasklet(reportFile);
    }

}
