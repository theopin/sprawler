package com.sprawler.external.myinfo.entity.person.decrypted.cpf;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record CpfLife (
    ValueObject cpflifecoverage,
    ValueObject cpflifeplan,
    ValueObject cpflifemonthlypayout,
    ValueObject cpflifepaymentcommencement,
    ValueObject cpflifepaymentdate,
    String classification,
    String source,
    String lastupdated
) {

}