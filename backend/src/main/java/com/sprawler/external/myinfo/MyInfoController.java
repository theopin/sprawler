package com.sprawler.external.myinfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.sprawler.common.validators.nric.ValidNric;
import com.sprawler.external.myinfo.dto.request.TokenRequestDTO;
import com.sprawler.external.myinfo.entity.person.decrypted.DecryptedPersonInfo;
import com.sprawler.external.myinfo.entity.token.TokenApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

@RestController("myInfoController")
@RequestMapping("/myinfo")
public class MyInfoController {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoController.class);

    @Autowired
    private MyInfoService myInfoService;

    @GetMapping("/sandbox/person/{uinfin}")
    public DecryptedPersonInfo getPersonDataFromSandbox(@PathVariable("uinfin") @Valid @ValidNric String uinFin) {
        LOGGER.info("Running sandbox api to retrieve person data");

        return myInfoService.getSandboxPersonData(uinFin);
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> makeAuthorizeCall(@RequestParam("verifier") String verifier) throws NoSuchAlgorithmException {
        LOGGER.info("Making api call to authorize data");

        String redirectUrl = myInfoService.createAuthRedirectUrl(verifier);

        // Return the redirect URL as a plain string response
        return ResponseEntity.ok(redirectUrl);
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TokenApiResponse obtainMyInfoAccessToken(
            @RequestBody TokenRequestDTO tokenRequestDTO) throws NoSuchAlgorithmException, InvalidKeySpecException, JOSEException, MalformedURLException, ParseException {
        return myInfoService.retrieveAccessToken(tokenRequestDTO);
    }

    @GetMapping("/person")
    public DecryptedPersonInfo getPersonData(
            @RequestParam("access_token") String accessToken,
            @RequestParam("dpop_string") String dpopString) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException, MalformedURLException, ParseException, JOSEException {

        return myInfoService.getPersonDataset(accessToken, dpopString);
    }
}
