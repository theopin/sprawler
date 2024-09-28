package com.sprawler.spring.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service("mqJmsReceiver")
public class JmsReceiver {

    @JmsListener(destination = "myDestination")
    public void sampleJmsListenerMethod(String incomingMessage) {
        System.out.println(incomingMessage);
    }
    
}
