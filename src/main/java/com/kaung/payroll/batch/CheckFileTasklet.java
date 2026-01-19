package com.kaung.payroll.batch;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckFileTasklet implements Tasklet {
    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        JobParameters jobParameters = contribution.getStepExecution().getJobParameters();

        String filePathString = jobParameters.getString("input.file");

        System.out.println("☢️ file path " + filePathString);

        if (filePathString == null || filePathString.isEmpty())
            throw new IllegalStateException("File not found.");

        Path filePath = Paths.get("").toAbsolutePath().resolve(filePathString);

        boolean isFileExisted = Files.exists(filePath);

        if (!isFileExisted)
            throw new IllegalStateException("File does not exist.");

        boolean isFileFormatCorrect = checkFileFormat(filePath.getFileName());

        if (!isFileFormatCorrect)
            throw new IllegalStateException("File type mismatch.");

        return RepeatStatus.FINISHED;
    }

    private boolean checkFileFormat(Path filePath)
    {
        try {
            System.out.println("File type: " + filePath.getFileName().toString());
            return filePath.getFileName().toString().endsWith("csv");
        } catch (Exception e) {
            return false;
        }
    }

}
