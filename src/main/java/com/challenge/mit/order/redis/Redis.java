package com.challenge.mit.order.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class Redis {

    private final RedisTemplate<String, String> redisTemplate;

    public Redis(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public void addKeys(List<String> keys) {

        for (String redisKey : keys) {
            redisTemplate.opsForValue().set(redisKey, "processado", 24, TimeUnit.HOURS);
        }
    }
}
