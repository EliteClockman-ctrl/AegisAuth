package com.authsystem.plugin.util;

import com.authsystem.plugin.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    private ValidationUtil validationUtil;
    private ConfigManager configManager;

    @BeforeEach
    public void setup() {
        configManager = Mockito.mock(ConfigManager.class);
        Mockito.when(configManager.getPasswordMinLength()).thenReturn(6);
        Mockito.when(configManager.getPasswordMaxLength()).thenReturn(32);
        Mockito.when(configManager.isRequireLetterAndNumber()).thenReturn(false);
        Mockito.when(configManager.getBlacklistedPasswords()).thenReturn(Set.of("123456", "password"));

        validationUtil = new ValidationUtil(configManager);
    }

    @Test
    public void testValidPassword() {
        ValidationUtil.ValidationResult result = validationUtil.validatePassword("MyStrongP@ss99");
        assertTrue(result.isValid());
    }

    @Test
    public void testTooShortPassword() {
        ValidationUtil.ValidationResult result = validationUtil.validatePassword("123");
        assertFalse(result.isValid());
        assertEquals(ValidationUtil.ValidationResult.TOO_SHORT, result);
    }

    @Test
    public void testBlacklistedPassword() {
        ValidationUtil.ValidationResult result = validationUtil.validatePassword("123456");
        assertFalse(result.isValid());
        assertEquals(ValidationUtil.ValidationResult.BLACKLISTED, result);
    }
}
