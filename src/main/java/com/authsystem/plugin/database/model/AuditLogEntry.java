package com.authsystem.plugin.database.model;

import java.time.Instant;

public class AuditLogEntry {

    private final long id;
    private final String adminName;
    private final String targetPlayer;
    private final String action;
    private final String details;
    private final Instant timestamp;

    public AuditLogEntry(long id, String adminName, String targetPlayer, String action, String details, Instant timestamp) {
        this.id = id;
        this.adminName = adminName;
        this.targetPlayer = targetPlayer;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getTargetPlayer() {
        return targetPlayer;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
