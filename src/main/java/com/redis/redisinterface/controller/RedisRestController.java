package com.redis.redisinterface.controller;

import com.redis.redisinterface.bean.RedisResponse;
import com.redis.redisinterface.bean.UserSession;
import com.redis.redisinterface.redisexception.RedisOperationException;
import com.redis.redisinterface.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Redis User Session API", description = "API endpoints for managing user sessions in Redis")

public class RedisRestController {

    private static final String INTERNAL_SERVER_ERROR = "An internal server error occurred";
    private final RedisService<UserSession> redisService;

    @Operation(summary = "Delete a user session by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User session successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<RedisResponse> deleteUserSession(
            @Parameter(description = "ID of the user session to delete")
            @PathVariable String id) {
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

    @Operation(summary = "Update an existing user session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User session successfully updated"),
            @ApiResponse(responseCode = "404", description = "User session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<RedisResponse> updateUserSession(
            @Parameter(description = "Updated user session details")
            @RequestBody @Validated UserSession userSession) {

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

    @Operation(summary = "Get all user sessions with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user sessions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<RedisResponse> getAllUserSessions(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
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


    @Operation(summary = "Create a new user session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User session successfully created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<RedisResponse> create(
            @Parameter(description = "User session to create")
            @RequestBody(required = true) @Validated UserSession userSession) {


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


    @Operation(summary = "Get a user session by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user session"),
            @ApiResponse(responseCode = "404", description = "User session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<RedisResponse> getUserSession(
            @Parameter(description = "ID of the user session to retrieve")
            @PathVariable String id) {

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



