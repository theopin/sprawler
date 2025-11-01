package com.sprawler.external.myinfo.entity.person.decrypted.base;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ValueMetadata {
    private String value;
    private String classification;
    private String source;
    private String lastupdated;
}
