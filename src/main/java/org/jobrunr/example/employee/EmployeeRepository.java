package org.jobrunr.example.employee;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.stream.Stream;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Query("select e.id from Employee e")
    Stream<Long> getAllEmployeeIds();

}
