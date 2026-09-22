package com.sprawler.spring.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "kafka.bootstrap-servers=localhost:9092",
    "kafka.topic.name=test-topic",
    "kafka.topic.partitions=3",
    "kafka.topic.replication-factor=2"
})
class KafkaTopicConfigTest {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired(required = false)
    private NewTopic topic1;

    @Test
    void testKafkaAdminBeanCreated() {
        assertNotNull(kafkaAdmin, "KafkaAdmin bean should be created");
    }

    @Test
    void testNewTopicBeanCreated() {
        assertNotNull(topic1, "NewTopic bean should be created");
    }

    @Test
    void testNewTopicProperties() {
        assertNotNull(topic1, "Topic should not be null");
        assertEquals("test-topic", topic1.name(), "Topic name should match configuration");
    }

    @Test
    void testKafkaAdminConfiguration() {
        assertNotNull(kafkaAdmin, "KafkaAdmin should be properly configured");
    }
}
