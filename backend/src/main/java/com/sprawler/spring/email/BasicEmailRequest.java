package com.sprawler.spring.email;

public record BasicEmailRequest(String recipient, String subject, String text) {
}
