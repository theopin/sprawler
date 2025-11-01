package com.sprawler.external.myinfo.entity.person.decrypted.cpf;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CpfLife {
    private ValueObject cpflifecoverage;
    private ValueObject cpflifeplan;
    private ValueObject cpflifemonthlypayout;
    private ValueObject cpflifepaymentcommencement;
    private ValueObject cpflifepaymentdate;
    private String classification;
    private String source;
    private String lastupdated;
}