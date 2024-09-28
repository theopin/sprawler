package com.sprawler.spring.email;

public record AttachmentEmailRequest(String recipient,
                                     String subject,
                                     String text,
                                     String attachmentName,
                                     String attachment) {
}
