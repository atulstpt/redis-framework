package com.redis.redisinterface.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User Session Information")

public class UserSession implements Serializable {
    @Schema(description = "Unique identifier for the user session")
    String id;
    String oidcSession;
    String portal;
    String createdAt;
    String status;
}
