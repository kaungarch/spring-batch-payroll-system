# Payroll Batch System

This project is a Spring Batch–based payroll processing system. It processes a monthly CSV file, calculates employee salaries, persists valid records into a database, and generates reports for both successful and failed records.

## System Overview

The batch job is designed to run once per month and consists of multiple sequential steps. Each step has a clearly defined responsibility to keep the job simple, traceable, and easy to maintain.

## System Flow

When the job starts, the first step checks whether the input file exists and whether the file format is valid. If the file does not exist or the format is incorrect, the job stops immediately and no further processing is performed.

In the second step, the system reads the input CSV file line by line. Each line is mapped into a domain model representing payroll data. During this phase, monthly salary calculation logic is applied based on job position, base salary, working days, and bonus. Valid records are written into the database as monthly payroll entries.

If a record cannot be parsed or processed correctly, it is skipped. Skipped records do not stop the job. Instead, they are collected and written into a separate error report file for later inspection.

In the final step, a summary report is generated. This report contains the total number of records read, the number of successfully processed records, and the number of skipped records. This provides a high-level view of the batch execution outcome.

## Error Report Feature

The system includes an error reporting mechanism for invalid input data. Any record that fails during reading or processing is skipped and recorded in an error report file. This report is intended for future analysis and debugging, allowing problematic input rows to be identified without rerunning the entire job blindly.

The error report focuses on operational visibility rather than stopping the batch execution, ensuring that valid payroll data is still processed even when some input rows are incorrect.

## Known Limitation

At the current stage of development, validation for empty string values has not been fully implemented. Fields that are empty but syntactically valid may not be detected as invalid during the read phase. This is a known limitation and is planned to be addressed in a future enhancement by adding explicit validation logic.

## Domain Model Summary

The input payroll data represents individual employee payroll information such as employee ID, name, job position, base salary, working days, and bonus. The processed output is stored as monthly payroll data, which includes the calculated monthly salary along with the month and year provided as job parameters.

## Notes

This project is intended as a learning and demonstration system for Spring Batch concepts such as step flow control, item skipping, and reporting. The design favors clarity and traceability over completeness at this stage.
