package com.sprawler.spring.security;


import com.sprawler.spring.jms.JmsRequest;
import com.sprawler.spring.jms.JmsResponse;
import com.sprawler.spring.jms.JmsSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/security")
public class SecurityController {

    private static final Logger LOGGER = LogManager.getLogger(SecurityController.class);


    @GetMapping("/user")
    public String processUserRequest() {
        LOGGER.info("User URL hit");

        return "This is the User URL";
    }

    @GetMapping("/admin")
    public String processAdminRequest() {
        LOGGER.info("Admin URL hit");

        return "This is the Admin URL";
    }



}
