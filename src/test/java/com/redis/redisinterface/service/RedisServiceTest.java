package com.redis.redisinterface.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.redisinterface.bean.UserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisService<UserSession> redisService;




    @Test
    void redisSetValue() {
        //redisTemplate.opsForValue().set("Weather_In_Mumbai", "34");
       // Object obj = redisTemplate.opsForValue().get("Weather_In_Mumbai");
        //System.out.println(obj.toString());

//        UserSession userSession = new UserSession();
//        userSession.setId("4");
//        userSession.setOidcSession("session44");
//        userSession.setPortal("portal");
//        userSession.setCreatedAt("2023-01-01");
//        redisService.save(userSession.getId(), userSession);

        UserSession userSession = redisService.findById("4");
        ObjectMapper mapper = new ObjectMapper();
        String jsonUserSession;
        try {
            jsonUserSession = mapper.writeValueAsString(userSession);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize UserSession to JSON", e);
        }

        System.out.println(jsonUserSession);
    }
}