package com.sprawler.spring.messaging.jms;

public record JmsRequest(String queue, String message) {
}
