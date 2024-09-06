package com.sprawler.rest;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String returnHealthCheck() {
        return "System Healthy!";
    }

}