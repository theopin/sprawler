package com.sprawler.external.myinfo;

import com.sprawler.external.myinfo.dto.person.decrypted.DecryptedPersonInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController("myInfoController")
@RequestMapping("/myinfo")
public class MyInfoController {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoController.class);

    @Autowired
    @Qualifier("myInfoTemplate")
    RestTemplate myInfoTemplate;

    @GetMapping("/sandbox/person/{uinfin}")
    public DecryptedPersonInfo getPersonDataFromSandbox(@PathVariable("uinfin") String uinFin) {
        LOGGER.info("Running sandbox api to retrieve person data");

        DecryptedPersonInfo personData = myInfoTemplate.getForEntity(
                "https://sandbox.api.myinfo.gov.sg/com/v4/person-sample/" + uinFin,
                DecryptedPersonInfo.class).getBody();

        return personData;
    }

    @GetMapping("/authorize")
    public String makeAuthoriseCall() {
        LOGGER.info("Making api call to authorize data");

        String authApiUrl = "https://test.api.myinfo.gov.sg/com/v4/authorize";
        String clientId = "STG2-MYINFO-SELF-TEST";
        String scope = "uinfin name sex race nationality dob email mobileno regadd housingtype hdbtype marital edulevel noa-basic ownerprivate cpfcontributions cpfbalances";
        String purposeId = "demonstration";
        String codeChallenge = "YMDc6r_HboHPuCed_ctZw9bOPqa_TcO6QJa3AyCknvQ";
        String codeChallengeMethod = "S256";
        String redirectUri = "http://localhost:3001/callback";
        String responseType = "code";


        return UriComponentsBuilder.fromHttpUrl(authApiUrl)
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", responseType)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", codeChallengeMethod)
                .queryParam("purpose_id", purposeId)
                .toUriString();
    }
}
