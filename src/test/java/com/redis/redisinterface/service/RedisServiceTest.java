package com.redis.redisinterface.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void redisSetValue() {
        //redisTemplate.opsForValue().set("Weather_In_Mumbai", "34");
        Object obj = redisTemplate.opsForValue().get("Weather_In_Mumbai");
        System.out.println(obj.toString());
    }
}