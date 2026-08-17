package com.authsystem.plugin.security;

import com.authsystem.plugin.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    private PasswordHasher passwordHasher;
    private ConfigManager configManager;

    @BeforeEach
    public void setup() {
        configManager = Mockito.mock(ConfigManager.class);
        Mockito.when(configManager.getArgon2Iterations()).thenReturn(2);
        Mockito.when(configManager.getArgon2MemoryKb()).thenReturn(16384);
        Mockito.when(configManager.getArgon2Parallelism()).thenReturn(1);

        passwordHasher = new PasswordHasher(configManager, ForkJoinPool.commonPool());
    }

    @Test
    public void testHashAndVerifySync() {
        char[] password = "SuperSecretPassword123!".toCharArray();
        String hash = passwordHasher.hashPasswordSync(password);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$argon2id$"));

        char[] checkPass = "SuperSecretPassword123!".toCharArray();
        boolean matches = passwordHasher.verifyPasswordSync(hash, checkPass);
        assertTrue(matches);

        char[] wrongPass = "WrongPassword123!".toCharArray();
        boolean wrongMatches = passwordHasher.verifyPasswordSync(hash, wrongPass);
        assertFalse(wrongMatches);
    }

    @Test
    public void testHashAndVerifyAsync() throws Exception {
        char[] password = "AsyncPassword2026".toCharArray();
        String hash = passwordHasher.hashPassword(password).get();

        assertNotNull(hash);
        assertTrue(hash.startsWith("$argon2id$"));

        boolean verified = passwordHasher.verifyPassword(hash, "AsyncPassword2026".toCharArray()).get();
        assertTrue(verified);

        boolean wrongVerified = passwordHasher.verifyPassword(hash, "IncorrectPass".toCharArray()).get();
        assertFalse(wrongVerified);
    }
}
