package org.jobrunr.example.paycheck;

import com.github.javafaker.Faker;
import org.jobrunr.example.employee.Employee;
import org.jobrunr.example.timeclock.WorkWeek;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

class DocumentGenerationServiceTest {

    private DocumentGenerationService documentGenerationService;
    private Faker faker;

    @BeforeEach
    public void setUpTest() {
        documentGenerationService = new DocumentGenerationService();
        faker = new Faker();
    }

    @Test
    public void generateDocument() throws Exception {
        final SalarySlip salarySlip = new SalarySlip(
                new Employee(1L, faker.name().firstName(), faker.name().lastName(), faker.internet().emailAddress()),
                new WorkWeek(BigDecimal.valueOf(7.35), BigDecimal.valueOf(7), BigDecimal.valueOf(5.5), BigDecimal.valueOf(6.8), BigDecimal.valueOf(8))
        );
        final Path template = Path.of("./src/main/resources/templates/salary-slip-template.docx");
        final Path pdfOutput = Path.of("/tmp/generated-paycheck.pdf");
        documentGenerationService.generateDocument(template, pdfOutput, salarySlip);
    }

}