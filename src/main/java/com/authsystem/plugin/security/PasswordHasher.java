package com.authsystem.plugin.security;

import com.authsystem.plugin.config.ConfigManager;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PasswordHasher {

    private final ConfigManager configManager;
    private final Executor asyncExecutor;
    private final Argon2 argon2;

    public PasswordHasher(ConfigManager configManager, Executor asyncExecutor) {
        this.configManager = configManager;
        this.asyncExecutor = asyncExecutor;
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public CompletableFuture<String> hashPassword(char[] password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return argon2.hash(
                        configManager.getArgon2Iterations(),
                        configManager.getArgon2MemoryKb(),
                        configManager.getArgon2Parallelism(),
                        password
                );
            } finally {
                argon2.wipeArray(password);
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> verifyPassword(String hash, char[] password) {
        return CompletableFuture.supplyAsync(() -> {
            if (hash == null || hash.isEmpty() || password == null) {
                return false;
            }
            try {
                return argon2.verify(hash, password);
            } finally {
                argon2.wipeArray(password);
            }
        }, asyncExecutor);
    }

    public String hashPasswordSync(char[] password) {
        try {
            return argon2.hash(
                    configManager.getArgon2Iterations(),
                    configManager.getArgon2MemoryKb(),
                    configManager.getArgon2Parallelism(),
                    password
            );
        } finally {
            argon2.wipeArray(password);
        }
    }

    public boolean verifyPasswordSync(String hash, char[] password) {
        if (hash == null || hash.isEmpty() || password == null) {
            return false;
        }
        try {
            return argon2.verify(hash, password);
        } finally {
            argon2.wipeArray(password);
        }
    }
}
