package com.redis.redisinterface.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAPIConfigTest {

    @Test
    void openAPI_containsContactAndTitle() {
        OpenAPIConfig cfg = new OpenAPIConfig();
        OpenAPI api = cfg.openAPI();
        assertThat(api.getInfo()).isNotNull();
        assertThat(api.getInfo().getTitle()).contains("Redis Framework API");
        assertThat(api.getInfo().getContact()).isNotNull();
        assertThat(api.getInfo().getContact().getName()).isEqualTo("Atul Satpute");
    }
}

