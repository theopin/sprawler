package com.sprawler.external.myinfo.entity.person.decrypted.cpf;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record CpfMonthlyPayouts (
        ValueObject monthlypayout,
        ValueObject paymentcommencement,
        String classification,
        String source,
        String lastupdated
) {

}