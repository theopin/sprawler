package com.sprawler.spring.messaging.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("KafkaProducerService Tests")
class KafkaProducerServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private KafkaProducerService producerService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        objectMapper = new ObjectMapper();
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

        producerService.sendMessageToDefaultTopic(message);

        verify(kafkaTemplate, times(1)).send("sprawler-topic", message.getId(), 
            objectMapper.writeValueAsString(message));
    }

    @Test
    @DisplayName("Should send message to custom topic")
    void testSendMessageToCustomTopic() {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId(UUID.randomUUID().toString());
        message.setType("CUSTOM_MESSAGE");
        message.setContent("Custom topic message");
        message.setSource("test-source");

        producerService.sendMessage("custom-topic", message);

        verify(kafkaTemplate, times(1)).send(eq("custom-topic"), eq(message.getId()), anyString());
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

