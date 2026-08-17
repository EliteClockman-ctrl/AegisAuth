package com.authsystem.plugin.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    private String databaseType;
    private String sqliteFile;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private boolean mysqlUseSsl;
    private int poolMaxSize;
    private int poolMinIdle;
    private long poolConnectionTimeout;
    private long poolIdleTimeout;
    private long poolMaxLifetime;

    private int argon2Iterations;
    private int argon2MemoryKb;
    private int argon2Parallelism;
    private int passwordMinLength;
    private int passwordMaxLength;
    private boolean requireLetterAndNumber;
    private Set<String> blacklistedPasswords = new HashSet<>();

    private boolean sessionEnabled;
    private long sessionTtlMinutes;

    private boolean rateLimitEnabled;
    private int maxFailedAttempts;
    private int lockoutDurationMinutes;

    private int authTimeoutSeconds;
    private int reminderIntervalSeconds;
    private String defaultLanguage;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.defaultLanguage = config.getString("language", "vi");
        this.databaseType = config.getString("database.type", "SQLITE").toUpperCase();
        this.sqliteFile = config.getString("database.sqlite.file", "auth_database.db");
        this.mysqlHost = config.getString("database.mysql.host", "localhost");
        this.mysqlPort = config.getInt("database.mysql.port", 3306);
        this.mysqlDatabase = config.getString("database.mysql.database", "minecraft_auth");
        this.mysqlUsername = config.getString("database.mysql.username", "root");
        this.mysqlPassword = config.getString("database.mysql.password", "");
        this.mysqlUseSsl = config.getBoolean("database.mysql.use-ssl", false);

        this.poolMaxSize = config.getInt("database.pool.maximum-pool-size", 10);
        this.poolMinIdle = config.getInt("database.pool.minimum-idle", 5);
        this.poolConnectionTimeout = config.getLong("database.pool.connection-timeout-ms", 30000L);
        this.poolIdleTimeout = config.getLong("database.pool.idle-timeout-ms", 600000L);
        this.poolMaxLifetime = config.getLong("database.pool.max-lifetime-ms", 1800000L);

        this.argon2Iterations = config.getInt("security.argon2.iterations", 3);
        this.argon2MemoryKb = config.getInt("security.argon2.memory-kb", 65536);
        this.argon2Parallelism = config.getInt("security.argon2.parallelism", 1);

        this.passwordMinLength = config.getInt("security.password.min-length", 6);
        this.passwordMaxLength = config.getInt("security.password.max-length", 32);
        this.requireLetterAndNumber = config.getBoolean("security.password.require-letter-and-number", false);
        List<String> blacklisted = config.getStringList("security.password.blacklisted-passwords");
        this.blacklistedPasswords = new HashSet<>(blacklisted);

        this.sessionEnabled = config.getBoolean("session.enabled", true);
        this.sessionTtlMinutes = config.getLong("session.ttl-minutes", 720L);

        this.rateLimitEnabled = config.getBoolean("rate-limit.enabled", true);
        this.maxFailedAttempts = config.getInt("rate-limit.max-failed-attempts", 5);
        this.lockoutDurationMinutes = config.getInt("rate-limit.lockout-duration-minutes", 10);

        this.authTimeoutSeconds = config.getInt("auth-timeout.seconds", 60);
        this.reminderIntervalSeconds = config.getInt("auth-timeout.reminder-interval-seconds", 10);
    }

    public String getRawMessage(MessageKey key) {
        return getRawMessage(key, defaultLanguage);
    }

    public String getRawMessage(MessageKey key, String lang) {
        String language = (lang != null && !lang.isEmpty()) ? lang.toLowerCase() : defaultLanguage;
        String langSpecificKey = key.getPath().replace("messages.", "messages." + language + ".");
        if (config.contains(langSpecificKey)) {
            return config.getString(langSpecificKey);
        }
        if ("vi".equalsIgnoreCase(language)) {
            if (config.contains(key.getPath())) {
                return config.getString(key.getPath());
            }
            return key.getDefaultMessage("vi");
        }
        return key.getDefaultMessage(language);
    }

    public String getPrefix() {
        return getPrefix(defaultLanguage);
    }

    public String getPrefix(String lang) {
        return getRawMessage(MessageKey.PREFIX, lang);
    }

    public String getDefaultLanguage() {
        return defaultLanguage != null ? defaultLanguage : "vi";
    }

    public void setSystemLanguage(String lang) {
        this.defaultLanguage = (lang != null && !lang.isEmpty()) ? lang.toLowerCase() : "vi";
        config.set("language", this.defaultLanguage);
        plugin.saveConfig();
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getSqliteFile() {
        return sqliteFile;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public boolean isMysqlUseSsl() {
        return mysqlUseSsl;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public int getPoolMinIdle() {
        return poolMinIdle;
    }

    public long getPoolConnectionTimeout() {
        return poolConnectionTimeout;
    }

    public long getPoolIdleTimeout() {
        return poolIdleTimeout;
    }

    public long getPoolMaxLifetime() {
        return poolMaxLifetime;
    }

    public int getArgon2Iterations() {
        return argon2Iterations;
    }

    public int getArgon2MemoryKb() {
        return argon2MemoryKb;
    }

    public int getArgon2Parallelism() {
        return argon2Parallelism;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    public int getPasswordMaxLength() {
        return passwordMaxLength;
    }

    public boolean isRequireLetterAndNumber() {
        return requireLetterAndNumber;
    }

    public Set<String> getBlacklistedPasswords() {
        return Collections.unmodifiableSet(blacklistedPasswords);
    }

    public boolean isSessionEnabled() {
        return sessionEnabled;
    }

    public long getSessionTtlMinutes() {
        return sessionTtlMinutes;
    }

    public boolean isRateLimitEnabled() {
        return rateLimitEnabled;
    }

    public int getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public int getLockoutDurationMinutes() {
        return lockoutDurationMinutes;
    }

    public int getAuthTimeoutSeconds() {
        return authTimeoutSeconds;
    }

    public int getReminderIntervalSeconds() {
        return reminderIntervalSeconds;
    }
}
