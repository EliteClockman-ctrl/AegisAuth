package com.authsystem.plugin.database.dao;

import com.authsystem.plugin.database.DatabaseManager;
import com.authsystem.plugin.database.model.AuditLogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AuditDao {

    private final DatabaseManager databaseManager;
    private final Executor asyncExecutor;

    public AuditDao(DatabaseManager databaseManager, Executor asyncExecutor) {
        this.databaseManager = databaseManager;
        this.asyncExecutor = asyncExecutor;
    }

    public CompletableFuture<Void> log(String adminName, String targetPlayer, String action, String details) {
        return CompletableFuture.runAsync(() -> {
            String sql = """
                    INSERT INTO auth_audit_logs (admin_name, target_player, action, details, timestamp)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, adminName);
                stmt.setString(2, targetPlayer);
                stmt.setString(3, action);
                stmt.setString(4, details);
                stmt.setLong(5, Instant.now().toEpochMilli());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, asyncExecutor);
    }

    public CompletableFuture<List<AuditLogEntry>> getRecentLogs(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<AuditLogEntry> logs = new ArrayList<>();
            String sql = "SELECT * FROM auth_audit_logs ORDER BY timestamp DESC LIMIT ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, limit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        logs.add(new AuditLogEntry(
                                rs.getLong("id"),
                                rs.getString("admin_name"),
                                rs.getString("target_player"),
                                rs.getString("action"),
                                rs.getString("details"),
                                Instant.ofEpochMilli(rs.getLong("timestamp"))
                        ));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return logs;
        }, asyncExecutor);
    }
}
