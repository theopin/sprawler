package com.sprawler.external.myinfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DPoPUtils;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.interfaces.ECPrivateKey;
import java.util.*;

@Component("myInfoSecurityComponent")
public class MyInfoSecurity {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoSecurity.class);

    public String createCodeChallenge(String codeVerifier) {
        try {
            byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
            MessageDigest sha256messageDigester = MessageDigest.getInstance("SHA-256");
            sha256messageDigester.update(bytes, 0, bytes.length);
            byte[] mdDigest = sha256messageDigester.digest();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(mdDigest);
        } catch(Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    public ECKey generateEphemeralKeys() {
        try {
            return new ECKeyGenerator(Curve.P_256)
                    .keyID( RandomStringUtils.randomAlphanumeric(40))
                    .algorithm(JWSAlgorithm.ES256)
                    .keyUse(KeyUse.SIGNATURE)
                    .generate();
        } catch(Exception e) {
            LOGGER.error(e);
            return null;
        }
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
                uri = new URI(url+"/"+uuid);
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

        } catch(Exception e) {
            LOGGER.error(e);
            return null;
        }

    }

    public String generateClientAssertion(final String url, final String clientId, Base64URL jktThumbprint, String keyId, ECPrivateKey privateSigningKey) {
        Map<String, Object> cnf = new HashMap<>();
        cnf.put("jkt", jktThumbprint.toString());

        try {
            final JWSSigner signer = new ECDSASigner(privateSigningKey, Curve.P_256);
            final SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.ES256)
                            .keyID(keyId)
                            .type(JOSEObjectType.JWT).build(),
                    new JWTClaimsSet.Builder()
                            .issuer(clientId)
                            .subject(clientId)
                            .audience(url)
                            .issueTime(new Date())
                            .expirationTime(new Date(System.currentTimeMillis() + 300000L))
                            .jwtID(new JWTID().getValue())
                            .claim("cnf", cnf).build());

            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    public DecodedJWT decodeJwtToken(String token, String url) {

        // The URL to the JWKS endpoint
        URL jwksUrl;
        JWSObject jwsObj = null;
        DecodedJWT jwt = null;
        try {
            jwksUrl = new URL(url);
            jwsObj = JWSObject.parse(token);

            // Create a new JWK source with rate limiting and refresh ahead. caching, using sensible default settings
            JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwksUrl).build();

            JWKMatcher matcher = new JWKMatcher.Builder().keyIDs(jwsObj.getHeader().getKeyID()).build();

            // Will select keys marked for signature use only
            JWKSelector selector = new JWKSelector(matcher);

            // Get the JWK with the ECC public key
            List<JWK> jwk = jwkSource.get(selector, null);

            // Create a JWS verifier from the JWK set source
            JWSVerifier verifier = new DefaultJWSVerifierFactory().createJWSVerifier(jwsObj.getHeader(),
                    jwk.get(0).toECKey().toECPublicKey());

            boolean flag = jwsObj.verify(verifier);

            if (!flag) {
                return null;
            }

            return JWT.decode(token);
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    public String getPayload(String result, ECPrivateKey privateKey) {
        JWEObject jweObject = null;
        try {
            // Parse JWE & validate headers
            jweObject = EncryptedJWT.parse(result);

            // Set PrivateKey and Decrypt
            JWEDecrypter decrypter = new ECDHDecrypter(privateKey);
            jweObject.decrypt(decrypter);

        } catch (Exception e) {
            LOGGER.error(e);
        }
        // Get String Payload

        return jweObject.getPayload().toString();
    }
}
