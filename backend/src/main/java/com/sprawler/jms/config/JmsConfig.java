package com.sprawler.jms.config;

import com.sprawler.redis.RedisConfig;
import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {
    private static final Logger LOGGER = LogManager.getLogger(JmsConfig.class);

    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";
    private static final String BROKER_USERNAME = "admin";
    private static final String BROKER_PASSWORD = "admin";


    @Bean("mqConnectionFactory")
    public ActiveMQConnectionFactory connectionFactory() {
        LOGGER.info("Setting mq connection factory (ActiveMQ)");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setUserName(BROKER_USERNAME);
        connectionFactory.setPassword(BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean("mqJmsTemplate")
    public JmsTemplate jmsTemplate(
            @Qualifier("mqConnectionFactory") ConnectionFactory mqConnectionFactory) {
        LOGGER.info("Setting jms template based on provided mq connection factory");
        return new JmsTemplate(mqConnectionFactory);
    }

}
