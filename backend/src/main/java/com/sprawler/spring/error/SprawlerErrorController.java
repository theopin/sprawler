package com.sprawler.spring.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SprawlerErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error() {
        return "Custom Error Response";
    }

}
