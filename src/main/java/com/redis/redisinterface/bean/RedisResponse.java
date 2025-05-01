package com.redis.redisinterface.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RedisResponse {
    private boolean success;
    private String message;
    private Object data;


    public RedisResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RedisResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }


}
