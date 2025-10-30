package com.sprawler.external.myinfo;

import com.sprawler.external.myinfo.dto.person.decrypted.DecryptedPersonInfo;
import com.sprawler.external.myinfo.dto.token.TokenApiResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("myInfoController")
@RequestMapping("/myinfo")
public class MyInfoController {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoController.class);

    @Autowired
    private MyInfoService myInfoService;

    @GetMapping("/sandbox/person/{uinfin}")
    public DecryptedPersonInfo getPersonDataFromSandbox(@PathVariable("uinfin") String uinFin) {
        LOGGER.info("Running sandbox api to retrieve person data");

        return myInfoService.getSandboxPersonData(uinFin);
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> makeAuthorizeCall(@RequestParam("verifier") String verifier) {
        LOGGER.info("Making api call to authorize data");

        String redirectUrl = myInfoService.createAuthRedirectUrl(verifier);

        // Return the redirect URL as a plain string response
        return ResponseEntity.ok(redirectUrl);
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TokenApiResponse obtainMyInfoAccessToken(
            @RequestBody Map<String, String> userData) {
        return myInfoService.retrieveAccessToken(userData);
    }

    @GetMapping("/person")
    public DecryptedPersonInfo getPersonData(
            @RequestParam("access_token") String accessToken,
            @RequestParam("dpop_string") String dpopString) {

        return myInfoService.getPersonDataset(accessToken, dpopString);
    }
}
