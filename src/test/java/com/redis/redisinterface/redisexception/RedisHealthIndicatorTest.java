package com.redis.redisinterface.redisexception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RedisHealthIndicatorTest {

    @Test
    void doHealthCheck_upWhenPingSucceeds() throws Exception {
        RedisTemplate<String, Object> template = mock(RedisTemplate.class);
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisConnection connection = mock(RedisConnection.class);

        when(template.getConnectionFactory()).thenReturn(factory);
        when(factory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenReturn("PONG");

        RedisHealthIndicator indicator = new RedisHealthIndicator(template);

        Health.Builder builder = Health.up();
        indicator.doHealthCheck(builder);
        Health health = builder.build();
        assertThat(health.getStatus().getCode()).isEqualTo("UP");

        verify(connection, times(1)).ping();
    }

    @Test
    void doHealthCheck_downWhenPingThrows() throws Exception {
        RedisTemplate<String, Object> template = mock(RedisTemplate.class);
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisConnection connection = mock(RedisConnection.class);

        when(template.getConnectionFactory()).thenReturn(factory);
        when(factory.getConnection()).thenReturn(connection);
        when(connection.ping()).thenThrow(new RuntimeException("no redis"));

        RedisHealthIndicator indicator = new RedisHealthIndicator(template);

        Health.Builder builder = Health.up();
        indicator.doHealthCheck(builder);
        Health health = builder.build();
        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");
        assertThat(health.getDetails().get("error")).isNotNull();

        verify(connection, times(1)).ping();
    }
}

