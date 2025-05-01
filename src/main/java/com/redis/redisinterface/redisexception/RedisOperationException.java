package com.redis.redisinterface.redisexception;

import lombok.Getter;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class RedisOperationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final RedisErrorCode errorCode;
    private final Map<String, Object> context;

    @Getter
    public enum RedisErrorCode {
        CONNECTION_FAILED("Redis connection failed"),
        OPERATION_TIMEOUT("Redis operation timed out"),
        DATA_ACCESS_ERROR("Error accessing Redis data"),
        INVALID_OPERATION("Invalid Redis operation"),
        UNKNOWN_ERROR("Unknown Redis error");

        private final String description;

        RedisErrorCode(String description) {
            this.description = description;
        }

    }

    private RedisOperationException(Builder builder) {
        super(builder.errorCode.getDescription() + ": " + builder.message, builder.cause);
        this.errorCode = builder.errorCode;
        this.context = new HashMap<>(builder.context);
    }

    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }

    public static class Builder {
        private RedisErrorCode errorCode = RedisErrorCode.UNKNOWN_ERROR;
        private String message = "";
        private Throwable cause;
        private final Map<String, Object> context = new HashMap<>();

        public Builder withErrorCode(RedisErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public Builder withContextValue(String key, Object value) {
            this.context.put(key, value);
            return this;
        }

        public RedisOperationException build() {
            return new RedisOperationException(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
