package com.sprawler.external.myinfo;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.sprawler.external.myinfo.dto.person.decrypted.DecryptedPersonInfo;
import com.sprawler.external.myinfo.dto.token.TokenApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RestController("myInfoController")
@RequestMapping("/myinfo")
public class MyInfoController {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoController.class);

    private static final String authApiUrl = "https://test.api.myinfo.gov.sg/com/v4/authorize";
    private static final String tokenApiUrl = "https://test.api.myinfo.gov.sg/com/v4/token";
    private static final String personApiUrl = "https://test.api.myinfo.gov.sg/com/v4/person";
    private static final String clientId = "STG2-MYINFO-SELF-TEST";
    private static final String scope = "uinfin name sex race nationality dob email mobileno regadd housingtype hdbtype marital edulevel noa-basic ownerprivate cpfcontributions cpfbalances";


    private static final String redirectUri = "http://localhost:3001/callback";
    private static final String responseType = "code";

    private static final String codeChallengeMethod = "S256";
    private static final String purposeId = "demonstration";

    private static final String grantType = "authorization_code";
    private static final String clientAssertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private static final String keyId = "aQPyZ72NM043E4KEioaHWzixt0owV99gC9kRK388WoQ";

    @Autowired
    @Qualifier("myInfoSecurityComponent")
    private MyInfoSecurity myInfoSecurity;

    @Autowired
    @Qualifier("myInfoTemplate")
    private RestTemplate myInfoTemplate;

    @GetMapping("/sandbox/person/{uinfin}")
    public DecryptedPersonInfo getPersonDataFromSandbox(@PathVariable("uinfin") String uinFin) {
        LOGGER.info("Running sandbox api to retrieve person data");

        return myInfoTemplate.getForEntity(
                "https://sandbox.api.myinfo.gov.sg/com/v4/person-sample/" + uinFin,
                DecryptedPersonInfo.class).getBody();
    }

    @GetMapping("/authorize")
    public RedirectView makeAuthorizeCall(@RequestParam("verifier") String verifier) {
        LOGGER.info("Making api call to authorize data");

        String codeChallenge = myInfoSecurity.createCodeChallenge(verifier);

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(authApiUrl)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", responseType)
                .queryParam("code_challenge_method", codeChallengeMethod)
                .queryParam("purpose_id", purposeId)
                .toUriString();

        return new RedirectView(redirectUrl);
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TokenApiResponse obtainMyInfoAccessToken(
            @RequestBody Map<String, String> userData) {
        LOGGER.info("Making api call to obtain token");

        LOGGER.info("Generating DPoP");

        ECKey sessionPopKeyPair = myInfoSecurity.generateEphemeralKeys();
        String dpopString = myInfoSecurity.generateDPoP(
                tokenApiUrl,
                "POST",
                sessionPopKeyPair,
                null,
                null);

        Base64URL jktThumbprint = null;
        ECPrivateKey privateSigningKey = null;
        try {
            jktThumbprint = sessionPopKeyPair.toPublicJWK().computeThumbprint("SHA-256");

            String privateKeyStr = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgZw4GTT/we1cBHxeRKKcalUTgZOZs9CeOSEY74D5Cs9qgCgYIKoZIzj0DAQehRANCAAQFdRarRnZFEWquVZtbaa4jJs2eP9gHF+U1BMQ6D5CYJL1zuBuMg6NIjlSQM5hFsiGcF8J0pxsTyK2Xu0l9Dyru";

            byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);

            // Step 2: Convert to PKCS8EncodedKeySpec
            PKCS8EncodedKeySpec  keySpec = new PKCS8EncodedKeySpec(keyBytes);

            // Step 3: Generate ECPrivateKey from KeyFactory
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Return as ECPrivateKey
            privateSigningKey = (ECPrivateKey) privateKey;
        } catch (Exception e) {
            LOGGER.error(e);
        }

        assert jktThumbprint != null;

        LOGGER.info("Generating Client Assertion");
        String clientAssertion = myInfoSecurity
                .generateClientAssertion(
                        tokenApiUrl,
                        clientId,
                        jktThumbprint,
                        keyId,
                        privateSigningKey
                );

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("DPoP", dpopString);

        // Set up the form parameters
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("client_assertion", clientAssertion);
        formParams.add("code", userData.get("code"));
        formParams.add("code_verifier", userData.get("verifier"));
        formParams.add("grant_type", grantType);
        formParams.add("redirect_uri", redirectUri);
        formParams.add("client_id", clientId);
        formParams.add("client_assertion_type", clientAssertionType);

        LOGGER.info(formParams);
        LOGGER.info(headers);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formParams, headers);

        TokenApiResponse tokenApiResponseObject = myInfoTemplate
                .postForEntity(tokenApiUrl, requestEntity, TokenApiResponse.class)
                .getBody();


        LOGGER.info("Decoding provided auth token to extract details");
        String jwksUrl = "https://test.authorise.singpass.gov.sg/.well-known/keys.json";
        DecodedJWT tokenJWT = myInfoSecurity
                .decodeJwtToken(tokenApiResponseObject.getAccess_token(), jwksUrl);

        LOGGER.info("Generating dPOP key string to ");
        AccessToken ath = new DPoPAccessToken(tokenApiResponseObject.getAccess_token());
        String dpopPersonString = myInfoSecurity
                .generateDPoP(personApiUrl,
                        "GET",
                        sessionPopKeyPair,
                        ath,
                        tokenJWT.getSubject());

        tokenApiResponseObject.setDpop_string(dpopPersonString);

        return tokenApiResponseObject;
    }

    @GetMapping("/person")
    public String getPersonData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("dpop_string") String dPoPString) {

        LOGGER.info("Running api to retrieve person data");

        LOGGER.info("Decoding provided auth token");
        String accessToken = authHeader.split(" ")[1];
        String jwksUrl = "https://test.authorise.singpass.gov.sg/.well-known/keys.json";

        DecodedJWT tokenJWT = myInfoSecurity
                .decodeJwtToken(accessToken, jwksUrl);

        LOGGER.info("Building Request Headers for Person API");
        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DPoP " + accessToken);
        headers.set("DPoP", dPoPString);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        // Build URI with path parameter
        String personUri = UriComponentsBuilder.fromUriString(personApiUrl)
                .pathSegment(tokenJWT.getSubject())
                .queryParam("scope", scope)
                .toUriString();

        LOGGER.info(personUri);
        LOGGER.info(headers.get("Authorization"));
        LOGGER.info(headers.get("DPoP"));

        String encryptedResponse = "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiRUNESC1FUytBMjU2S1ciLCJraWQiOiI3VGt5TWFqV0JYUlo3aVpmZ3lQUGZmMmdMMzloMlh0ZkpEemNzNXRjZXJNIiwiZXBrIjp7Imt0eSI6IkVDIiwiY3J2IjoiUC0yNTYiLCJ4IjoiMHBwczBTMU5tTlZRaFdBQ1hXTU9JVVN1aVpNSUJqMy1qem5WOVJSSGtzSSIsInkiOiJzc0kyaWZTVEVqRnhWa0tRWUFSLW5yMFFuVVN6b2F4VC1QMUMxNWJsSWtjIn19.FhcMB_6gqrGt898Pq3W1FsssOktF5m3wP6qvIYX31o98-t9dP9q7IQ.6g8XwLlrPQaURfiT.TDi5V136ShNeWxHHvruhaQmCYKo6me28FTvDpuwpOaA2JZV3G1KOTG78grCmS32iygV5CLnuB7pBggs4Qr8xrZoiPyiC-KyW0lN5kv8qpSiWwUsh0XZlGre85_WyfSrpv7khausCRLAJfpCQvrxaNRJCdMr1nr9WvmPDSYG4HoFu_KmaSwGiSO_pRDFkN-WsXjdJqnufIaZRlYD9EAe0xJrqt2jEYwnnemGnsykfjTx5mN6TA9OkKKO39iYp5kboIqZazbLLwHvLJs_JUAaJm6bb6JXnd70VaVknkraA-sExq272qpFJORb1zph8EwOaParJuo59C_V9TZcmVW5kTSIrYI7RONoWn_k.CPCS8Li8UZr14g3DaAfezQ";
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
        ECPrivateKey encryptionPrivateKey = null;
        try {


            byte[] keyBytes = Base64.getDecoder().decode(encryptionPrivateKeyString);

            // Step 2: Convert to PKCS8EncodedKeySpec
            PKCS8EncodedKeySpec  keySpec = new PKCS8EncodedKeySpec(keyBytes);

            // Step 3: Generate ECPrivateKey from KeyFactory
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // Return as ECPrivateKey
            encryptionPrivateKey = (ECPrivateKey) privateKey;
        } catch (Exception e) {
            LOGGER.error(e);
        }

        LOGGER.info("Extracting payload");
        String payload = myInfoSecurity.getPayload(encryptedResponse, encryptionPrivateKey);

        DecodedJWT personJWT = myInfoSecurity.decodeJwtToken(payload, jwksUrl);
        LOGGER.info(new String (Base64.getDecoder().decode(personJWT.getHeader())));
        LOGGER.info(new String (Base64.getDecoder().decode(personJWT.getPayload())));

        LOGGER.info("Formatting result");

        // Convert byte[] to String
        byte[] base64Decode = Base64.getDecoder().decode(personJWT.getPayload());
        String decryptedPersonApiResponse = new String(base64Decode);

        return decryptedPersonApiResponse;
    }
}
