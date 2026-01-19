package com.kaung.payroll.batch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportTasklet implements Tasklet {

    private final Path reportFilePath;

    public ReportTasklet(String reportFilePath)
    {
        this.reportFilePath = Paths.get(reportFilePath);
    }

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        ExecutionContext executionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();

        long totalItems = executionContext.getLong("totalItems");
        long successfulItems = executionContext.getLong("successfulItems");
        long skippedItems = executionContext.getLong("skippedItems");

        this.writeReport(this.reportFilePath, totalItems, successfulItems, skippedItems);

        return RepeatStatus.FINISHED;
    }

    private void writeReport(Path reportFilePath, long totalItems, long successfulItems, long skippedItems)
    {
        try {
            Files.createDirectories(reportFilePath.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(reportFilePath)) {

                writer.write("metric,value");
                writer.newLine();

                writer.write("total_items," + totalItems);
                writer.newLine();

                writer.write("successful_items," + successfulItems);
                writer.newLine();

                writer.write("skipped_items," + skippedItems);
                writer.newLine();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
