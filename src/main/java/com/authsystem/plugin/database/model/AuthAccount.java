package com.authsystem.plugin.database.model;

import java.time.Instant;
import java.util.UUID;

public class AuthAccount {

    private final UUID uniqueId;
    private String username;
    private String passwordHash;
    private boolean isPremium;
    private String backupPasswordHash;
    private String registeredIp;
    private String lastIp;
    private Instant registeredAt;
    private Instant lastLoginAt;
    private String language;

    public AuthAccount(UUID uniqueId, String username, String passwordHash, boolean isPremium,
                       String backupPasswordHash, String registeredIp, String lastIp,
                       Instant registeredAt, Instant lastLoginAt) {
        this(uniqueId, username, passwordHash, isPremium, backupPasswordHash, registeredIp, lastIp, registeredAt, lastLoginAt, "vi");
    }

    public AuthAccount(UUID uniqueId, String username, String passwordHash, boolean isPremium,
                       String backupPasswordHash, String registeredIp, String lastIp,
                       Instant registeredAt, Instant lastLoginAt, String language) {
        this.uniqueId = uniqueId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isPremium = isPremium;
        this.backupPasswordHash = backupPasswordHash;
        this.registeredIp = registeredIp;
        this.lastIp = lastIp;
        this.registeredAt = registeredAt;
        this.lastLoginAt = lastLoginAt;
        this.language = language != null ? language : "vi";
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getBackupPasswordHash() {
        return backupPasswordHash;
    }

    public void setBackupPasswordHash(String backupPasswordHash) {
        this.backupPasswordHash = backupPasswordHash;
    }

    public String getRegisteredIp() {
        return registeredIp;
    }

    public void setRegisteredIp(String registeredIp) {
        this.registeredIp = registeredIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language != null ? language : "vi";
    }
}
