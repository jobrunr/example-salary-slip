package org.jobrunr.example.paycheck;

import org.jobrunr.example.utils.Docx4JStamper;
import org.docx4j.Docx4J;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class DocumentGenerationService {

    private static final Logger LOGGER = new JobRunrDashboardLogger(LoggerFactory.getLogger(DocumentGenerationService.class));

    private final String bestMatchingFonts;

    public DocumentGenerationService() {
        bestMatchingFonts = getBestMatchingFonts();
        PhysicalFonts.setRegex(bestMatchingFonts);
    }


    public void generateDocument(Path wordTemplatePath, Path pdfOutputPath, SalarySlip salarySlip) throws Exception {
        if(Files.notExists(pdfOutputPath.getParent().toAbsolutePath())) {
            Files.createDirectories(pdfOutputPath.getParent().toAbsolutePath());
        }

        try(InputStream templateAsInputStream = Files.newInputStream(wordTemplatePath); OutputStream out = Files.newOutputStream(pdfOutputPath)) {
            WordprocessingMLPackage template = WordprocessingMLPackage.load(templateAsInputStream);;
            template.setFontMapper(new BestMatchingMapper());

            Docx4JStamper.searchAndReplace(template, getContext(salarySlip));

            Docx4J.toPDF(template, out);
            LOGGER.info(String.format("Generated salary slip %s", pdfOutputPath)); // for demo purposes only
        }
    }

    private static String getBestMatchingFonts() {
        String fontRegex = ".*(Courier New|Arial|Times New Roman|Comic Sans|Georgia|Impact|Lucida Console|Lucida Sans Unicode|Palatino Linotype|Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*";
        if(System.getProperty("os.name").startsWith("Windows")) {
            fontRegex = ".*(calibri|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
        }
        return fontRegex;
    }

    private Map<String, String> getContext(SalarySlip salarySlip) {
        Map<String, String> context = new HashMap<>();
        context.putAll(getEmployeeDetails(salarySlip));
        context.putAll(getWorkWeekDetails(salarySlip));
        context.putAll(getSalaryTotalDetails(salarySlip));
        return context;
    }

    private Map<String, String> getEmployeeDetails(SalarySlip salarySlip) {
        return Map.of(
                "${employee.id}", salarySlip.getEmployee().getId().toString(),
                "${employee.firstName}", salarySlip.getEmployee().getFirstName(),
                "${employee.lastName}", salarySlip.getEmployee().getLastName());
    }

    private Map<String, String> getWorkWeekDetails(SalarySlip salarySlip) {
        return Map.of(
                "${workWeek.weekNbr}", String.valueOf(salarySlip.getWorkWeek().getWeekNbr()),
                "${workWeek.from}", String.valueOf(salarySlip.getWorkWeek().getFrom()),
                "${workWeek.to}", String.valueOf(salarySlip.getWorkWeek().getTo()),
                "${workWeek.workHoursMonday}", String.valueOf(salarySlip.getWorkWeek().getWorkHoursMonday().setScale(2)),
                "${workWeek.workHoursTuesday}", String.valueOf(salarySlip.getWorkWeek().getWorkHoursTuesday().setScale(2)),
                "${workWeek.workHoursWednesday}", String.valueOf(salarySlip.getWorkWeek().getWorkHoursWednesday().setScale(2)),
                "${workWeek.workHoursThursday}", String.valueOf(salarySlip.getWorkWeek().getWorkHoursThursday().setScale(2)),
                "${workWeek.workHoursFriday}", String.valueOf(salarySlip.getWorkWeek().getWorkHoursFriday().setScale(2)),
                "${workWeek.total}", String.valueOf(salarySlip.getWorkWeek().getTotal().setScale(2)));
    }

    private Map<String, String> getSalaryTotalDetails(SalarySlip salarySlip) {
        return Map.of(
                "${totalPerHour}", String.valueOf(salarySlip.getTotalPerHour().setScale(2)),
                "${total}", String.valueOf(salarySlip.getTotal().setScale(2)));
    }

}
