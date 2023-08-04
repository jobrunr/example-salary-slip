package org.jobrunr.example;

import com.github.javafaker.Faker;
import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.employee.EmployeeRepository;
import org.jobrunr.example.paycheck.SalarySlipService;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.DayOfWeek;

@SpringBootApplication
public class SalarySlipMicroService {

    public static void main(String[] args) {
        SpringApplication.run(SalarySlipMicroService.class, args);
    }

    @Bean
    public CommandLineRunner demo(EmployeeRepository repository) {
        final Faker faker = new Faker();
        return (args) -> {
            for(int i = 0; i < 10; i++) {
                repository.save(new Employee(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()));
            }

            BackgroundJob.scheduleRecurrently(
                    "generate-and-send-salary-slip",
                    Cron.weekly(DayOfWeek.SUNDAY, 22),
                    SalarySlipService::generateAndSendSalarySlipToAllEmployees
            );

            Thread.currentThread().join();
        };
    }
}
