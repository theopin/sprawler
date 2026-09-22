package com.sprawler.spring.messaging.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(String topic, KafkaMessageDto message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, message.getId(), jsonMessage);
            log.info("Message sent to topic {}: {}", topic, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to send message to topic {}", topic, e);
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    public void sendMessageToDefaultTopic(KafkaMessageDto message) {
        sendMessage("sprawler-topic", message);
    }
}
