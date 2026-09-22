package com.sprawler.spring.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaTopicConfig Tests")
class KafkaTopicConfigTest {

    @Test
    @DisplayName("Should create NewTopic with correct properties")
    void testNewTopicCreation() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        config.bootstrapAddress = "localhost:9092";
        config.topicName = "test-topic";
        config.partitions = 3;
        config.replicationFactor = (short) 2;

        NewTopic topic = config.topic1();

        assertNotNull(topic, "NewTopic should be created");
        assertEquals("test-topic", topic.name(), "Topic name should match configuration");
    }

    @Test
    @DisplayName("Should create KafkaAdmin with bootstrap servers")
    void testKafkaAdminCreation() {
        KafkaTopicConfig config = new KafkaTopicConfig();
        config.bootstrapAddress = "localhost:9092";

        assertNotNull(config.kafkaAdmin(), "KafkaAdmin should be created");
    }
}

