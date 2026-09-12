package com.authsystem.plugin.database;

import com.authsystem.plugin.config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private HikariDataSource dataSource;

    public DatabaseManager(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void initialize() throws SQLException {
        HikariConfig config = new HikariConfig();
        String dbType = configManager.getDatabaseType();

        if ("MYSQL".equalsIgnoreCase(dbType)) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            String jdbcUrl = String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=%b&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8",
                    configManager.getMysqlHost(),
                    configManager.getMysqlPort(),
                    configManager.getMysqlDatabase(),
                    configManager.isMysqlUseSsl()
            );
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(configManager.getMysqlUsername());
            config.setPassword(configManager.getMysqlPassword());
        } else {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, configManager.getSqliteFile());
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            config.setConnectionTestQuery("SELECT 1");
        }

        config.setPoolName("AuthPlugin-HikariPool");
        config.setMaximumPoolSize(configManager.getPoolMaxSize());
        config.setMinimumIdle(configManager.getPoolMinIdle());
        config.setConnectionTimeout(configManager.getPoolConnectionTimeout());
        config.setIdleTimeout(configManager.getPoolIdleTimeout());
        config.setMaxLifetime(configManager.getPoolMaxLifetime());

        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() throws SQLException {
        String accountsTable = """
                CREATE TABLE IF NOT EXISTS auth_accounts (
                    unique_id VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(16) NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    is_premium BOOLEAN NOT NULL DEFAULT 0,
                    backup_password_hash VARCHAR(255),
                    registered_ip VARCHAR(45) NOT NULL,
                    last_ip VARCHAR(45) NOT NULL,
                    registered_at BIGINT NOT NULL,
                    last_login_at BIGINT NOT NULL,
                    language VARCHAR(10) NOT NULL DEFAULT 'vi'
                );
                """;

        String accountsIndex = "CREATE INDEX IF NOT EXISTS idx_auth_username ON auth_accounts(username);";

        String auditTable = """
                CREATE TABLE IF NOT EXISTS auth_audit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    admin_name VARCHAR(32) NOT NULL,
                    target_player VARCHAR(16) NOT NULL,
                    action VARCHAR(64) NOT NULL,
                    details TEXT,
                    timestamp BIGINT NOT NULL
                );
                """;

        if ("MYSQL".equalsIgnoreCase(configManager.getDatabaseType())) {
            auditTable = """
                    CREATE TABLE IF NOT EXISTS auth_audit_logs (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        admin_name VARCHAR(32) NOT NULL,
                        target_player VARCHAR(16) NOT NULL,
                        action VARCHAR(64) NOT NULL,
                        details TEXT,
                        timestamp BIGINT NOT NULL
                    );
                    """;
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            if (!"MYSQL".equalsIgnoreCase(configManager.getDatabaseType())) {
                try {
                    stmt.execute("PRAGMA journal_mode = WAL;");
                    stmt.execute("PRAGMA synchronous = NORMAL;");
                    stmt.execute("PRAGMA temp_store = MEMORY;");
                    stmt.execute("PRAGMA cache_size = 10000;");
                } catch (SQLException ignored) {
                }
            }

            stmt.executeUpdate(accountsTable);
            try {
                stmt.executeUpdate("ALTER TABLE auth_accounts ADD COLUMN language VARCHAR(10) NOT NULL DEFAULT 'vi';");
            } catch (SQLException ignored) {
                // Column might already exist
            }
            try {
                stmt.executeUpdate(accountsIndex);
            } catch (SQLException ignored) {
            }
            try {
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_auth_last_ip ON auth_accounts(last_ip);");
            } catch (SQLException ignored) {
            }
            stmt.executeUpdate(auditTable);
            try {
                stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_audit_target ON auth_audit_logs(target_player);");
            } catch (SQLException ignored) {
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("HikariDataSource is closed or not initialized.");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
