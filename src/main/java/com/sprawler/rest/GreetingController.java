package com.sprawler.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    private static final String template = "Hello, %s!";
    private static int counter = 0;

    @GetMapping
    public GreetingData greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        counter += 1;

        return new GreetingData(counter, String.format(template, name));
    }
}