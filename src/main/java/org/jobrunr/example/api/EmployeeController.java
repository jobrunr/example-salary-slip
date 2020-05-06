package org.jobrunr.example.api;

import com.github.javafaker.Faker;
import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class EmployeeController {

    private static Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/get-employee-count")
    public SimpleResponse getEmployeeCount() {
        return new SimpleResponse(String.format("%d employees in the database", repository.count()));
    }

    @GetMapping("/create-employees")
    @Transactional
    public SimpleResponse createEmployees(@RequestParam Integer amount) {
        final Collection<List<Employee>> employeesPartitioned = createEmployees(amount, 1000);
        Instant startTime = Instant.now();
        for (List<Employee> employeeList: employeesPartitioned) {
            repository.saveAll(employeeList);
            LOGGER.info(String.format("Saved %d employees to DB", employeeList.size()));
        }
        Instant endTime = Instant.now();
        return new SimpleResponse(String.format("%d employees created in %s s", amount, Duration.between(startTime, endTime).getSeconds()));
    }

    private Collection<List<Employee>> createEmployees(Integer amount, int partitionSize) {
        final Faker faker = new Faker();
        final AtomicInteger counter = new AtomicInteger();
        return IntStream.range(0, amount)
                .mapToObj(i -> new Employee(faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()))
                .collect(Collectors.groupingBy(it -> (counter.getAndIncrement() / partitionSize)))
                .values();
    }
}
