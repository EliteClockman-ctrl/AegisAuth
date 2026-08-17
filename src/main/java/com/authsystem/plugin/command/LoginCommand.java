package com.authsystem.plugin.command;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.PasswordHasher;
import com.authsystem.plugin.security.RateLimiter;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.task.AuthTimeoutTask;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class LoginCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final PasswordHasher passwordHasher;
    private final RateLimiter rateLimiter;
    private final SessionManager sessionManager;
    private final AuthDao authDao;
    private final AuthTimeoutTask timeoutTask;

    public LoginCommand(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil, PasswordHasher passwordHasher,
                        RateLimiter rateLimiter, SessionManager sessionManager,
                        AuthDao authDao, AuthTimeoutTask timeoutTask) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.passwordHasher = passwordHasher;
        this.rateLimiter = rateLimiter;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
        this.timeoutTask = timeoutTask;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            messageUtil.sendMessage(sender, MessageKey.PLAYER_ONLY);
            return true;
        }

        UUID uniqueId = player.getUniqueId();
        if (sessionManager.isLoggedIn(uniqueId)) {
            messageUtil.sendMessage(player, MessageKey.LOGIN_ALREADY);
            return true;
        }

        if (args.length < 1) {
            messageUtil.sendMessage(player, MessageKey.LOGIN_USAGE);
            return true;
        }

        String password = args[0];
        InetSocketAddress socketAddress = player.getAddress();
        String ip = socketAddress != null ? socketAddress.getAddress().getHostAddress() : "127.0.0.1";

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            if (optAccount.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(player, MessageKey.LOGIN_NOT_REGISTERED);
                });
                return;
            }

            AuthAccount account = optAccount.get();

            passwordHasher.verifyPassword(account.getPasswordHash(), password.toCharArray()).thenAccept(matched -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) return;

                    if (matched) {
                        rateLimiter.clearFailedAttempts(ip);
                        sessionManager.saveSession(uniqueId, ip);
                        player.setInvulnerable(false);
                        timeoutTask.cancelTimeout(uniqueId);
                        messageUtil.clearDisplay(player);

                        authDao.updateLoginMetadata(uniqueId, ip, Instant.now());

                        messageUtil.sendMessage(player, MessageKey.LOGIN_SUCCESS);
                        messageUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                    } else {
                        rateLimiter.recordFailedAttempt(ip);
                        if (rateLimiter.isLocked(ip)) {
                            player.kick(messageUtil.getMessage(MessageKey.RATE_LIMITED_KICK, Map.of("minutes", String.valueOf(configManager.getLockoutDurationMinutes()))));
                        } else {
                            player.kick(messageUtil.getMessage(MessageKey.LOGIN_WRONG_PASSWORD));
                        }
                    }
                });
            });
        });

        return true;
    }
}
