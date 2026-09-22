package com.sprawler.spring.messaging.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "kafka.bootstrap-servers=localhost:9092",
    "kafka.group-id=test-group"
})
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaMessageDto testMessage;

    @BeforeEach
    void setUp() {
        testMessage = new KafkaMessageDto();
        testMessage.setId(UUID.randomUUID().toString());
        testMessage.setType("TEST_MESSAGE");
        testMessage.setContent("This is a test message");
        testMessage.setSource("test-source");
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setMetadata("test-metadata");
    }

    @Test
    void testProducerServiceBeanCreated() {
        assertNotNull(producerService, "ProducerService bean should be created");
    }

    @Test
    void testSendMessageToDefaultTopic() {
        assertDoesNotThrow(() -> {
            producerService.sendMessageToDefaultTopic(testMessage);
        }, "sendMessageToDefaultTopic should not throw exception");
    }

    @Test
    void testSendMessageToCustomTopic() {
        assertDoesNotThrow(() -> {
            producerService.sendMessage("test-topic", testMessage);
        }, "sendMessage should not throw exception");
    }

    @Test
    void testMessageWithoutId() {
        KafkaMessageDto messageNoId = new KafkaMessageDto();
        messageNoId.setType("TEST");
        messageNoId.setContent("Test content");
        
        assertDoesNotThrow(() -> {
            producerService.sendMessageToDefaultTopic(messageNoId);
        }, "Should handle message without ID");
    }
}
