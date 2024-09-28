package com.sprawler.spring.jms;

public record JmsRequest(String queue, String message) {
}
