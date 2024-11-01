package com.sprawler.external.myinfo;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
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
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
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
    private MyInfoSecurity myInfoSecurityComponent;

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

        String codeChallenge = myInfoSecurityComponent.createCodeChallenge(verifier);

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

        ECKey sessionPopKeyPair = myInfoSecurityComponent.generateEphemeralKeys();
        String dpopString = myInfoSecurityComponent.generateDPoP(
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
        String clientAssertion = myInfoSecurityComponent
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

        byte[] byteListSessionKey = sessionPopKeyPair.toJSONString().getBytes(StandardCharsets.UTF_8);

        tokenApiResponseObject.setSession_key(Base64.getEncoder().encodeToString(byteListSessionKey));

        return tokenApiResponseObject;
    }

    @GetMapping("/person")
    public Object getPersonData(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("key") String encodedSessionKey) {

        LOGGER.info("Running api to retrieve person data");

        LOGGER.info("Decoding provided auth token");
        String accessToken = authHeader.split(" ")[1];
        String jwksUrl = "https://test.authorise.singpass.gov.sg/.well-known/keys.json";
        DecodedJWT tokenJWT = myInfoSecurityComponent
                .decodeJwtToken(accessToken, jwksUrl);

        LOGGER.info("Building DPoP string");
        AccessToken ath = new DPoPAccessToken(accessToken);
        ECKey sessionPopKeyPair = null;
        try {
            String sessionKey = new String(Base64.getDecoder().decode(encodedSessionKey), StandardCharsets.UTF_8);
            sessionPopKeyPair =(ECKey) JWK.parse(sessionKey);
        } catch (ParseException e) {
            LOGGER.error(e);
        }
        String dpopString = myInfoSecurityComponent
                .generateDPoP(personApiUrl,
                        "GET",
                        sessionPopKeyPair,
                        ath,
                        tokenJWT.getSubject());












        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DPoP " + accessToken);
        headers.set("DPoP", dpopString);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        // Build URI with path parameter
        String personUri = UriComponentsBuilder.fromUriString(personApiUrl)
                .pathSegment(tokenJWT.getSubject())
                .queryParam("scope", scope)
                .toUriString();

        LOGGER.info(personUri);
        LOGGER.info(headers.get("Authorization"));
        LOGGER.info(headers.get("DPoP"));

        return myInfoTemplate.getForEntity(
                personUri,
                Object.class,
                requestEntity).getBody();
    }
}
