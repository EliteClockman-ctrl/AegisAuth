package com.authsystem.plugin.database.dao;

import com.authsystem.plugin.database.DatabaseManager;
import com.authsystem.plugin.database.model.AuthAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AuthDao {

    private final DatabaseManager databaseManager;
    private final Executor asyncExecutor;

    public AuthDao(DatabaseManager databaseManager, Executor asyncExecutor) {
        this.databaseManager = databaseManager;
        this.asyncExecutor = asyncExecutor;
    }

    public CompletableFuture<Optional<AuthAccount>> findByUniqueId(UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM auth_accounts WHERE unique_id = ? LIMIT 1;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, uniqueId.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSet(rs));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, asyncExecutor);
    }

    public CompletableFuture<Optional<AuthAccount>> findByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM auth_accounts WHERE LOWER(username) = LOWER(?) LIMIT 1;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSet(rs));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> save(AuthAccount account) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = """
                    INSERT INTO auth_accounts (unique_id, username, password_hash, is_premium, backup_password_hash, registered_ip, last_ip, registered_at, last_login_at, language)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, account.getUniqueId().toString());
                stmt.setString(2, account.getUsername());
                stmt.setString(3, account.getPasswordHash());
                stmt.setBoolean(4, account.isPremium());
                stmt.setString(5, account.getBackupPasswordHash());
                stmt.setString(6, account.getRegisteredIp());
                stmt.setString(7, account.getLastIp());
                stmt.setLong(8, account.getRegisteredAt().toEpochMilli());
                stmt.setLong(9, account.getLastLoginAt().toEpochMilli());
                stmt.setString(10, account.getLanguage());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> updatePassword(UUID uniqueId, String newPasswordHash) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auth_accounts SET password_hash = ? WHERE unique_id = ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newPasswordHash);
                stmt.setString(2, uniqueId.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> updateBackupPassword(UUID uniqueId, String backupPasswordHash) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auth_accounts SET backup_password_hash = ? WHERE unique_id = ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, backupPasswordHash);
                stmt.setString(2, uniqueId.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> setPremium(UUID uniqueId, boolean isPremium) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auth_accounts SET is_premium = ? WHERE unique_id = ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setBoolean(1, isPremium);
                stmt.setString(2, uniqueId.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> updateLanguage(UUID uniqueId, String language) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auth_accounts SET language = ? WHERE unique_id = ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, language);
                stmt.setString(2, uniqueId.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> updateLoginMetadata(UUID uniqueId, String lastIp, Instant lastLoginAt) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auth_accounts SET last_ip = ?, last_login_at = ? WHERE unique_id = ?;";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, lastIp);
                stmt.setLong(2, lastLoginAt.toEpochMilli());
                stmt.setString(3, uniqueId.toString());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    public CompletableFuture<Boolean> deleteByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "DELETE FROM auth_accounts WHERE LOWER(username) = LOWER(?);";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }, asyncExecutor);
    }

    private AuthAccount mapResultSet(ResultSet rs) throws SQLException {
        String lang = "vi";
        try {
            lang = rs.getString("language");
            if (lang == null) lang = "vi";
        } catch (SQLException ignored) {}

        return new AuthAccount(
                UUID.fromString(rs.getString("unique_id")),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getBoolean("is_premium"),
                rs.getString("backup_password_hash"),
                rs.getString("registered_ip"),
                rs.getString("last_ip"),
                Instant.ofEpochMilli(rs.getLong("registered_at")),
                Instant.ofEpochMilli(rs.getLong("last_login_at")),
                lang
        );
    }
}
