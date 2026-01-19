package com.kaung.payroll.batch.processor;

import com.kaung.payroll.batch.domain.MonthlyPayroll;
import com.kaung.payroll.batch.domain.Payroll;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

public class PayrollItemProcessor implements ItemProcessor<Payroll, MonthlyPayroll> {

    private int month;
    private int year;

    public PayrollItemProcessor(int month, int year)
    {
        this.month = month;
        this.year = year;
    }

    @Override
    public @Nullable MonthlyPayroll process(Payroll item) throws Exception
    {
        long dailySalary = item.getBaseSalary() / item.getWorkDays();
        long earnedSalary = dailySalary * item.getWorkDays();
        long monthlySalary = earnedSalary + item.getBonus();
        return new MonthlyPayroll(
                item,
                monthlySalary,
                this.month,
                this.year
        );
    }
}
