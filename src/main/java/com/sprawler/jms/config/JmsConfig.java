package com.sprawler.jms.config;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JmsConfig {

    private static final String BROKER_URL = "vm://localhost?broker.persistent=false";
    private static final String BROKER_USERNAME = "admin";
    private static final String BROKER_PASSWORD = "admin";


    @Bean("mqConnectionFactory")
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setUserName(BROKER_USERNAME);
        connectionFactory.setPassword(BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean("mqJmsTemplate")
    public JmsTemplate jmsTemplate(
            @Qualifier("mqConnectionFactory") ConnectionFactory mqConnectionFactory) {
        return new JmsTemplate(mqConnectionFactory);
    }

}
