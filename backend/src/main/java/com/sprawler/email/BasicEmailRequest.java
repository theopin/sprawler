package com.sprawler.email;

public record BasicEmailRequest(String recipient, String subject, String text) {
}
