package com.redis.redisinterface.controller;

import com.redis.redisinterface.bean.RedisResponse;
import com.redis.redisinterface.bean.UserSession;
import com.redis.redisinterface.redisexception.RedisOperationException;
import com.redis.redisinterface.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/redis/api")
@RequiredArgsConstructor
public class RedisRestController {

    private static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
    private final RedisService<UserSession> redisService;


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RedisResponse> deleteUserSession(@PathVariable String id) {
        log.debug("Attempting to delete user session with ID: {}", id);
        try {
            UserSession existingSession = redisService.findById(id);
            if (existingSession == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new RedisResponse(false, "User session not found for id: " + id));
            }
            redisService.delete(id);
            return ResponseEntity.ok(new RedisResponse(true, "User session deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<RedisResponse> updateUserSession(@RequestBody @Validated UserSession userSession) {
        log.debug("Attempting to update user session: {}", userSession);
        try {
            UserSession updatedSession = redisService.update(userSession.getId(), userSession);
            if (updatedSession != null) {
                return ResponseEntity.ok(new RedisResponse(true, "User session updated successfully", updatedSession));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new RedisResponse(false, "User session not found for update"));
            }
        } catch (RedisOperationException e) {
            log.error("Redis operation failed while updating user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, "Redis operation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while updating user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<RedisResponse> getAllUserSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("Attempting to retrieve all user sessions, page: {}, size: {}", page, size);
        try {
            List<UserSession> sessions = redisService.findAll(page, size);
            return ResponseEntity.ok(new RedisResponse(true, "User sessions retrieved successfully", sessions));
        } catch (RedisOperationException e) {
            log.error("Redis operation failed while retrieving user sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, "Redis operation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, INTERNAL_SERVER_ERROR));
        }
    }


    @PostMapping("/save")
    public ResponseEntity<RedisResponse> create(@RequestBody(required = true) @Validated UserSession userSession) {

        log.debug("Attempting to save user session: {}", userSession);

        try {
            redisService.save(userSession.getId(), userSession);
            UserSession savedSession = redisService.findById(userSession.getId());

            if (savedSession != null) {
                log.info("Successfully saved user session with ID: {}", userSession.getId());
                return ResponseEntity.ok(new RedisResponse(true, "User session successfully saved"));
            } else {
                log.error("Failed to verify saved data in Redis for ID: {}", userSession.getId());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new RedisResponse(false, "Failed to verify saved data"));
            }
        } catch (RedisOperationException e) {
            log.error("Redis operation failed while saving user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, "Redis operation failed: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while saving user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, INTERNAL_SERVER_ERROR));
        }

    }


    @GetMapping("/get/{id}")
    public ResponseEntity<RedisResponse> getUserSession
            (@PathVariable String id) {
        log.debug("Attempting to retrieve user session with ID: {}", id);
        try {
            UserSession userSession = redisService.findById(id);
            if (userSession != null) {
                log.info("Successfully retrieved user session with ID: {}", id);
                return ResponseEntity.ok(new RedisResponse(true, "User session retrieved successfully", userSession));
            } else {
                log.warn("User session not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new RedisResponse(false, "User session not found for id: " + id));
            }
        } catch (Exception e) {
            log.error("Unexpected error while retrieving user session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedisResponse(false, INTERNAL_SERVER_ERROR));
        }
    }

}



