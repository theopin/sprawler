package com.sprawler.external.myinfo.entity.person.decrypted.schemes;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record GenerationPackage (
         ValueObject eligibility,
         ValueObject quantum,
         CodeObject message,
         String classification,
         String source,
         String lastupdated
) {

}
