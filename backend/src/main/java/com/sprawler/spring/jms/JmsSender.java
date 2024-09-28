package com.sprawler.spring.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service("mqJmsSender")
public class JmsSender  {

    @Autowired
    @Qualifier("mqJmsTemplate")
    private JmsTemplate mqJmsTemplate;

    public void sendMessage(String destinationQueueName, String outgoingMessage) {
        this.mqJmsTemplate.convertAndSend(destinationQueueName, outgoingMessage);
    }

}
