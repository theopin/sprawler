package com.sprawler.external.myinfo.dto.request;

public record TokenRequestDTO(
        String code,
        String verifier
) {
}
