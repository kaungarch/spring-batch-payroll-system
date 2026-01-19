package com.kaung.payroll.batch.listener;

import com.kaung.payroll.batch.domain.Payroll;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PayrollItemReadListener implements ItemReadListener<Payroll> {

    private final Path errorFilePath;

    public PayrollItemReadListener(String errorFilePath)
    {
        this.errorFilePath = Paths.get(errorFilePath);
    }

    @Override
    public void onReadError(Exception ex)
    {
        if(ex.getCause() instanceof FlatFileParseException exception)
            this.writeErrorReportFile(exception);
    }

    private void writeErrorReportFile(FlatFileParseException exception)
    {
        String rawLine = exception.getInput();
        int lineNumber = exception.getLineNumber();
        String line = lineNumber + "|" + rawLine + System.lineSeparator();

        try {
            Files.createDirectories(this.errorFilePath.getParent());

            Files.writeString(this.errorFilePath, line, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write skipped line " + lineNumber);
        }
    }
}
