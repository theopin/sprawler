package com.sprawler.external.myinfo.entity.person.decrypted.contact;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegAdd {
    private String type;
    private ValueObject block;
    private ValueObject building;
    private ValueObject floor;
    private ValueObject unit;
    private ValueObject street;
    private ValueObject postal;
    private CodeObject country;
    private String classification;
    private String source;
    private String lastupdated;
}