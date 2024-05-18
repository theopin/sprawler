package com.sprawler.jms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jms")
public class JmsController {


    @Autowired
    @Qualifier("mqJmsSender")
    private JmsSender jmsSenderObject;


    @PostMapping
    public JmsResponse sendNewMessage(@RequestBody JmsRequest request) {
        jmsSenderObject.sendMessage(request.queue(), request.message());

        return new JmsResponse("sent message");
    }



}
