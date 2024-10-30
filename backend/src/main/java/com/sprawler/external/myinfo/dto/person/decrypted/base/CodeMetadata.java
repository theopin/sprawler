package com.sprawler.external.myinfo.dto.person.decrypted.base;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeMetadata {
    private String code;
    private String desc;
    private String classification;
    private String source;
    private String lastupdated;
}
