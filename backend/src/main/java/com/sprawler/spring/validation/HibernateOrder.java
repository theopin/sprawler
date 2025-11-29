package com.sprawler.spring.validation;

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

        @CodePointLength(min = 1, max = 3)
        String codePointString,

        @ISBN(type = ISBN.Type.ISBN_13, message = "Provided ISBN value is invalid")
        String isbnCode

) {
}
