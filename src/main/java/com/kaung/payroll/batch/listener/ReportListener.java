package com.kaung.payroll.batch.listener;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;

public class ReportListener implements StepExecutionListener {
    @Override
    public @Nullable ExitStatus afterStep(StepExecution stepExecution)
    {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        executionContext.putLong("totalItems", stepExecution.getReadCount());
        executionContext.putLong("successfulItems", stepExecution.getWriteCount());
        executionContext.putLong("skippedItems", stepExecution.getSkipCount());

        return ExitStatus.COMPLETED;
    }
}
