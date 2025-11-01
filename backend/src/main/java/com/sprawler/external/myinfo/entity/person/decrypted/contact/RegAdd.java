package com.sprawler.external.myinfo.entity.person.decrypted.contact;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record RegAdd (
     String type,
     ValueObject block,
     ValueObject building,
     ValueObject floor,
     ValueObject unit,
     ValueObject street,
     ValueObject postal,
     CodeObject country,
     String classification,
     String source,
     String lastupdated
) {

}