package com.authsystem.plugin.security;

import com.authsystem.plugin.config.ConfigManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {

    private final ConfigManager configManager;
    private final Cache<String, AtomicInteger> failedAttemptsCache;
    private final Cache<String, Long> lockoutCache;

    public RateLimiter(ConfigManager configManager) {
        this.configManager = configManager;
        this.failedAttemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(configManager.getLockoutDurationMinutes(), TimeUnit.MINUTES)
                .build();
        this.lockoutCache = Caffeine.newBuilder()
                .expireAfterWrite(configManager.getLockoutDurationMinutes(), TimeUnit.MINUTES)
                .build();
    }

    public boolean isLocked(String ip) {
        if (!configManager.isRateLimitEnabled()) {
            return false;
        }
        Long unlockTime = lockoutCache.getIfPresent(ip);
        if (unlockTime != null) {
            if (System.currentTimeMillis() < unlockTime) {
                return true;
            } else {
                lockoutCache.invalidate(ip);
            }
        }
        return false;
    }

    public int recordFailedAttempt(String ip) {
        if (!configManager.isRateLimitEnabled()) {
            return 999;
        }
        AtomicInteger attempts = failedAttemptsCache.get(ip, k -> new AtomicInteger(0));
        int current = attempts.incrementAndGet();
        if (current >= configManager.getMaxFailedAttempts()) {
            lockoutCache.put(ip, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(configManager.getLockoutDurationMinutes()));
            failedAttemptsCache.invalidate(ip);
        }
        return Math.max(0, configManager.getMaxFailedAttempts() - current);
    }

    public void clearFailedAttempts(String ip) {
        failedAttemptsCache.invalidate(ip);
        lockoutCache.invalidate(ip);
    }

    public int getRemainingAttempts(String ip) {
        AtomicInteger attempts = failedAttemptsCache.getIfPresent(ip);
        if (attempts == null) {
            return configManager.getMaxFailedAttempts();
        }
        return Math.max(0, configManager.getMaxFailedAttempts() - attempts.get());
    }
}
