package com.sprawler.spring.messaging.kafka.controller;

import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import com.sprawler.spring.messaging.kafka.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "kafka.bootstrap-servers=localhost:9092",
    "kafka.group-id=test-group",
    "kafka.topic.name=sprawler-topic",
    "kafka.topic.partitions=1",
    "kafka.topic.replication-factor=1"
})
class KafkaMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/kafka/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("Kafka API is up and running"));
    }

    @Test
    void testSendMessageToDefaultTopic() throws Exception {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId(UUID.randomUUID().toString());
        message.setType("TEST_MESSAGE");
        message.setContent("Test content");
        message.setSource("test-controller");
        message.setTimestamp(LocalDateTime.now());
        message.setMetadata("test");

        mockMvc.perform(post("/api/kafka/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Message sent successfully")));
    }

    @Test
    void testSendMessageToCustomTopic() throws Exception {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setId(UUID.randomUUID().toString());
        message.setType("CUSTOM_MESSAGE");
        message.setContent("Custom topic message");
        message.setSource("test-controller");
        message.setTimestamp(LocalDateTime.now());
        message.setMetadata("custom");

        mockMvc.perform(post("/api/kafka/send/custom-topic")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Message sent to topic")));
    }

    @Test
    void testSendMessageWithoutId() throws Exception {
        KafkaMessageDto message = new KafkaMessageDto();
        message.setType("AUTO_ID_MESSAGE");
        message.setContent("Message without manual ID");
        message.setSource("test-controller");

        mockMvc.perform(post("/api/kafka/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(message)))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Message sent successfully")));
    }

    @Test
    void testSendInvalidMessageReturnsError() throws Exception {
        String invalidJson = "{invalid json}";

        mockMvc.perform(post("/api/kafka/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
            .andExpect(status().is4xxClientError());
    }
}
