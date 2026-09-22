package com.sprawler.spring.messaging.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaProducerService Tests")
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:0"})
class KafkaProducerServiceTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaProducerService producerService;

    @BeforeEach
    void setUp() {
        producerService = new KafkaProducerService(kafkaTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should send message to default topic")
    void testSendMessageToDefaultTopic() {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId(UUID.randomUUID().toString());
        message.setType("TEST_MESSAGE");
        message.setContent("This is a test message");
        message.setSource("test-source");
        message.setTimestamp(LocalDateTime.now());
        message.setMetadata("test-metadata");

        assertDoesNotThrow(() -> producerService.sendMessageToDefaultTopic(message));
    }

    @Test
    @DisplayName("Should send message to custom topic")
    void testSendMessageToCustomTopic() {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId(UUID.randomUUID().toString());
        message.setType("CUSTOM_MESSAGE");
        message.setContent("Custom topic message");
        message.setSource("test-source");

        assertDoesNotThrow(() -> producerService.sendMessage("custom-topic", message));
    }

    @Test
    @DisplayName("Should throw exception on serialization error")
    void testSendMessageWithSerializationError() {
        KafkaProducerService service = new KafkaProducerService(kafkaTemplate, null);
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId("test-id");

        assertThrows(RuntimeException.class, () -> service.sendMessageToDefaultTopic(message));
    }
}

