package com.sprawler.redis;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

    private static final Log LOG = LogFactory.getLog(RedisController.class);

    @Autowired
    //@Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping("/get")
    public RedisData obtainValueFromKey(@RequestParam(value = "key", defaultValue = "foo") String key) {
        redisTemplate.opsForValue().set(key, "bar");

        return new RedisData(key, redisTemplate.opsForValue().get(key));

    }
}
