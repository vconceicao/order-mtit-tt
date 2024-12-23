package com.challenge.mit.order.service;

import com.challenge.mit.order.redis.Redis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private Redis redis;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testHasKey_KeyExists() {
        // Arrange
        String key = "existingKey";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        // Act
        boolean result = redis.hasKey(key);

        // Assert
        assertTrue(result, "The key should exist in Redis");
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    void testHasKey_KeyDoesNotExist() {
        // Arrange
        String key = "nonExistingKey";
        when(redisTemplate.hasKey(key)).thenReturn(false);

        // Act
        boolean result = redis.hasKey(key);

        // Assert
        assertFalse(result, "The key should not exist in Redis");
        verify(redisTemplate, times(1)).hasKey(key);
    }

    @Test
    void testAddKeys_Success() {
        // Arrange
        List<String> keys = List.of("key1", "key2", "key3");

        // Act
        redis.addKeys(keys);

        // Assert
        for (String key : keys) {
            verify(valueOperations, times(1)).set(key, "processado", 24, TimeUnit.HOURS);
        }
    }

    @Test
    void testAddKeys_EmptyList() {
        // Arrange
        List<String> keys = List.of();

        // Act
        redis.addKeys(keys);

        // Assert
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}
