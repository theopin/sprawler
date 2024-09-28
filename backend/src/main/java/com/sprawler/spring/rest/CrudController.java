package com.sprawler.spring.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/rest")
public class CrudController {

    public static final String METHOD_TRIGGERED_MESSAGE = " method triggered";
    private static final Logger LOGGER = LogManager.getLogger(CrudController.class);

    private static final String TEMPLATE = "Hello, %s! You have used the %s method!";

    @PostMapping
    public CrudResponse postGreeting(@RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.POST + METHOD_TRIGGERED_MESSAGE);
        return new CrudResponse(0, String.format(TEMPLATE, request.name(), HttpMethod.POST));
    }

    @GetMapping
    public CrudResponse getGreeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        LOGGER.info(HttpMethod.GET + METHOD_TRIGGERED_MESSAGE);
        return new CrudResponse(0, String.format(TEMPLATE, name, HttpMethod.GET));
    }

    @PutMapping("/{id}")
    public CrudResponse putGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.PUT + METHOD_TRIGGERED_MESSAGE);
        return new CrudResponse(id, String.format(TEMPLATE, request.name(), HttpMethod.PUT));
    }

    @PatchMapping("/{id}")
    public CrudResponse patchGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.PATCH + METHOD_TRIGGERED_MESSAGE);
        return new CrudResponse(id, String.format(TEMPLATE, request.name(), HttpMethod.PATCH));
    }

    @DeleteMapping("/{id}")
    public CrudResponse deleteGreeting(@PathVariable long id) {
        LOGGER.info(HttpMethod.DELETE + METHOD_TRIGGERED_MESSAGE);
        return new CrudResponse(id, String.format(TEMPLATE, "", HttpMethod.DELETE));
    }
}