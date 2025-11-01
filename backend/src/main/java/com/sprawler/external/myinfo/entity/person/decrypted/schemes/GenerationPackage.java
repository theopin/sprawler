package com.sprawler.external.myinfo.entity.person.decrypted.schemes;

import com.sprawler.external.myinfo.entity.person.decrypted.base.CodeObject;
import com.sprawler.external.myinfo.entity.person.decrypted.base.ValueObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenerationPackage {
    private ValueObject eligibility;
    private ValueObject quantum;
    private CodeObject message;
    private String classification;
    private String source;
    private String lastupdated;
}
