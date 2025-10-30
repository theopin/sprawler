package com.sprawler.pdf;


import org.openpdf.text.Document;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    private static final Logger LOGGER = LogManager.getLogger(PdfController.class);


    @GetMapping("/simple")
    public ResponseEntity<String> generateSimplePdf(
            @RequestParam(value = "content", defaultValue = "Hello, OpenPDF!") String content) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            LOGGER.info("Creating simple pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            document.add(new Paragraph(content));
            document.close();

            // Encode to Base64
            String base64Pdf = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return ResponseEntity.ok(base64Pdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating PDF");
        }
    }
}
