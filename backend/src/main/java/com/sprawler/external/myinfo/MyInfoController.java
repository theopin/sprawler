package com.sprawler.external.myinfo;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.*;
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
    public ResponseEntity<String> makeAuthorizeCall(@RequestParam("verifier") String verifier) {
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

        // Return the redirect URL as a plain string response
        return ResponseEntity.ok(redirectUrl);
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
    public DecryptedPersonInfo getPersonData(
            @RequestParam("access_token") String accessToken,
            @RequestParam("dpop_string") String dPoPString) {

        LOGGER.info("Running api to retrieve person data");

        LOGGER.info("Decoding provided auth token");
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

        String encryptedResponse = "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiRUNESC1FUytBMjU2S1ciLCJraWQiOiI3VGt5TWFqV0JYUlo3aVpmZ3lQUGZmMmdMMzloMlh0ZkpEemNzNXRjZXJNIiwiZXBrIjp7Imt0eSI6IkVDIiwiY3J2IjoiUC0yNTYiLCJ4IjoibUVaMHhpRmQ4SV9nbERTVDRYWHZaUTgwN2E4X1lpN0ZaYkxmdnhZUEtJZyIsInkiOiJoNEEtOV9tYndJeVQ3UlVnVzNBVV9fQjg2TFYyWUhTWFlZN1l6ekEtX2NjIn19.-tkyW9doM-nvfi5iLIX30livPwFb4Ocpj_sH9lf3EBT_665a4obEiQ.YInOs5ZA_wA0kfAA.ESz3K9C-K7BUtifjT-sLlam7sddzAmpfW0QeF5oDrDYOLJXwgwCYbZCwXP7BBkI0TbccTdatQja76Vs2Y-OWsSDS-AOvg46HWUmUkSnzQAREJ_fxPGhDhDLvmBwLl4Y64zVOHDwZFC9dfnJAH8_2ezM03kqatw1gjs5gmipaicdoGZNA3doy33J6W2uAnjl6eunw47ABlu1PKdizumYnISZjPYWxIGLI3BMSfQSo9arvXT9R7sodhmVMb_KlRG9tnbqazvXwjafRs4B9moT7a2pTNts2hVKZ72Oknl-YpED4pkmx7IoV8CUHU0JIrtAnD_fkZlAl1zUSYtJgy7EXJ11T-UktkBWGzXswQQm913YV0ZYiwBkK80exOoGq-40YTDcFnhAqAJQW3N7Ro2nX4Mk4ujBxtxB-j25rtYmIG1-lU0MPepTzFrTLupzrslkPHyfW8a5LystczDLPH7ryCYUAKw1ex87Wbf784mmkd62QfxLjAhvK3fHh0MA_naOBoIzX89o7B2EjAZ015YJvsIKRPb2JM-Y1_Ll5mFmxttMk17ItjDfcxCqQE9dU_4_X3Z8A6LW0HOSMjrIop-9WvmWKCK3DzpgWw_x4H7dlbTPsOKM7cVnP8_LqNMSmmxjskJeBZ4XMOG9G273RvrZbgA8POS0zESTvkrwULTwPFr4fsfDP8t82WWBzsuwuXXNqE_BE-McfoyZJZ-a6iWbMyp8b3CSrLj5neCYHQSGJ14ZJagfOoz8BgTzqJxUoeQdg6K14bdP2Wdwu32q207al7riw35tFU45WFy8lUuIKURsznadUeyG1GzBJ6Rl7zjVmFmtGReawCbGrFLZDxkHl6520YbDVHqnjHbBX4vnneoISdx3YhSHdd08rHNSOPPOrbpv22bWsLCr-6PKZzzM9XG4J4oloDxwMZgwJ-fmExlCwRml9aywlEArbs0UqLdjC9mtngNb4Sw1kNhUleVzXRD3qkKZj1Zzzei-oHN-LEz9hWVMtAdy-bpuq8eVYt437Td2IQnCMQ-f3hA8FqaASLVlfjrbNm0GwRQpki3s6hA2FlUEXlE3RGyOYB9Z9jyf8AB1WQ9QfWpOPUNKlf8pmqS7f-Ym0EVabTFtTT-QNHXWo5CAQ1RZ7K_sxmiLSiMsE-V6V7DFjq0b7bXPiCFbKAyJW21gC_t_G8Shg7nzXtxKY2YEOCFMOjn-RsnQAWYlJZ9sCpiL3PoueLdK2ouAq9KebqpP-syM8jlwTh6ZrL8k_InYCSZAWvCRdqf85hu4qoKxTFd7biVpeJQ1fQb9sLGHihSMx3_kOOsS-OUctuVy_nElcuFaVy4vGk6bjDetED8egwwQZn3oRtbrQeye0filIYw_hcJyXzwNzGKUox7C8aIV6-Mi4x0fJTQPPJrJyEU4fSJC9auGOi75CWPQOf-Kd8GOpr8vLQ8FULaPB3S1zVfhXN7q08Qcp_MolotPPFfBhn7VMM7ai5JG4vhB-oti3gsih4xaIDiYK98Nitqp_OnY34keiEjzxWL8PQJugTyXiGuM6Gft-Xu07OrOzYC-CqtX7QOUhLSSQefP1HhyvuSAocCvgWQ2Q7EydphmlCJAgzlIwXtePcQ-YKqM_bbJ5TZmD_of4T9-Ab-VwNdgcor_Ro1rq3c6zbzTsKxaJ1SeHRSzIfyfz854TbZd4PiJwERYp4dSt-I-sPW21h90CJXCujS--2QzgVeOkSHlUI94dkl54-ahiKd8UlnU_N24_uVsiVYpqkh2Ouce8uy9FRQAL6ZRh5bOC78GWKQEyMvMDWEZl8myrlSiKWm4OFH65hiBZx6uoNIramaMKQNVZOMFLiTUCOoqJc_Xk-LrNzh7FNdfzP9NgbqfPdwKwVeuy4lWLttYOq2UHXVIU_u4aHEjkUuGVO0O2Fz_Ig_pIyTSIQGkhTC7MQjzWfMTuUCPphh4x7bO74VzqprsN35TxxOVvD9iTpDZ-kg5fJ53j2f5G413q8JmCMHhf3rqX7N8bPTHXlkvHoeCMwc7TMyU4ugJOzxQgMqoO78iWHhr4PxriotwB3vvnvzMsg4fVPRzwUWiD_adBhWB_ZZOnoxQHftb9fmcIP6DGyNne11_4VsuIPmifdSDixBVV6AOk39hkQDbBMQctGPat2TXVo8kl3vg_sgqkTDobccEWT5YnQ3XRpQCrD7V9Z5P9u64_rSeFxz1dBLFHe39sytwFtJT-NzhDAN_5UWyNln5uS-R5WtNARjP5b7W50oQntUql1smZiH3o-xqittFs-QoPeH94D5qtLUpK-XF1fWFJrNaL_GhVBkH0S56fYGfceDs-JZ73AePN5lNbFBVLVuA55QMzBHM_Y8k7zcu6cWIRa15z3UdariVcGXsaurFfTYpP0ABmKtJPbBotEEBWb-N7gvdXuH25MLpEXtqjh6GLXcuRMwIUeQ9mcQKFjh2tJF0jf4kEnMsVs9XtK8pO47owp0TAR1iLre80ryuLcbaSoj_Cm4T5IJORuURKftjfQsoC9NMpPLV8dJrBT0hjv2QgZrQmjyZ1lZwZa1cjVy2_o72epuJsdSgf_aSJ1Pq7ucHnr9WnOmvlwU0IWQ.bjsOSR3XIMI6WMNkJ2gCqg";
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
        LOGGER.info(encryptedResponse);
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

        LOGGER.info(payload);

        DecodedJWT personJWT = myInfoSecurity.decodeJwtToken(payload, jwksUrl);
        LOGGER.info(new String (Base64.getDecoder().decode(personJWT.getHeader())));
        LOGGER.info(new String (Base64.getDecoder().decode(personJWT.getPayload())));

        LOGGER.info("Formatting result");

        // Convert byte[] to String
        byte[] base64Decode = Base64.getDecoder().decode(personJWT.getPayload());
        String decryptedPersonApiResponse = new String(base64Decode);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(decryptedPersonApiResponse, DecryptedPersonInfo.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
