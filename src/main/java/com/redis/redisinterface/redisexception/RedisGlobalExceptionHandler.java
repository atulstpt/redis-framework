package com.redis.redisinterface.redisexception;

import com.redis.redisinterface.bean.RedisResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j

public class RedisGlobalExceptionHandler {

    @ExceptionHandler(RedisOperationException.class)
    public ResponseEntity<RedisResponse> handleRedisException(RedisOperationException ex) {
        log.error("Redis operation failed", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RedisResponse(false, "Redis operation failed"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RedisResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        // This happens when the request body is missing or cannot be parsed
        log.warn("Request body is missing or unreadable", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RedisResponse(false, "User session cannot be null"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RedisResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for request", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RedisResponse(false, "Validation failed for request"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RedisResponse> handleGenericException(Exception ex) {
        // Robust detection for HttpMessageNotReadableException in wrapped/suppressed cases
        if (looksLikeHttpMessageNotReadable(ex)) {
            log.warn("Request body is missing or unreadable (detected in generic handler)", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RedisResponse(false, "User session cannot be null"));
        }

        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RedisResponse(false, "An unexpected error occurred"));
    }

    private boolean looksLikeHttpMessageNotReadable(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            // direct instanceof check
            if (t instanceof HttpMessageNotReadableException) {
                return true;
            }
            String cname = t.getClass().getName();
            if (cname != null && cname.contains("HttpMessageNotReadableException")) {
                return true;
            }
            String msg = t.getMessage();
            if (msg != null && msg.contains("Required request body is missing")) {
                return true;
            }
            // also inspect suppressed exceptions
            for (Throwable s : t.getSuppressed()) {
                if (s != null) {
                    if (s instanceof HttpMessageNotReadableException) return true;
                    if (s.getClass().getName().contains("HttpMessageNotReadableException")) return true;
                    if (s.getMessage() != null && s.getMessage().contains("Required request body is missing")) return true;
                }
            }
            t = t.getCause();
        }
        // last resort: check ex.toString()
        String full = ex.toString();
        return full != null && full.contains("HttpMessageNotReadableException");
    }

}
