package com.sprawler.health;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String returnHealthCheck() {
        return "System Healthy!";
    }

}