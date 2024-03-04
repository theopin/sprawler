package com.sprawler.redis;

import com.sprawler.rest.CrudRequest;
import com.sprawler.rest.CrudResponse;
import com.sprawler.rest.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis")
public class RedisController {


    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;


    @PostMapping
    public RedisResponse createNewKeyValue(@RequestBody RedisRequest request) {
        redisTemplate.opsForValue().set(request.key(), request.value());

        return new RedisResponse(request.key(), redisTemplate.opsForValue().get(request.key()));
    }

    @GetMapping("/{key}")
    public RedisResponse obtainKeyValue(@PathVariable String key) {
        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }

    @PutMapping("/{key}")
    public RedisResponse updateValueOfKey(@PathVariable String key,@RequestBody RedisRequest request) {
        redisTemplate.opsForValue().setIfPresent(key, request.value());

        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }

    @DeleteMapping("/{key}")
    public RedisResponse deleteKey(@PathVariable String key) {
        redisTemplate.delete(key);

        return new RedisResponse(key, redisTemplate.opsForValue().get(key));
    }


}
