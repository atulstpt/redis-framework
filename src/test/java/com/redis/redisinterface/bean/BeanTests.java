package com.redis.redisinterface.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanTests {

    @Test
    void userSession_and_redisResponse_gettersSetters() {
        UserSession u = new UserSession();
        u.setId("12");
        u.setOidcSession("s");
        u.setPortal("p");
        u.setCreatedAt("d");
        u.setStatus("ok");

        assertEquals("12", u.getId());
        assertEquals("s", u.getOidcSession());

        RedisResponse r = new RedisResponse(true, "msg", u);
        assertTrue(r.isSuccess());
        assertEquals("msg", r.getMessage());
        assertNotNull(r.getData());

        RedisResponse r2 = new RedisResponse(false, "err");
        assertFalse(r2.isSuccess());
        assertEquals("err", r2.getMessage());
    }
}

