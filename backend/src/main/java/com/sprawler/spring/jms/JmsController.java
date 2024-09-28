package com.sprawler.spring.jms;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jms")
public class JmsController {

    private static final Logger LOGGER = LogManager.getLogger(JmsController.class);

    @Autowired
    @Qualifier("mqJmsSender")
    private JmsSender jmsSenderObject;


    @PostMapping
    public JmsResponse sendNewMessage(@RequestBody JmsRequest request) {
        LOGGER.info("Sending message to: " + request.queue());
        jmsSenderObject.sendMessage(request.queue(), request.message());

        return new JmsResponse("sent message");
    }



}
