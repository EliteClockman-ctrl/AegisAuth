package com.authsystem.plugin.util;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;

public class ValidationUtil {

    private final ConfigManager configManager;

    public ValidationUtil(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public enum ValidationResult {
        VALID(null),
        TOO_SHORT(MessageKey.REGISTER_PASSWORD_TOO_SHORT),
        TOO_LONG(MessageKey.REGISTER_PASSWORD_TOO_LONG),
        BLACKLISTED(MessageKey.REGISTER_PASSWORD_BLACKLISTED),
        REQUIRE_LETTER_AND_NUMBER(MessageKey.REGISTER_PASSWORD_BLACKLISTED);

        private final MessageKey errorKey;

        ValidationResult(MessageKey errorKey) {
            this.errorKey = errorKey;
        }

        public MessageKey getErrorKey() {
            return errorKey;
        }

        public boolean isValid() {
            return this == VALID;
        }
    }

    public ValidationResult validatePassword(String password) {
        if (password == null || password.length() < configManager.getPasswordMinLength()) {
            return ValidationResult.TOO_SHORT;
        }
        if (password.length() > configManager.getPasswordMaxLength()) {
            return ValidationResult.TOO_LONG;
        }
        if (configManager.getBlacklistedPasswords().contains(password.toLowerCase())) {
            return ValidationResult.BLACKLISTED;
        }
        if (configManager.isRequireLetterAndNumber()) {
            boolean hasLetter = false;
            boolean hasDigit = false;
            for (char c : password.toCharArray()) {
                if (Character.isLetter(c)) hasLetter = true;
                if (Character.isDigit(c)) hasDigit = true;
            }
            if (!hasLetter || !hasDigit) {
                return ValidationResult.REQUIRE_LETTER_AND_NUMBER;
            }
        }
        return ValidationResult.VALID;
    }
}
