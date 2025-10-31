package com.sprawler.external.myinfo;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.sprawler.external.myinfo.config.MyInfoProperties;
import com.sprawler.external.myinfo.dto.request.TokenRequestDTO;
import com.sprawler.external.myinfo.entity.person.decrypted.DecryptedPersonInfo;
import com.sprawler.external.myinfo.entity.token.TokenApiResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;

@Service
public class MyInfoService {

    private final MyInfoProperties myInfoProperties;

    @Autowired
    @Qualifier("myInfoSecurityComponent")
    private MyInfoSecurity myInfoSecurity;

    @Autowired
    @Qualifier("myInfoTemplate")
    private RestTemplate myInfoTemplate;

    // Constructor injection (Spring Boot will auto-wire it)
    public MyInfoService(MyInfoProperties myInfoProperties) {
        this.myInfoProperties = myInfoProperties;
    }

    private static final Logger LOGGER = LogManager.getLogger(MyInfoService.class);


    public DecryptedPersonInfo getSandboxPersonData(String uinFin) {
        return myInfoTemplate.getForEntity(
                myInfoProperties.sandboxPersonApiUrl() + uinFin,
                DecryptedPersonInfo.class).getBody();
    }

    public String createAuthRedirectUrl(String verifier) throws NoSuchAlgorithmException {
        String codeChallenge = createCodeChallenge(verifier);

        return UriComponentsBuilder
                .fromUriString(myInfoProperties.authApiUrl())
                .queryParam("code_challenge", codeChallenge)
                .queryParam("client_id", myInfoProperties.clientId())
                .queryParam("scope", myInfoProperties.scope())
                .queryParam("redirect_uri", myInfoProperties.redirectUri())
                .queryParam("response_type", myInfoProperties.responseType())
                .queryParam("code_challenge_method", myInfoProperties.codeChallengeMethod())
                .queryParam("purpose_id", myInfoProperties.purposeId())
                .toUriString();
    }

