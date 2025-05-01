package com.redis.redisinterface.service;

import com.redis.redisinterface.redisexception.RedisOperationException;

import java.util.List;

public interface RedisService<T> {
    void save(String key, T t) throws RedisOperationException;
    T findById(String id);
    List<T> findAll(int page, int size) throws RedisOperationException;
    T update(String key,T t) throws RedisOperationException;
    void delete(String id);
}
