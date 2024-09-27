package com.sprawler.email;

public record AttachmentEmailRequest(String recipient,
                                     String subject,
                                     String text,
                                     String attachmentName,
                                     String attachment) {
}
