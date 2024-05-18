package com.sprawler.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:application.properties")
public class RedisConfig {

    private static final Logger LOGGER = LogManager.getLogger(RedisConfig.class);


    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;


    @Bean("primaryLettuceFactory")
    public LettuceConnectionFactory primaryLettuceConnectionFactory() {
        LOGGER.info("Setting redis connection factory");
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(
            @Qualifier("primaryLettuceFactory") RedisConnectionFactory connectionFactory) {
        LOGGER.info("Setting redis template based on provided redis connection factory");
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        return template;
    }
}
