package com.authsystem.plugin.command;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.PasswordHasher;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.task.AuthTimeoutTask;
import com.authsystem.plugin.util.MessageUtil;
import com.authsystem.plugin.util.ValidationUtil;
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

public class RegisterCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final ValidationUtil validationUtil;
    private final PasswordHasher passwordHasher;
    private final SessionManager sessionManager;
    private final AuthDao authDao;
    private final AuthTimeoutTask timeoutTask;

    public RegisterCommand(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil,
                           ValidationUtil validationUtil, PasswordHasher passwordHasher,
                           SessionManager sessionManager, AuthDao authDao, AuthTimeoutTask timeoutTask) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.validationUtil = validationUtil;
        this.passwordHasher = passwordHasher;
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
            messageUtil.sendMessage(player, MessageKey.REGISTER_ALREADY);
            return true;
        }

        if (args.length < 2) {
            messageUtil.sendMessage(player, MessageKey.REGISTER_USAGE);
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            messageUtil.sendMessage(player, MessageKey.REGISTER_PASSWORD_MISMATCH);
            return true;
        }

        ValidationUtil.ValidationResult valResult = validationUtil.validatePassword(password);
        if (!valResult.isValid()) {
            Map<String, String> placeholders = Map.of(
                    "min", String.valueOf(configManager.getPasswordMinLength()),
                    "max", String.valueOf(configManager.getPasswordMaxLength())
            );
            messageUtil.sendMessage(player, valResult.getErrorKey(), placeholders);
            return true;
        }

        InetSocketAddress socketAddress = player.getAddress();
        String ip = socketAddress != null ? socketAddress.getAddress().getHostAddress() : "127.0.0.1";

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            if (optAccount.isPresent()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(player, MessageKey.REGISTER_ALREADY);
                });
                return;
            }

            passwordHasher.hashPassword(password.toCharArray()).thenAccept(hash -> {
                Instant now = Instant.now();
                AuthAccount newAccount = new AuthAccount(
                        uniqueId,
                        player.getName(),
                        hash,
                        false,
                        null,
                        ip,
                        ip,
                        now,
                        now
                );

                authDao.save(newAccount).thenAccept(saved -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (!player.isOnline()) return;

                        if (saved) {
                            sessionManager.saveSession(uniqueId, ip);
                            player.setInvulnerable(false);
                            timeoutTask.cancelTimeout(uniqueId);
                            messageUtil.clearDisplay(player);

                            messageUtil.sendMessage(player, MessageKey.REGISTER_SUCCESS);
                            messageUtil.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                        } else {
                            messageUtil.sendRawMessage(player, "<red>Lỗi lưu trữ tài khoản! Vui lòng liên hệ Admin.</red>");
                        }
                    });
                });
            });
        });

        return true;
    }
}
