package com.sprawler.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest")
public class CrudController {

    private static final String template = "Hello, %s! You have used the %s method!";
    private static int counter = 0;

    @PostMapping
    public CrudResponse postGreeting(@RequestBody CrudRequest request) {
        counter += 1;
        return new CrudResponse(counter, String.format(template, request.name(), HttpMethod.POST));
    }

    @GetMapping
    public CrudResponse getGreeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        counter += 1;
        return new CrudResponse(counter, String.format(template, name, HttpMethod.GET));
    }

    @PutMapping("/{id}")
    public CrudResponse putGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        return new CrudResponse(id, String.format(template, request.name(), HttpMethod.PUT));
    }

    @PatchMapping("/{id}")
    public CrudResponse patchGreeting(@PathVariable long id, @RequestBody CrudRequest request) {
        return new CrudResponse(id, String.format(template, request.name(), HttpMethod.PATCH));
    }

    @DeleteMapping("/{id}")
    public CrudResponse deleteGreeting(@PathVariable long id) {
        return new CrudResponse(id, String.format(template, "", HttpMethod.DELETE));
    }
}