package com.learning.transaction.exchange.module.transactions;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionReceiptUtil {

    private final TemplateEngine templateEngine;
    private final JavaMailSender emailSender;

    @Value("${receipt.directory}")
    private String pdfDirectory;
    @Value("${receipt.template-name}")
    private String templateName;
    @Value("$(receipt.email-sender)")
    private String email;

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

    public void sendReceiptToEmail(Transaction transaction){

        try{

            byte[] byteArrayFileObj = createReceipt(transaction);
            File attachmentFile = new File(transaction.getId()+".pdf");
            OutputStream os = new FileOutputStream(attachmentFile);
            os.write(byteArrayFileObj);
            os.close();

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(email);
            helper.setTo(transaction.getCustomerEmail());
            helper.setSubject("Transaction Receipt - " + transaction.getId());
            helper.setText("Here is your receipt for transaction " + transaction.getTrxCode());

            FileSystemResource file = new FileSystemResource(attachmentFile);
            helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);

            emailSender.send(message);
        } catch (Exception e) {
            log.info("Failed send email. Caused by " + e.getMessage());
        }
    }

    public byte[] createReceipt(Transaction transaction){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        Map<String, Object> receiptData = new HashMap<>();
        receiptData.put("companyName","Company Name");
        receiptData.put("companyAddress","Company Address");
        receiptData.put("companyPhoneNumber","Company Phone Number");
        receiptData.put("companyEmail","Company Email");

        receiptData.put("customerName",transaction.getCustomerName());
        receiptData.put("customerEmail",transaction.getCustomerEmail());
        receiptData.put("transactionDate",formattedDate);

        receiptData.put("currency",transaction.getCurrency().getCurrencyCode());
        receiptData.put("type",transaction.getTransactionType());
        receiptData.put("exchangeRate",transaction.getTransactionType().equalsIgnoreCase("buy") ? transaction.getRate().getBuyRate() : transaction.getRate().getSellRate());
        receiptData.put("amount",transaction.getAmount());
        receiptData.put("totalAmount",transaction.getTotalIdr());
        receiptData.put("total",transaction.getTotalIdr());

        return generatePdf(receiptData);

    }
}
