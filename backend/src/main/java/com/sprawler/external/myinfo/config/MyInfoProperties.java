package com.sprawler.external.myinfo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "myinfo")
public record MyInfoProperties(
        String sandboxPersonApiUrl,
        String authApiUrl,
        String tokenApiUrl,
        String personApiUrl,
        String clientId,
        String scope,
        String redirectUri,
        String responseType,
        String codeChallengeMethod,
        String purposeId,
        String keyId,
        String grantType,
        String clientAssertionType,
        String jwksUrl
) {
}
