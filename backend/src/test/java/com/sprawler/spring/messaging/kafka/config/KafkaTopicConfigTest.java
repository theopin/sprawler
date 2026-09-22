package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaTopicConfig Tests")
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:0"})
class KafkaTopicConfigTest {

    @Autowired
    private KafkaTopicConfig config;

    @Test
    @DisplayName("Should create NewTopic with correct properties")
    void testNewTopicCreation() {
        NewTopic topic = config.topic1();

        assertNotNull(topic, "NewTopic should be created");
        assertEquals("sprawler-topic", topic.name(), "Topic name should match configuration");
    }

    @Test
    @DisplayName("Should create KafkaAdmin with bootstrap servers")
    void testKafkaAdminCreation() {
        assertNotNull(config.kafkaAdmin(), "KafkaAdmin should be created");
    }
}

