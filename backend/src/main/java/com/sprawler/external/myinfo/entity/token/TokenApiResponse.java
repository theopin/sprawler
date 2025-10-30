package com.sprawler.external.myinfo.entity.token;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenApiResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String refresh_token;
    private String client_assertion;
    private String scope;
    private String dpop_string;


}
