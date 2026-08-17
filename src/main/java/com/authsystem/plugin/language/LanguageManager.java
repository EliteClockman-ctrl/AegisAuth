package com.authsystem.plugin.language;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager {

    private volatile String globalLanguage = "vi";

    public LanguageManager() {
    }

    public LanguageManager(String initialLanguage) {
        if (initialLanguage != null && !initialLanguage.isEmpty()) {
            this.globalLanguage = initialLanguage.toLowerCase();
        }
    }

    public String getGlobalLanguage() {
        return globalLanguage != null ? globalLanguage : "vi";
    }

    public void setGlobalLanguage(String language) {
        if (language != null && !language.isEmpty()) {
            this.globalLanguage = language.toLowerCase();
        }
    }

    public String getLanguage(UUID uniqueId) {
        return getGlobalLanguage();
    }
}
