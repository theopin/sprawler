package com.sprawler.external.myinfo.entity.person.decrypted.schemes;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record Chas (
        CodeObject cardtype,
        CodeObject indicator,
        ValueObject expirydate,
        ValueObject issuedate,
        ValueObject name,
        String classification,
        String source,
        String lastupdated
){

}
