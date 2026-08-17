package com.authsystem.plugin;

import com.authsystem.plugin.command.AdminManageCommand;
import com.authsystem.plugin.command.AdminUnpremiumCommand;
import com.authsystem.plugin.command.AdminUnregisterCommand;
import com.authsystem.plugin.command.ChangePasswordCommand;
import com.authsystem.plugin.command.LanguageCommand;
import com.authsystem.plugin.command.LoginCommand;
import com.authsystem.plugin.command.PremiumCommand;
import com.authsystem.plugin.command.RegisterCommand;
import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.database.DatabaseManager;
import com.authsystem.plugin.database.dao.AuditDao;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.language.LanguageManager;
import com.authsystem.plugin.listener.PlayerJoinQuitListener;
import com.authsystem.plugin.listener.PlayerPreLoginListener;
import com.authsystem.plugin.listener.PlayerRestrictionListener;
import com.authsystem.plugin.security.PasswordHasher;
import com.authsystem.plugin.security.PremiumManager;
import com.authsystem.plugin.security.RateLimiter;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.task.AuthTimeoutTask;
import com.authsystem.plugin.util.MessageUtil;
import com.authsystem.plugin.util.ValidationUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class AuthPlugin extends JavaPlugin {

    private ExecutorService asyncExecutor;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private MessageUtil messageUtil;
    private ValidationUtil validationUtil;
    private DatabaseManager databaseManager;
    private AuthDao authDao;
    private AuditDao auditDao;
    private PasswordHasher passwordHasher;
    private RateLimiter rateLimiter;
    private SessionManager sessionManager;
    private PremiumManager premiumManager;
    private AuthTimeoutTask timeoutTask;

    @Override
    public void onEnable() {
        this.asyncExecutor = Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()),
                r -> {
                    Thread t = new Thread(r, "AuthPlugin-AsyncWorker");
                    t.setDaemon(true);
                    return t;
                }
        );

        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(configManager.getDefaultLanguage());
        this.messageUtil = new MessageUtil(configManager, languageManager);
        this.validationUtil = new ValidationUtil(configManager);

        this.databaseManager = new DatabaseManager(this, configManager);
        try {
            this.databaseManager.initialize();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database connection!", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.authDao = new AuthDao(databaseManager, asyncExecutor);
        this.auditDao = new AuditDao(databaseManager, asyncExecutor);
        this.passwordHasher = new PasswordHasher(configManager, asyncExecutor);
        this.rateLimiter = new RateLimiter(configManager);
        this.sessionManager = new SessionManager(configManager);
        this.premiumManager = new PremiumManager(asyncExecutor);
        this.timeoutTask = new AuthTimeoutTask(this, configManager, messageUtil, sessionManager, authDao);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerPreLoginListener(configManager, rateLimiter, messageUtil), this);
        pm.registerEvents(new PlayerJoinQuitListener(this, messageUtil, sessionManager, premiumManager, authDao, timeoutTask, languageManager), this);
        pm.registerEvents(new PlayerRestrictionListener(sessionManager, messageUtil), this);

        registerCommands();
    }

    private void registerCommands() {
        RegisterCommand regCmd = new RegisterCommand(this, configManager, messageUtil, validationUtil, passwordHasher, sessionManager, authDao, timeoutTask);
        registerCommand("register", regCmd);

        LoginCommand loginCmd = new LoginCommand(this, configManager, messageUtil, passwordHasher, rateLimiter, sessionManager, authDao, timeoutTask);
        registerCommand("login", loginCmd);

        ChangePasswordCommand changePassCmd = new ChangePasswordCommand(this, configManager, messageUtil, validationUtil, passwordHasher, sessionManager, authDao);
        registerCommand("changepassword", changePassCmd);

        PremiumCommand premiumCmd = new PremiumCommand(this, messageUtil, premiumManager, sessionManager, authDao);
        registerCommand("premium", premiumCmd);
        registerCommand("premiumconfirm", premiumCmd);

        AdminUnregisterCommand adminUnregisterCmd = new AdminUnregisterCommand(this, messageUtil, sessionManager, authDao, auditDao);
        registerCommand("unregister", adminUnregisterCmd);

        AdminUnpremiumCommand adminUnpremiumCmd = new AdminUnpremiumCommand(this, messageUtil, sessionManager, authDao, auditDao);
        registerCommand("unpremium", adminUnpremiumCmd);

        LanguageCommand langCmd = new LanguageCommand(this, configManager, messageUtil, languageManager);
        PluginCommand langPluginCmd = getCommand("language");
        if (langPluginCmd != null) {
            langPluginCmd.setExecutor(langCmd);
            langPluginCmd.setTabCompleter(langCmd);
        }

        AdminManageCommand adminManageCmd = new AdminManageCommand(this, configManager, messageUtil, sessionManager, authDao, auditDao);
        PluginCommand authAdmin = getCommand("authadmin");
        if (authAdmin != null) {
            authAdmin.setExecutor(adminManageCmd);
            authAdmin.setTabCompleter(adminManageCmd);
        }
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            if (executor instanceof org.bukkit.command.TabCompleter tc) {
                cmd.setTabCompleter(tc);
            }
        } else {
            getLogger().warning("Could not register command /" + name);
        }
    }

    @Override
    public void onDisable() {
        if (timeoutTask != null) {
            timeoutTask.cancelAll();
        }

        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        if (asyncExecutor != null) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
            }
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public PasswordHasher getPasswordHasher() {
        return passwordHasher;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public AuthDao getAuthDao() {
        return authDao;
    }

    public AuditDao getAuditDao() {
        return auditDao;
    }
}
