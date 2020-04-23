package org.jobrunr.example.paycheck;

import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.timeclock.WorkWeek;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SalarySlip {

    private final Employee employee;
    private final WorkWeek workWeek;

    public SalarySlip(Employee employee, WorkWeek workWeek) {
        this.employee = employee;
        this.workWeek = workWeek;
    }

    public Employee getEmployee() {
        return employee;
    }

    public WorkWeek getWorkWeek() {
        return workWeek;
    }

    public BigDecimal getTotal() {
        BigDecimal totalPerHour = getTotalPerHour();
        BigDecimal amountOfWorkedHours = getAmountOfWorkedHours();
        return totalPerHour.multiply(amountOfWorkedHours).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAmountOfWorkedHours() {
        return workWeek.getTotal();
    }

    public BigDecimal getTotalPerHour() {
        return BigDecimal.valueOf(16.50);
    }
}
