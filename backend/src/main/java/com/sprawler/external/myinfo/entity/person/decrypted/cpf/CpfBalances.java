package com.sprawler.external.myinfo.entity.person.decrypted.cpf;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueMetadata;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CpfBalances {
    private ValueMetadata ma;
    private ValueMetadata oa;
    private ValueMetadata sa;
    private ValueMetadata ra;
}