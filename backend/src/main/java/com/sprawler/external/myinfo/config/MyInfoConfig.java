package com.sprawler.external.myinfo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyInfoConfig {

    @Bean("myInfoTemplate")
    public RestTemplate createMyInfoRestTemplate() {
        return new RestTemplate();
    }
}
