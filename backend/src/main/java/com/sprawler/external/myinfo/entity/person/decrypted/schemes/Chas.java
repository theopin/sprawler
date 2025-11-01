package com.sprawler.external.myinfo.entity.person.decrypted.schemes;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Chas {
    private CodeObject cardtype;
    private CodeObject indicator;
    private ValueObject expirydate;
    private ValueObject issuedate;
    private ValueObject name;
    private String classification;
    private String source;
    private String lastupdated;
}
