package com.sprawler.spring.cache;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring/cache")
public class CacheController {

    private static final Logger LOGGER = LogManager.getLogger(CacheController.class);

    @GetMapping("/retrieve")
    @Cacheable("cache")
    public String retrieveCache() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.info("Failed to sleep retrieve");
        }
        return "Cache Returned";
    }

    @GetMapping("/evict")
    @CacheEvict(value = "cache", allEntries = true)
    public String evictCache() {
        return "Cache evicted";
    }

    @GetMapping("/put")
    @CachePut(value = "cache")
    public String putCache() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.info("Failed to sleep put");
        }
        return "Cache Put";
    }

}