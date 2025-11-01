package com.sprawler.spring.validation;

import jakarta.validation.constraints.*;

import java.util.Date;

public record Order(

        @AssertFalse
        boolean isHibernateOrder,

        @DecimalMin("5.50")
        @DecimalMax("50")
        double price,

        @Min(1)
        @Max(10)
        double quantity,

        @NotEmpty
        String name,

        @Future
        Date futureDate,

        @Null(message = "Rating should be null.")
        String rating

) {
}
