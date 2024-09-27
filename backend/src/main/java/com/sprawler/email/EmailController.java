package com.sprawler.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@RestController
@RequestMapping("/email")
@PropertySource("classpath:application.properties")
public class EmailController {

    @Autowired
    @Qualifier("emailSender")
    private JavaMailSender emailSender;

    @Value("${email.senderEmail}")
    private String senderEmail;

    private static final Logger LOGGER = LogManager.getLogger(EmailController.class);

    @PostMapping("/basic")
    public String sendBasicEmail(
            @RequestBody BasicEmailRequest request) {

        SimpleMailMessage basicMessage = new SimpleMailMessage();

        basicMessage.setFrom(senderEmail);
        basicMessage.setTo(request.recipient());
        basicMessage.setSubject(request.subject());
        basicMessage.setText(request.text());

        LOGGER.info("Basic email prepared. Sending to recipient");
        emailSender.send(basicMessage);

        return "Basic Email Sent";
    }

    @PostMapping("/attachment")
    public String sendAttachmentEmail(
            @RequestBody AttachmentEmailRequest request) {

        MimeMessage attachmentMessage = emailSender.createMimeMessage();
        File tempFile = null;

        try {
            MimeMessageHelper attachmentMessageHelper = new MimeMessageHelper(attachmentMessage, true);

            attachmentMessageHelper.setFrom(senderEmail);
            attachmentMessageHelper.setTo(request.recipient());
            attachmentMessageHelper.setSubject(request.subject());
            attachmentMessageHelper.setText(request.text());

            byte[] decodedBytes = Base64.getDecoder().decode(request.attachment());
            Path tempFilePath = Files.createTempFile("", request.attachmentName());
            Files.write(tempFilePath, decodedBytes);  // Write the decoded bytes to the temp file
            tempFile  = tempFilePath.toFile(); // Convert Path to File

            attachmentMessageHelper.addAttachment(request.attachmentName(), tempFile);



            LOGGER.info("Attachment email prepared. Sending to recipient");
            emailSender.send(attachmentMessage);
        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to send attachment message",e);
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

        return "Attachment Email Sent";
    }

}