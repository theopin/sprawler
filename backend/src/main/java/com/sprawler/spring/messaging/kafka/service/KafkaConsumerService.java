package com.sprawler.spring.messaging.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "sprawler-topic", groupId = "${kafka.group-id}")
    public void consumeMessage(String message) {
        try {
            log.info("Received message from Kafka: {}", message);
            KafkaMessageDto kafkaMessage = objectMapper.readValue(message, KafkaMessageDto.class);
            
            // Process the message
            log.info("Processed message - ID: {}, Type: {}, Content: {}, Source: {}", 
                kafkaMessage.getId(), 
                kafkaMessage.getType(), 
                kafkaMessage.getContent(), 
                kafkaMessage.getSource());
            
            // Add your business logic here
            // e.g., save to database, trigger workflows, etc.
            
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }
}
