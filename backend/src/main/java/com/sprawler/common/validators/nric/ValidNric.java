package com.sprawler.common.validators.nric;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NricValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNric {
    String message() default "Invalid NRIC format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}