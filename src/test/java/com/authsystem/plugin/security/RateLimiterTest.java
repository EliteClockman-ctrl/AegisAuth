package com.authsystem.plugin.security;

import com.authsystem.plugin.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterTest {

    private RateLimiter rateLimiter;
    private ConfigManager configManager;

    @BeforeEach
    public void setup() {
        configManager = Mockito.mock(ConfigManager.class);
        Mockito.when(configManager.isRateLimitEnabled()).thenReturn(true);
        Mockito.when(configManager.getMaxFailedAttempts()).thenReturn(3);
        Mockito.when(configManager.getLockoutDurationMinutes()).thenReturn(10);

        rateLimiter = new RateLimiter(configManager);
    }

    @Test
    public void testRateLimitingLockout() {
        String testIp = "192.168.1.100";

        assertFalse(rateLimiter.isLocked(testIp));

        int remaining = rateLimiter.recordFailedAttempt(testIp);
        assertEquals(2, remaining);
        assertFalse(rateLimiter.isLocked(testIp));

        remaining = rateLimiter.recordFailedAttempt(testIp);
        assertEquals(1, remaining);
        assertFalse(rateLimiter.isLocked(testIp));

        remaining = rateLimiter.recordFailedAttempt(testIp);
        assertEquals(0, remaining);
        assertTrue(rateLimiter.isLocked(testIp));

        rateLimiter.clearFailedAttempts(testIp);
        assertFalse(rateLimiter.isLocked(testIp));
    }
}
