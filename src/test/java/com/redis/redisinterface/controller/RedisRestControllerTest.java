package com.redis.redisinterface.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.redisinterface.bean.RedisResponse;
import com.redis.redisinterface.bean.UserSession;
import com.redis.redisinterface.redisexception.RedisOperationException;
import com.redis.redisinterface.service.RedisService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RedisRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService<UserSession> redisService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreate_SuccessfulSave() throws Exception, RedisOperationException {
        UserSession userSession = new UserSession("1", "session1", "portal1", "2023-01-01", "active");
        RedisResponse mockResponse = new RedisResponse(true, "User session successfully saved");

        Mockito.doNothing().when(redisService).save(eq("1"), any(UserSession.class));
        Mockito.when(redisService.findById("1")).thenReturn(userSession);

        mockMvc.perform(post("/redis/api/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSession)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(mockResponse))));
    }

    @Test
    public void testCreate_NullUserSession() throws Exception {
        RedisResponse mockResponse = new RedisResponse(false, "User session cannot be null");

        mockMvc.perform(post("/redis/api/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(mockResponse))));
    }

    @Test
    public void testCreate_FailedToVerifySavedData() throws Exception, RedisOperationException {
        UserSession userSession = new UserSession("1", "session1", "portal1", "2023-01-01", "active");
        RedisResponse mockResponse = new RedisResponse(false, "Failed to verify saved data");

        Mockito.doNothing().when(redisService).save(eq("1"), any(UserSession.class));
        Mockito.when(redisService.findById("1")).thenReturn(null);

        mockMvc.perform(post("/redis/api/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSession)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(mockResponse))));
    }

    @Test
    public void testCreate_InternalServerError() throws Exception, RedisOperationException {
        UserSession userSession = new UserSession("1", "session1", "portal1", "2023-01-01", "active");
        RedisResponse mockResponse = new RedisResponse(false, "An internal server error occurred");

        Mockito.doThrow(new RuntimeException("Redis exception")).when(redisService).save(eq("1"), any(UserSession.class));

        mockMvc.perform(post("/redis/api/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userSession)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(mockResponse))));
    }
}