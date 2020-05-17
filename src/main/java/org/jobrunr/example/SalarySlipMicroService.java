package org.jobrunr.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.example.employee.EmployeeRepository;
import org.jobrunr.example.paycheck.SalarySlipService;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.time.DayOfWeek;

@SpringBootApplication
public class SalarySlipMicroService {

    private static Logger LOGGER = LoggerFactory.getLogger(SalarySlipMicroService.class);

    public static void main(String[] args) {
        SpringApplication.run(SalarySlipMicroService.class, args);
    }

    @Bean
    public CommandLineRunner demo(EmployeeRepository repository) {
        return (args) -> {
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
        LOGGER.warn("Received following environment variables: ");
        LOGGER.warn("  - DB_NAME = " + System.getenv("DB_NAME"));
        LOGGER.warn("  - DB_USER = " + System.getenv("DB_USER"));
        LOGGER.warn("  - DB_PASS = " + System.getenv("DB_PASS"));
        LOGGER.warn("  - CLOUD_SQL_INSTANCE = " + System.getenv("CLOUD_SQL_INSTANCE"));

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:postgresql:///%s", System.getenv("DB_NAME")));
        config.setUsername(System.getenv("DB_USER"));
        config.setPassword(System.getenv("DB_PASS"));
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", System.getenv("CLOUD_SQL_INSTANCE"));

        return new HikariDataSource(config);
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
