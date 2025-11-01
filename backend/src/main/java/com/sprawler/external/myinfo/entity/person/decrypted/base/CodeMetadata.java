package com.sprawler.external.myinfo.entity.person.decrypted.base;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CodeMetadata {
    private String code;
    private String desc;
    private String classification;
    private String source;
    private String lastupdated;
}
