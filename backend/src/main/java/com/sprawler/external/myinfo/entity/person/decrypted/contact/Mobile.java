package com.sprawler.external.myinfo.entity.person.decrypted.contact;

import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Mobile {
    private ValueObject prefix;
    private ValueObject areacode;
    private ValueObject nbr;
    private String classification;
    private String source;
    private String lastupdated;
}
