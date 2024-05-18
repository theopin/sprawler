package com.sprawler.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service("mqJmsReceiver")
public class JmsReceiver {

    @JmsListener(destination = "myDestination")
    public void SampleJmsListenerMethod(String incomingMessage) {
        System.out.println(incomingMessage);
    }
    
}
