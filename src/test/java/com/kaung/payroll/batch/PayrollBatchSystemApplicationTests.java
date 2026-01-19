package com.kaung.payroll.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBatchTest
@SpringBootTest
class PayrollBatchSystemApplicationTests {

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @BeforeEach
    void setUp()
    {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    public void testCheckFileStep_successful()
    {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input.file", "input/2026-01.csv")
                .toJobParameters();

        JobExecution step_one = this.jobOperatorTestUtils
                .startStep("checkInputFile", jobParameters, null);

        assertEquals(ExitStatus.COMPLETED, step_one.getExitStatus());
    }

    @Test
    public void testCheckFileStep_failed()
    {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("input.file", "input/2026-14.csv")
                .toJobParameters();

        JobExecution step_one = this.jobOperatorTestUtils
                .startStep("checkInputFile", jobParameters, null);

        assertEquals("FAILED", step_one.getExitStatus().getExitCode());
    }

}
