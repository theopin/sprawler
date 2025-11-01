package com.sprawler.external.myinfo.entity.person.decrypted.contact;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;

public record Mobile (
        ValueObject prefix,
        ValueObject areacode,
        ValueObject nbr,
        String classification,
        String source,
        String lastupdated
) {
}
