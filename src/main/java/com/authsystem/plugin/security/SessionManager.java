package com.authsystem.plugin.security;

import com.authsystem.plugin.config.ConfigManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private final ConfigManager configManager;
    private final Cache<UUID, String> sessionCache;
    private final Set<UUID> loggedInPlayers = ConcurrentHashMap.newKeySet();

    public SessionManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.sessionCache = Caffeine.newBuilder()
                .expireAfterWrite(configManager.getSessionTtlMinutes(), TimeUnit.MINUTES)
                .build();
    }

    public boolean isSessionValid(UUID uniqueId, String currentIp) {
        if (!configManager.isSessionEnabled()) {
            return false;
        }
        String savedIp = sessionCache.getIfPresent(uniqueId);
        return savedIp != null && savedIp.equals(currentIp);
    }

    public void saveSession(UUID uniqueId, String ip) {
        if (configManager.isSessionEnabled()) {
            sessionCache.put(uniqueId, ip);
        }
        loggedInPlayers.add(uniqueId);
    }

    public void markLoggedIn(UUID uniqueId) {
        loggedInPlayers.add(uniqueId);
    }

    public boolean isLoggedIn(UUID uniqueId) {
        return loggedInPlayers.contains(uniqueId);
    }

    public void markLoggedOut(UUID uniqueId, boolean clearSession) {
        loggedInPlayers.remove(uniqueId);
        if (clearSession) {
            sessionCache.invalidate(uniqueId);
        }
    }

    public void invalidateSession(UUID uniqueId) {
        sessionCache.invalidate(uniqueId);
    }
}
