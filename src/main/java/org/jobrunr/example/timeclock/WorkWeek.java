package org.jobrunr.example.timeclock;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

import static java.time.LocalDate.now;

public class WorkWeek {

    private final int weekNbr;
    private final BigDecimal workHoursMonday;
    private final BigDecimal workHoursTuesday;
    private final BigDecimal workHoursWednesday;
    private final BigDecimal workHoursThursday;
    private final BigDecimal workHoursFriday;
    private final LocalDate from;
    private final LocalDate to;

    public WorkWeek(BigDecimal workHoursMonday, BigDecimal workHoursTuesday, BigDecimal workHoursWednesday, BigDecimal workHoursThursday, BigDecimal workHoursFriday) {
        this.workHoursMonday = workHoursMonday;
        this.workHoursTuesday = workHoursTuesday;
        this.workHoursWednesday = workHoursWednesday;
        this.workHoursThursday = workHoursThursday;
        this.workHoursFriday = workHoursFriday;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        weekNbr = now().get(weekFields.weekOfWeekBasedYear());
        this.from = now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
        this.to = now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public BigDecimal getWorkHoursMonday() {
        return workHoursMonday;
    }

    public BigDecimal getWorkHoursTuesday() {
        return workHoursTuesday;
    }

    public BigDecimal getWorkHoursWednesday() {
        return workHoursWednesday;
    }

    public BigDecimal getWorkHoursThursday() {
        return workHoursThursday;
    }

    public BigDecimal getWorkHoursFriday() {
        return workHoursFriday;
    }

    public int getWeekNbr() {
        return weekNbr;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public BigDecimal getTotal() {
        return workHoursMonday
                .add(workHoursTuesday)
                .add(workHoursWednesday)
                .add(workHoursThursday)
                .add(workHoursFriday);
    }
}
