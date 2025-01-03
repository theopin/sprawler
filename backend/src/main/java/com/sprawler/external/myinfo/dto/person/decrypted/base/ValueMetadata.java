package com.sprawler.external.myinfo.dto.person.decrypted.base;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValueMetadata {
    private String value;
    private String classification;
    private String source;
    private String lastupdated;
}
