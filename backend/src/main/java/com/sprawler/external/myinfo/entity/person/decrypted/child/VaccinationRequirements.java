package com.sprawler.external.myinfo.entity.person.decrypted.child;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VaccinationRequirements {
    private CodeObject requirement;
    private ValueObject fulfilled;
}