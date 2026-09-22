package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaConsumerConfig Tests")
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:0"})
class KafkaConsumerConfigTest {

    @Autowired
    private KafkaConsumerConfig config;

    @Test
    @DisplayName("Should create ConsumerFactory with bootstrap servers and group id")
    void testConsumerFactoryCreation() {
        ConsumerFactory<String, String> factory = config.consumerFactory();

        assertNotNull(factory, "ConsumerFactory should be created");
        assertInstanceOf(DefaultKafkaConsumerFactory.class, factory);
    }

    @Test
    @DisplayName("Should create KafkaListenerContainerFactory with consumer factory")
    void testKafkaListenerContainerFactoryCreation() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            config.kafkaListenerContainerFactory();

        assertNotNull(factory, "ConcurrentKafkaListenerContainerFactory should be created");
        assertNotNull(factory.getConsumerFactory(), "Factory should have consumer factory configured");
    }
}

