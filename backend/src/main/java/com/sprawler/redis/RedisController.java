package com.sprawler.redis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private static final Logger LOGGER = LogManager.getLogger(RedisController.class);

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;


    @PostMapping
    public RedisResponse createNewKeyValue(@RequestBody RedisRequest request) {
        LOGGER.info("Creating key-value pair from redis: " + request.key());
        redisTemplate.opsForValue().set(request.key(), request.value());

        return new RedisResponse(request.key(), redisTemplate.opsForValue().get(request.key()));
    }

    @GetMapping("/{key}")
    public RedisResponse obtainKeyValue(@PathVariable String key) {
        LOGGER.info("Retrieving following key-value pair from redis: " + key);
        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }

    @PutMapping("/{key}")
    public RedisResponse updateValueOfKey(@PathVariable String key,@RequestBody RedisRequest request) {
        LOGGER.info("Updating following key-value pair in redis: " + key);
        redisTemplate.opsForValue().setIfPresent(key, request.value());

        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }

    @DeleteMapping("/{key}")
    public RedisResponse deleteKey(@PathVariable String key) {
        LOGGER.info("Deleting following key from redis: " + key);

        redisTemplate.delete(key);

        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }


}
