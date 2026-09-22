package com.sprawler.spring.messaging.kafka.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "kafka.bootstrap-servers=localhost:9092",
    "kafka.group-id=test-consumer-group"
})
class KafkaConsumerConfigTest {

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    @Test
    void testConsumerFactoryBeanCreated() {
        assertNotNull(consumerFactory, "ConsumerFactory bean should be created");
    }

    @Test
    void testKafkaListenerContainerFactoryBeanCreated() {
        assertNotNull(kafkaListenerContainerFactory, 
            "ConcurrentKafkaListenerContainerFactory bean should be created");
    }

    @Test
    void testConsumerFactoryConfiguration() {
        assertNotNull(consumerFactory, "ConsumerFactory should be properly configured");
        assertNotNull(consumerFactory.createConsumer(), "Consumer should be created successfully");
    }

    @Test
    void testKafkaListenerContainerFactoryConfiguration() {
        assertNotNull(kafkaListenerContainerFactory, 
            "KafkaListenerContainerFactory should be properly configured");
        assertNotNull(kafkaListenerContainerFactory.getConsumerFactory(), 
            "Container factory should have consumer factory configured");
    }

    @Test
    void testKafkaListenerContainerFactoryUsesConsumerFactory() {
        assertNotNull(kafkaListenerContainerFactory, "Container factory must exist");
        assertEquals(consumerFactory, kafkaListenerContainerFactory.getConsumerFactory(), 
            "Container factory should use the configured ConsumerFactory");
    }

    @Test
    void testEnableKafkaAnnotationActive() {
        assertNotNull(kafkaListenerContainerFactory, 
            "@EnableKafka annotation should activate Kafka listener functionality");
    }
}
