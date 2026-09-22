package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("KafkaConsumerConfig Tests")
class KafkaConsumerConfigTest {

    @Test
    @DisplayName("Should create ConsumerFactory with bootstrap servers and group id")
    void testConsumerFactoryCreation() {
        KafkaConsumerConfig config = new KafkaConsumerConfig();
        config.bootstrapAddress = "localhost:9092";
        config.groupId = "test-group";

        ConsumerFactory<String, String> factory = config.consumerFactory();

        assertNotNull(factory, "ConsumerFactory should be created");
        assertInstanceOf(DefaultKafkaConsumerFactory.class, factory);
    }

    @Test
    @DisplayName("Should create KafkaListenerContainerFactory with consumer factory")
    void testKafkaListenerContainerFactoryCreation() {
        KafkaConsumerConfig config = new KafkaConsumerConfig();
        config.bootstrapAddress = "localhost:9092";
        config.groupId = "test-group";

        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            config.kafkaListenerContainerFactory();

        assertNotNull(factory, "ConcurrentKafkaListenerContainerFactory should be created");
        assertNotNull(factory.getConsumerFactory(), "Factory should have consumer factory configured");
    }
}

