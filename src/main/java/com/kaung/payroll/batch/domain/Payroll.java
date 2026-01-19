package com.kaung.payroll.batch.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payroll {

    String employeeId;
    String name;
    String jobPosition;
    long baseSalary;
    int workDays;
    long bonus;
}
