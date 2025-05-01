package com.redis.redisinterface.redisexception;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthIndicator extends AbstractHealthIndicator {
    
    private final RedisTemplate<String, ?> redisTemplate;
    
    public RedisHealthIndicator(RedisTemplate<String, ?> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            builder.up();
        } catch (Exception e) {
            builder.down().withException(e);
        }
    }
}