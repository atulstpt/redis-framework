package com.redis.redisinterface.redisexception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedisOperationExceptionTest {

    @Test
    void builder_constructsWithMessageAndErrorCode() {
        RedisOperationException ex = RedisOperationException.builder()
                .withErrorCode(RedisOperationException.RedisErrorCode.DATA_ACCESS_ERROR)
                .withMessage("failed")
                .withContextValue("k","v")
                .build();

        assertNotNull(ex);
        assertEquals(RedisOperationException.RedisErrorCode.DATA_ACCESS_ERROR, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("failed"));
        assertNotNull(ex.getContext());
        assertEquals("v", ex.getContext().get("k"));
    }
}

