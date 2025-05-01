package com.redis.redisinterface.service;

import com.redis.redisinterface.redisexception.RedisOperationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedisServiceImpl<T> implements RedisService<T> {

    private static final String KEY = "Product";
    private final RedisTemplate<String, T> redisTemplate;

    // Make TTL configurable
    @Value("${redis.ttl.hours:24}")
    private long ttlHours;

    private Duration defaultTtl;

    @PostConstruct
    private void init() {
        this.defaultTtl = Duration.ofHours(ttlHours);
    }

    public RedisServiceImpl(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, T value) throws RedisOperationException {
        try {
            validateKeyValue(key, value);

            // Atomic operation using setIfAbsent
            if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, defaultTtl))) {
                throw RedisOperationException.builder()
                        .withErrorCode(RedisOperationException.RedisErrorCode.INVALID_OPERATION)
                        .withMessage("Key already exists or save failed: " + key)
                        .build();
            }

            log.debug("Successfully saved value for key: {}", key);
        } catch (Exception e) {
            handleRedisException("Error saving to Redis", key, e);
        }

    }

    @Override
    public T findById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("ID cannot be null or empty");
            }
            T value = redisTemplate.opsForValue().get(id);
            if (value == null) {
                log.debug("No value found for ID: {}", id);
            } else {
                log.debug("Successfully retrieved value for ID: {}", id);
            }
            return value;
        } catch (Exception e) {
            log.error("Error retrieving value for ID: {}", id, e);
            return null;
        }
    }

    @Override
    public List<T> findAll(int page, int size) throws RedisOperationException {
        try {
            List<T> results = new ArrayList<>();
            ScanOptions scanOptions = ScanOptions.scanOptions()
                    .match("*")
                    .count(Math.max(size * 2, 100)) // Optimize scan batch size
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(scanOptions)) {
                return cursor.stream()
                        .skip((long) page * size)
                        .limit(size)
                        .map(key -> redisTemplate.opsForValue().get(key))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            handleRedisException("Error retrieving data from Redis", null, e);
            return Collections.emptyList();
        }

    }

    @Override
    public T update(String key, T value) throws RedisOperationException {
        try {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Key and value cannot be null");
            }

            if (!redisTemplate.hasKey(key)) {
                throw RedisOperationException.builder()
                        .withErrorCode(RedisOperationException.RedisErrorCode.DATA_ACCESS_ERROR)
                        .withMessage("Key not found: " + key)
                        .build();
            }

            redisTemplate.opsForValue().set(key, value, defaultTtl);
            return value;
        } catch (Exception e) {
            log.error("Error updating data in Redis - key: {}", key, e);
            throw RedisOperationException.builder()
                    .withErrorCode(RedisOperationException.RedisErrorCode.DATA_ACCESS_ERROR)
                    .withMessage("Failed to update data in Redis")
                    .withCause(e)
                    .build();
        }
    }

    @Override
    public void delete(String id) {
        try {
            if (id != null && redisTemplate.hasKey(id)) {
                redisTemplate.delete(id);
                log.info("Successfully deleted key: {}", id);
            } else {
                log.warn("Key not found for deletion: {}", id);
            }
        } catch (Exception e) {
            log.error("Error deleting key: {}", id, e);
            throw new RuntimeException("Failed to delete data from Redis", e);
        }
    }

    private void validateKeyValue(String key, T value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
    }

    private void handleRedisException(String message, String key, Exception e) throws RedisOperationException {
        String errorMessage = key != null ? message + " - key: " + key : message;
        log.error(errorMessage, e);
        throw RedisOperationException.builder()
                .withErrorCode(RedisOperationException.RedisErrorCode.DATA_ACCESS_ERROR)
                .withMessage(errorMessage)
                .withCause(e)
                .build();
    }


}