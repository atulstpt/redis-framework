package com.redis.redisinterface.redisexception;

import com.redis.redisinterface.bean.RedisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j

public class RedisGlobalExceptionHandler {

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<RedisResponse> handleRedisException(RedisOperationException ex) {
        log.error("Redis operation failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RedisResponse(false, "Redis operation failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RedisResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RedisResponse(false, "An unexpected error occurred"));
    }

}