    private String createCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest sha256messageDigester = MessageDigest.getInstance("SHA-256");
        sha256messageDigester.update(bytes, 0, bytes.length);
        byte[] mdDigest = sha256messageDigester.digest();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(mdDigest);
    }

    public TokenApiResponse retrieveAccessToken(TokenRequestDTO tokenRequestDTO) throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException, MalformedURLException, ParseException {
        LOGGER.info("Making api call to obtain token");

        LOGGER.info("Generating DPoP");

        ECKey sessionPopKeyPair = generateEphemeralKeys();
        String dpopString = myInfoSecurity.generateDPoP(
                myInfoProperties.tokenApiUrl(),
                "POST",
                sessionPopKeyPair,
                null,
                null);

        Base64URL jktThumbprint = sessionPopKeyPair.toPublicJWK().computeThumbprint("SHA-256");
        String privateKeyStr = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgZw4GTT/we1cBHxeRKKcalUTgZOZs9CeOSEY74D5Cs9qgCgYIKoZIzj0DAQehRANCAAQFdRarRnZFEWquVZtbaa4jJs2eP9gHF+U1BMQ6D5CYJL1zuBuMg6NIjlSQM5hFsiGcF8J0pxsTyK2Xu0l9Dyru";

        ECPrivateKey privateSigningKey = myInfoSecurity.getEcPrivateKey(privateKeyStr);

        assert jktThumbprint != null;

        String clientAssertion = generateClientAssertion(
                myInfoProperties.tokenApiUrl(),
                myInfoProperties.clientId(),
                jktThumbprint,
                myInfoProperties.keyId(),
                privateSigningKey
        );

        HttpEntity<MultiValueMap<String, String>> requestEntity = generateTokenRequestEntity(tokenRequestDTO, dpopString, clientAssertion);

        TokenApiResponse tokenApiResponseObject = myInfoTemplate
                .postForEntity(myInfoProperties.tokenApiUrl(), requestEntity, TokenApiResponse.class)
                .getBody();

        assert tokenApiResponseObject != null;

        LOGGER.info("Decoding provided auth token to extract details");
        return craftTokenApiResponse(tokenApiResponseObject, sessionPopKeyPair);
    }

    private ECKey generateEphemeralKeys() throws JOSEException {
        return new ECKeyGenerator(Curve.P_256)
                .keyID(RandomStringUtils.secure().next(40))
                .algorithm(JWSAlgorithm.ES256)
                .keyUse(KeyUse.SIGNATURE)
                .generate();
    }

    private String generateClientAssertion(final String url, final String clientId, Base64URL jktThumbprint, String keyId, ECPrivateKey privateSigningKey) throws JOSEException {
        Map<String, Object> cnf = new HashMap<>();
        cnf.put("jkt", jktThumbprint.toString());

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
    }


    private HttpEntity<MultiValueMap<String, String>> generateTokenRequestEntity(TokenRequestDTO tokenRequestDTO, String dpopString, String clientAssertion) {
        LOGGER.info("Generating Client Assertion");

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("DPoP", dpopString);

        // Set up the form parameters
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("client_assertion", clientAssertion);
        formParams.add("code", tokenRequestDTO.code());
        formParams.add("code_verifier", tokenRequestDTO.verifier());
        formParams.add("grant_type", myInfoProperties.grantType());
        formParams.add("redirect_uri", myInfoProperties.redirectUri());
        formParams.add("client_id", myInfoProperties.clientId());
        formParams.add("client_assertion_type", myInfoProperties.clientAssertionType());

        LOGGER.info(formParams);
        LOGGER.info(headers);

        return new HttpEntity<>(formParams, headers);
    }

    private TokenApiResponse craftTokenApiResponse(TokenApiResponse tokenApiResponseObject, ECKey sessionPopKeyPair) throws MalformedURLException, ParseException, JOSEException {
        DecodedJWT tokenJWT = myInfoSecurity
                .decodeJwtToken(tokenApiResponseObject.getAccess_token(), myInfoProperties.jwksUrl());

        LOGGER.info("Generating dPOP key string to ");
        AccessToken ath = new DPoPAccessToken(tokenApiResponseObject.getAccess_token());
        String dpopPersonString = myInfoSecurity
                .generateDPoP(myInfoProperties.personApiUrl(),
                        "GET",
                        sessionPopKeyPair,
                        ath,
                        tokenJWT.getSubject());

        tokenApiResponseObject.setDpop_string(dpopPersonString);

        return tokenApiResponseObject;
    }

    public DecryptedPersonInfo getPersonDataset(String accessToken, String dpopString) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException, MalformedURLException, ParseException, JOSEException {
        LOGGER.info("Running api to retrieve person data");

        LOGGER.info("Decoding provided auth token");

        DecodedJWT tokenJWT = myInfoSecurity
                .decodeJwtToken(accessToken, myInfoProperties.jwksUrl());

        LOGGER.info("Building Request Headers for Person API");
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DPoP " + accessToken);
        headers.set("DPoP", dpopString);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        // Build URI with path parameter
        String personUri = UriComponentsBuilder
                .fromUriString(myInfoProperties.personApiUrl())
                .pathSegment(tokenJWT.getSubject())
                .toUriString() + "?scope=" + myInfoProperties.scope();

        String encryptedResponse = "";
        try {

            encryptedResponse = myInfoTemplate.exchange(
                    personUri,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            ).getBody();
        } catch (Exception e) {
            LOGGER.error(e);
        }

        LOGGER.info("String obtained. Attempting to decrypt");
        String encryptionPrivateKeyString = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgOvpIsduu5J9C4s7G9zqcAvfD78OD0LemB7Voa5nyufagCgYIKoZIzj0DAQehRANCAAQ18jEm50E7X9cPgOeGRUo+ice7HQsbXY7cP5nY1+y4QdhxunsiXBBAVWVKdWJReey1jJQCJskrjecEHHhK5qPf";

        LOGGER.info("Loading Private Key");
        ECPrivateKey encryptionPrivateKey = myInfoSecurity.getEcPrivateKey(encryptionPrivateKeyString);

        LOGGER.info("Extracting payload");
        String payload = getPayload(encryptedResponse, encryptionPrivateKey);

        LOGGER.info(payload);

        DecodedJWT personJWT = myInfoSecurity.decodeJwtToken(payload, myInfoProperties.jwksUrl());

        LOGGER.info("Formatting result");
        // Convert byte[] to String
        byte[] base64Decode = Base64.getDecoder().decode(personJWT.getPayload());
        String decryptedPersonApiResponse = new String(base64Decode);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(decryptedPersonApiResponse, DecryptedPersonInfo.class);
    }

    public String getPayload(String result, ECPrivateKey privateKey) throws ParseException, JOSEException {
        JWEObject jweObject = EncryptedJWT.parse(result);

        // Set PrivateKey and Decrypt
        JWEDecrypter decrypter = new ECDHDecrypter(privateKey);
        jweObject.decrypt(decrypter);
        // Get String Payload
        return jweObject.getPayload().toString();
    }
}
