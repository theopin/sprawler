package com.sprawler.external.myinfo.entity.person.decrypted.child;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record VaccinationRequirements (
     CodeObject requirement,
     ValueObject fulfilled
) {

}