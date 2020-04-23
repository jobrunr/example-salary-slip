package org.jobrunr.example.paycheck;

import org.jobrunr.example.email.EmailService;
import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.employee.EmployeeRepository;
import org.jobrunr.example.timeclock.TimeClockService;
import org.jobrunr.example.timeclock.WorkWeek;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.BackgroundJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.LocalDate.now;

@Component
public class SalarySlipService {

    private static final Path salarySlipTemplatePath = Path.of("src/main/resources/templates/salary-slip-template.docx");

    private final EmployeeRepository employeeRepository;
    private final TimeClockService timeClockService;
    private final DocumentGenerationService documentGenerationService;
    private final EmailService emailService;

    public SalarySlipService(EmployeeRepository employeeRepository, TimeClockService timeClockService, DocumentGenerationService documentGenerationService, EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.timeClockService = timeClockService;
        this.documentGenerationService = documentGenerationService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    @Job(name = "Generate and send salary slip to all employees")
    public void generateAndSendSalarySlipToAllEmployees() {
        final Stream<Long> allEmployees = employeeRepository.getAllEmployeeIds();
        BackgroundJob.<SalarySlipService, Long>enqueue(allEmployees, (salarySlipService, employeeId) -> salarySlipService.generateAndSendSalarySlip(employeeId));
    }

    @Job(name = "Generate and send salary slip to employee %0")
    public void generateAndSendSalarySlip(Long employeeId) throws Exception {
        final Employee employee = getEmployee(employeeId);
        Path salarySlipPath = generateSalarySlip(employee);
        emailService.sendSalarySlip(employee, salarySlipPath);
    }

    private Path generateSalarySlip(Employee employee) throws Exception {
        final WorkWeek workWeek = getWorkWeekForEmployee(employee.getId());
        final SalarySlip salarySlip = new SalarySlip(employee, workWeek);
        return generateSalarySlipDocumentUsingTemplate(salarySlip);
    }

    private Path generateSalarySlipDocumentUsingTemplate(SalarySlip salarySlip) throws Exception {
        Path salarySlipPath = Paths.get(System.getProperty("java.io.tmpdir"), String.valueOf(now().getYear()), format("workweek-%d", salarySlip.getWorkWeek().getWeekNbr()), format("salary-slip-employee-%d.pdf", salarySlip.getEmployee().getId()));
        documentGenerationService.generateDocument(salarySlipTemplatePath, salarySlipPath, salarySlip);
        return salarySlipPath;
    }

    private WorkWeek getWorkWeekForEmployee(Long employeeId) {
        return timeClockService.getWorkWeekForEmployee(employeeId);
    }

    private Employee getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException(format("Employee with id '%d' does not exist", employeeId)));
    }

}
