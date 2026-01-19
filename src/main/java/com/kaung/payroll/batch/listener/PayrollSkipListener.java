package com.kaung.payroll.batch.listener;

import com.kaung.payroll.batch.domain.MonthlyPayroll;
import com.kaung.payroll.batch.domain.Payroll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class PayrollSkipListener implements SkipListener<Payroll, MonthlyPayroll> {

    private final Path errorFilePath;

    public PayrollSkipListener(String errorFilePath)
    {
        this.errorFilePath = Paths.get(errorFilePath);
    }

    @Override
    public void onSkipInRead(Throwable t)
    {
        log.debug("☢️ SKIPPED ONE LINE -----");
        System.out.println("☢️ SKIPPED ONE LINE -----");

        if (t instanceof FlatFileParseException exception) {
            writeErrorReportFile(exception);
        }
    }

    @Override
    public void onSkipInWrite(MonthlyPayroll item, Throwable t)
    {
        log.debug("☢️ SKIPPED IN WRITE -----");
        if (t instanceof FlatFileParseException exception)
            writeErrorReportFile(exception);
    }

    @Override
    public void onSkipInProcess(Payroll item, Throwable t)
    {
        log.debug("☢️ SKIPPED IN PROCESS -----");
        if (t instanceof FlatFileParseException exception)
            writeErrorReportFile(exception);
    }

    private void writeErrorReportFile(FlatFileParseException exception)
    {
        String rawLine = exception.getInput();
        int lineNumber = exception.getLineNumber();
        String line = lineNumber + "|" + rawLine + System.lineSeparator();

        try {
            Files.writeString(this.errorFilePath, line, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write skipped line " + lineNumber);
        }
    }
}
