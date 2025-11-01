package com.sprawler.external.myinfo.entity.person.decrypted.cpf;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueMetadata;

public record CpfBalances (
        ValueMetadata ma,
        ValueMetadata oa,
        ValueMetadata sa,
        ValueMetadata ra
) {

}