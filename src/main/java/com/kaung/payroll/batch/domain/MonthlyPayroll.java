package com.kaung.payroll.batch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record MonthlyPayroll (
    Payroll payroll,
    long monthlySalary,
    int month,
    int year
)
{}
