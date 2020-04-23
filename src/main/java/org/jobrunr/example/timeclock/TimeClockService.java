package org.jobrunr.example.timeclock;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class TimeClockService {

    public WorkWeek getWorkWeekForEmployee(Long employeeId) {
        try {
            //simulate a long-during call
            Thread.sleep(ThreadLocalRandom.current().nextInt(3, 5 + 1) * 1000);
            return new WorkWeek(
                    BigDecimal.valueOf(getRandomHours()),
                    BigDecimal.valueOf(getRandomHours()),
                    BigDecimal.valueOf(getRandomHours()),
                    BigDecimal.valueOf(getRandomHours()),
                    BigDecimal.valueOf(getRandomHours())
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private int getRandomHours() {
        Random r = new Random();
        return r.nextInt((8 - 5) + 1) + 5;
    }

}
