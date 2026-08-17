package com.authsystem.plugin.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class PremiumManager {

    private final Executor asyncExecutor;
    private final Cache<UUID, Boolean> pendingConfirmationCache;

    public PremiumManager(Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.pendingConfirmationCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    public void createPendingConfirmation(UUID uniqueId) {
        pendingConfirmationCache.put(uniqueId, Boolean.TRUE);
    }

    public boolean hasPendingConfirmation(UUID uniqueId) {
        return pendingConfirmationCache.getIfPresent(uniqueId) != null;
    }

    public void removePendingConfirmation(UUID uniqueId) {
        pendingConfirmationCache.invalidate(uniqueId);
    }

    public CompletableFuture<Boolean> isMojangAccount(String username) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = URI.create("https://api.mojang.com/users/profiles/minecraft/" + username).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestProperty("User-Agent", "MinecraftAuthPlugin/1.0");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        return response.toString().contains("\"id\"");
                    }
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }, asyncExecutor);
    }
}
