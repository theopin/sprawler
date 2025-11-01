package com.sprawler;

import com.sprawler.external.myinfo.config.MyInfoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyInfoProperties.class)
public class SprawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprawlerApplication.class, args);
	}

}
