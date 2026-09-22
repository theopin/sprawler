package com.sprawler.spring.messaging.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "kafka.bootstrap-servers=localhost:9092"
})
class KafkaProducerConfigTest {

    @Autowired
    private ProducerFactory<String, String> producerFactory;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testProducerFactoryBeanCreated() {
        assertNotNull(producerFactory, "ProducerFactory bean should be created");
    }

    @Test
    void testKafkaTemplateBeanCreated() {
        assertNotNull(kafkaTemplate, "KafkaTemplate bean should be created");
    }

    @Test
    void testProducerFactoryConfiguration() {
        assertNotNull(producerFactory, "ProducerFactory should be properly configured");
        assertNotNull(producerFactory.createProducer(), "Producer should be created successfully");
    }

    @Test
    void testKafkaTemplateConfiguration() {
        assertNotNull(kafkaTemplate, "KafkaTemplate should be properly configured");
        assertNotNull(kafkaTemplate.getDefaultTopic(), "KafkaTemplate should have default configuration");
    }

    @Test
    void testKafkaTemplateUsesProducerFactory() {
        assertNotNull(kafkaTemplate, "KafkaTemplate must use ProducerFactory");
        assertEquals(producerFactory, kafkaTemplate.getProducerFactory(), 
            "KafkaTemplate should use the configured ProducerFactory");
    }
}
