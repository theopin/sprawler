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

import java.util.Optional;

@RestController("myInfoController")
@RequestMapping("/myinfo")
public class MyInfoController {

    private static final Logger LOGGER = LogManager.getLogger(MyInfoController.class);

    @Autowired
    @Qualifier("myInfoTemplate")
    RestTemplate myInfoTemplate;

    @GetMapping("/person/sandbox/{uinfin}")
    public DecryptedPersonInfo getPersonDataFromSandbox(@PathVariable("uinfin") String uinFin) {
        LOGGER.info("Running sandbox api to retrieve person data");

        DecryptedPersonInfo personData = myInfoTemplate.getForEntity(
                "https://sandbox.api.myinfo.gov.sg/com/v4/person-sample/" + uinFin,
                DecryptedPersonInfo.class).getBody();

        return personData;
    }

}
