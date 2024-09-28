package com.sprawler.spring.email.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class EmailConfig {

    @Autowired
    private Environment env;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private int emailPort;

    @Value("${email.username}")
    private String emailUsername;


    @Value("${email.password}")
    private String emailPassword;

    @Bean("emailSender")
    public JavaMailSender createEmailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();

        addNonNullProperty(props, "mail.transport.protocol");
        addNonNullProperty(props, "mail.smtp.auth");
        addNonNullProperty(props, "mail.smtp.starttls.enable");
        addNonNullProperty(props, "mail.debug");

        return mailSender;
    }

    private void addNonNullProperty(Properties properties, String key) {
        String value = env.getProperty(key);
        if (value != null) {
            properties.put(key, value);
        }
    }
}
