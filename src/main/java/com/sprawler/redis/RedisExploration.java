package com.sprawler.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.logging.Logger;

public class RedisExploration {

    private static final Logger LOGGER = Logger.getLogger(RedisExploration.class.getName());



    @Bean("primaryLettuceFactory")
    public LettuceConnectionFactory primaryLettuceConnectionFactory() {
        LOGGER.info("HELLO PRIMARY");
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);

        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);;
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @Bean("secondaryLettuceFactory")
    public LettuceConnectionFactory secondaryLettuceConnectionFactory() {
        LOGGER.info("HELLO SECONDARY");
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.afterPropertiesSet();
        // Additional configuration for secondary connection factory if needed
        return connectionFactory;
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("primaryLettuceFactory") RedisConnectionFactory connectionFactory) {
        LOGGER.info("HELLO REDIS");
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();
        return template;
    }
}
