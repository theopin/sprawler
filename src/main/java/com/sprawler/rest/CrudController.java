package com.sprawler.rest;

import com.sprawler.redis.RedisController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest")
public class CrudController {

    private static final Logger LOGGER = LogManager.getLogger(CrudController.class);

    private static final String TEMPLATE = "Hello, %s! You have used the %s method!";
    private static int counter = 0;

    @PostMapping
    public CrudResponse postGreeting(@RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.POST + " method triggered");
        counter += 1;
        return new CrudResponse(counter, String.format(TEMPLATE, request.name(), HttpMethod.POST));
    }

    @GetMapping
    public CrudResponse getGreeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        LOGGER.info(HttpMethod.GET + " method triggered");
        counter += 1;
        return new CrudResponse(counter, String.format(TEMPLATE, name, HttpMethod.GET));
    }

    @PutMapping("/{id}")
    public CrudResponse putGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.PUT + " method triggered");
        counter += 1;
        return new CrudResponse(id, String.format(TEMPLATE, request.name(), HttpMethod.PUT));
    }

    @PatchMapping("/{id}")
    public CrudResponse patchGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        LOGGER.info(HttpMethod.PATCH + " method triggered");
        counter += 1;
        return new CrudResponse(id, String.format(TEMPLATE, request.name(), HttpMethod.PATCH));
    }

    @DeleteMapping("/{id}")
    public CrudResponse deleteGreeting(@PathVariable long id) {
        LOGGER.info(HttpMethod.DELETE + " method triggered");
        counter += 1;
        return new CrudResponse(id, String.format(TEMPLATE, "", HttpMethod.DELETE));
    }
}