package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaProducerConfig Tests")
class KafkaProducerConfigTest {

    @Test
    @DisplayName("Should create ProducerFactory with bootstrap servers")
    void testProducerFactoryCreation() {
        KafkaProducerConfig config = new KafkaProducerConfig();
        config.bootstrapAddress = "localhost:9092";

        ProducerFactory<String, String> factory = config.producerFactory();

        assertNotNull(factory, "ProducerFactory should be created");
        assertInstanceOf(DefaultKafkaProducerFactory.class, factory);
    }

    @Test
    @DisplayName("Should create KafkaTemplate with producer factory")
    void testKafkaTemplateCreation() {
        KafkaProducerConfig config = new KafkaProducerConfig();
        config.bootstrapAddress = "localhost:9092";

        KafkaTemplate<String, String> template = config.kafkaTemplate();

        assertNotNull(template, "KafkaTemplate should be created");
        assertNotNull(template.getProducerFactory(), "KafkaTemplate should have producer factory");
    }
}

