package com.sprawler.spring.validation;


import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring/validation")
public class ValidationController {

    private static final Logger LOGGER = LogManager.getLogger(ValidationController.class);

    @PostMapping("/jakarta")
    public ResponseEntity<Order> testJakartaValidation(@Valid @RequestBody Order order) {
        LOGGER.info("Order validated by jakarta successfully.");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hibernate")
    public ResponseEntity<Order> testHibernateValidation(@Valid @RequestBody HibernateOrder hibernateOrder) {
        LOGGER.info("Order validated by hibernate successfully.");
        return ResponseEntity.ok().build();
    }

}
