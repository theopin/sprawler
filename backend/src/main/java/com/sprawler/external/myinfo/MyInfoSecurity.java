package com.sprawler.external.myinfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DPoPUtils;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.*;

@Component("myInfoSecurityComponent")
public class MyInfoSecurity {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoSecurity.class);


    public ECPrivateKey getEcPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);

        // Step 2: Convert to PKCS8EncodedKeySpec
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        // Step 3: Generate ECPrivateKey from KeyFactory
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Return as ECPrivateKey
        return (ECPrivateKey) privateKey;
    }

    public String generateDPoP(String url, String method, ECKey sessionPopKeyPair, AccessToken ath, String uuid)  {
        try {
            Date iat = new Date();
            JWTID jti = new JWTID(40);
            URI uri = new URI(url);

            // 2 minutes in milliseconds
            long twoMinutesInMillis = 2 * 60 * 1000;
            Date exp = new Date(iat.getTime() + twoMinutesInMillis);

            JOSEObjectType TYPE = new JOSEObjectType("dpop+jwt");

            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .type(TYPE)
                    .jwk(((JWK) sessionPopKeyPair).toPublicJWK())
                    .build();

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
                    .jwtID(jti.getValue())
                    .claim("htm", method)
                    .issueTime(iat)
                    .expirationTime(exp);

            if (ath != null) {
                uri = new URI(url + "/" + uuid);
                builder = builder.claim("ath", DPoPUtils.computeSHA256(ath).toString())
                        .claim("htu", uri.toString());
            } else {
                builder = builder.claim("htu", uri.toString());
            }

            JWTClaimsSet jwtClaimsSet = builder.build();

            DefaultJWSSignerFactory factory = new DefaultJWSSignerFactory();
            JWSSigner jwsSigner = factory.createJWSSigner(sessionPopKeyPair, JWSAlgorithm.ES256);


            SignedJWT proof = new SignedJWT(jwsHeader, jwtClaimsSet);
            proof.sign(jwsSigner);

            return proof.serialize();

        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }

    }

    public DecodedJWT decodeJwtToken(String token, String url) throws MalformedURLException, ParseException, JOSEException {
        // The URL to the JWKS endpoint
        URL jwksUrl = UriComponentsBuilder
                .fromUriString(url)
                .build()
                .toUri()
                .toURL();
        JWSObject jwsObj = JWSObject.parse(token);

        // Create a new JWK source with rate limiting and refresh ahead. caching, using sensible default settings
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwksUrl).build();

        JWKMatcher matcher = new JWKMatcher.Builder().keyIDs(jwsObj.getHeader().getKeyID()).build();

        // Will select keys marked for signature use only
        JWKSelector selector = new JWKSelector(matcher);

        // Get the JWK with the ECC public key
        List<JWK> jwk = jwkSource.get(selector, null);

        // Create a JWS verifier from the JWK set source
        JWSVerifier verifier = new DefaultJWSVerifierFactory().createJWSVerifier(jwsObj.getHeader(),
                jwk.getFirst().toECKey().toECPublicKey());

        boolean flag = jwsObj.verify(verifier);

        if (!flag) {
            return null;
        }

        return JWT.decode(token);
    }
}
