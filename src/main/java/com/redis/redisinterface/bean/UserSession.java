package com.redis.redisinterface.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession implements Serializable {
    String id;
    String oidcSession;
    String portal;
    String createdAt;
    String status;
}
