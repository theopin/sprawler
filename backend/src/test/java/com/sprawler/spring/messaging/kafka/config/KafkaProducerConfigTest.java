package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaProducerConfig Tests")
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:0"})
class KafkaProducerConfigTest {

    @Autowired
    private KafkaProducerConfig config;

    @Test
    @DisplayName("Should create ProducerFactory with bootstrap servers")
    void testProducerFactoryCreation() {
        ProducerFactory<String, String> factory = config.producerFactory();

        assertNotNull(factory, "ProducerFactory should be created");
        assertInstanceOf(DefaultKafkaProducerFactory.class, factory);
    }

    @Test
    @DisplayName("Should create KafkaTemplate with producer factory")
    void testKafkaTemplateCreation() {
        KafkaTemplate<String, String> template = config.kafkaTemplate();

        assertNotNull(template, "KafkaTemplate should be created");
        assertNotNull(template.getProducerFactory(), "KafkaTemplate should have producer factory");
    }
}

