package com.learning.transaction.exchange.module.transactions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionReceiptUtil {

    private final TemplateEngine templateEngine;

    @Value("${receipt.directory}")
    private String pdfDirectory;
    @Value("${receipt.template-name}")
    private String templateName;
    public byte[] generatePdf(Map<String, Object> data) {
        // Create Thymeleaf context and set variables
        Context context = new Context();
        context.setVariables(data);

        // Render HTML template
        String htmlContent = templateEngine.process(templateName, context);

        // Generate PDF
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
