package com.authsystem.plugin.command;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.PasswordHasher;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import com.authsystem.plugin.util.ValidationUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public class ChangePasswordCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final ValidationUtil validationUtil;
    private final PasswordHasher passwordHasher;
    private final SessionManager sessionManager;
    private final AuthDao authDao;

    public ChangePasswordCommand(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil,
                                 ValidationUtil validationUtil, PasswordHasher passwordHasher,
                                 SessionManager sessionManager, AuthDao authDao) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.validationUtil = validationUtil;
        this.passwordHasher = passwordHasher;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            messageUtil.sendMessage(sender, MessageKey.PLAYER_ONLY);
            return true;
        }

        UUID uniqueId = player.getUniqueId();
        if (!sessionManager.isLoggedIn(uniqueId)) {
            messageUtil.sendMessage(player, MessageKey.CHANGEPASS_NOT_LOGGED_IN);
            return true;
        }

        if (args.length < 2) {
            messageUtil.sendMessage(player, MessageKey.CHANGEPASS_USAGE);
            return true;
        }

        String oldPass = args[0];
        String newPass = args[1];

        if (oldPass.equals(newPass)) {
            messageUtil.sendMessage(player, MessageKey.CHANGEPASS_SAME_AS_OLD);
            return true;
        }

        ValidationUtil.ValidationResult valResult = validationUtil.validatePassword(newPass);
        if (!valResult.isValid()) {
            Map<String, String> placeholders = Map.of(
                    "min", String.valueOf(configManager.getPasswordMinLength()),
                    "max", String.valueOf(configManager.getPasswordMaxLength())
            );
            messageUtil.sendMessage(player, valResult.getErrorKey(), placeholders);
            return true;
        }

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            if (optAccount.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(player, MessageKey.LOGIN_NOT_REGISTERED);
                });
                return;
            }

            AuthAccount account = optAccount.get();

            passwordHasher.verifyPassword(account.getPasswordHash(), oldPass.toCharArray()).thenAccept(oldMatched -> {
                if (!oldMatched) {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        messageUtil.sendMessage(player, MessageKey.CHANGEPASS_WRONG_OLD);
                        messageUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                    });
                    return;
                }

                passwordHasher.hashPassword(newPass.toCharArray()).thenAccept(newHash -> {
                    authDao.updatePassword(uniqueId, newHash).thenAccept(updated -> {
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            if (!player.isOnline()) return;

                            if (updated) {
                                messageUtil.sendMessage(player, MessageKey.CHANGEPASS_SUCCESS);
                                messageUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                            } else {
                                messageUtil.sendRawMessage(player, "<red>Lỗi cập nhật mật khẩu trong Database!</red>");
                            }
                        });
                    });
                });
            });
        });

        return true;
    }
}
