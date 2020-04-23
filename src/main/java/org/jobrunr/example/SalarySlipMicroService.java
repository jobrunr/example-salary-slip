package org.jobrunr.example;

import com.github.javafaker.Faker;
import org.h2.jdbcx.JdbcDataSource;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.employee.EmployeeRepository;
import org.jobrunr.example.paycheck.SalarySlipService;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.nio.file.Paths;
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
            for(int i = 0; i < 1000; i++) {
                repository.save(new Employee(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()));
            }

            BackgroundJob.scheduleRecurringly(
                    "generate-and-send-salary-slip",
                    SalarySlipService::generateAndSendSalarySlipToAllEmployees,
                    Cron.weekly(DayOfWeek.SUNDAY, 22)
            );

            Thread.currentThread().join();
        };
    }

    @Bean
    public DataSource dataSource() {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:" + Paths.get(System.getProperty("java.io.tmpdir"), "paycheck"));
        ds.setUser("sa");
        ds.setPassword("sa");
        return ds;
    }

    @Bean
    public JobScheduler initJobRunr(ApplicationContext applicationContext) {
        return JobRunr.configure()
                .useStorageProvider(SqlStorageProviderFactory
                        .using(applicationContext.getBean(DataSource.class)))
                .useJobActivator(applicationContext::getBean)
                .useDefaultBackgroundJobServer()
                .useDashboard()
                .initialize();
    }

}
