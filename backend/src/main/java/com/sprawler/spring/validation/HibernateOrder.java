package com.sprawler.spring.validation;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;

public record HibernateOrder(

        @CreditCardNumber
        String creditNo,

        @URL
        String orderSourceUrl,

        @Range(min = 1, max = 3)
        String orderCount,

        @Length(min = 5, max = 50)
        String customerName,

        @ISBN(type = ISBN.Type.ISBN_13)
        String isbnCode,

        @Currency(value = {"USD", "EUR"}, message = "Currency must be USD or EUR")
        String currencyType


) {
}
