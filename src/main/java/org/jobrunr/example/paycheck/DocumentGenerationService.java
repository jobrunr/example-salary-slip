package org.jobrunr.example.paycheck;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DocumentGenerationService {

    private static Logger LOGGER = LoggerFactory.getLogger(DocumentGenerationService.class);

    public void generateDocument(URL wordTemplateUrl, Path pdfOutputPath, Object context) throws IOException, Docx4JException {
        Files.createDirectories(pdfOutputPath.getParent().toAbsolutePath());

        try(InputStream template = wordTemplateUrl.openStream(); OutputStream out = Files.newOutputStream(pdfOutputPath)) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final DocxStamper stamper = new DocxStamperConfiguration().setFailOnUnresolvedExpression(true).build();
            stamper.stamp(template, context, byteArrayOutputStream);

            Docx4J.toPDF(WordprocessingMLPackage.load(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())), out);
            LOGGER.info(String.format("Generated salary slip %s", pdfOutputPath)); // for demo purposes only
        }

    }

}
