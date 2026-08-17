package com.authsystem.plugin.command;

import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.PremiumManager;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PremiumCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final MessageUtil messageUtil;
    private final PremiumManager premiumManager;
    private final SessionManager sessionManager;
    private final AuthDao authDao;

    public PremiumCommand(JavaPlugin plugin, MessageUtil messageUtil, PremiumManager premiumManager,
                          SessionManager sessionManager, AuthDao authDao) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
        this.premiumManager = premiumManager;
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
            messageUtil.sendMessage(player, MessageKey.ACTION_BLOCKED);
            return true;
        }

        String cmdName = command.getName().toLowerCase();

        if (cmdName.equals("premium")) {
            handlePremiumRequest(player);
        } else if (cmdName.equals("premiumconfirm")) {
            handlePremiumConfirm(player);
        }

        return true;
    }

    private void handlePremiumRequest(Player player) {
        UUID uniqueId = player.getUniqueId();

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            if (optAccount.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(player, MessageKey.PREMIUM_NOT_REGISTERED);
                });
                return;
            }

            if (optAccount.get().isPremium()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(player, MessageKey.PREMIUM_ALREADY);
                });
                return;
            }

            premiumManager.isMojangAccount(player.getName()).thenAccept(isMojang -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) return;

                    if (!isMojang) {
                        messageUtil.sendMessage(player, MessageKey.PREMIUM_NOT_MOJANG);
                        messageUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                        return;
                    }

                    premiumManager.createPendingConfirmation(uniqueId);
                    messageUtil.sendMessage(player, MessageKey.PREMIUM_PROMPT_CONFIRM);
                    messageUtil.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                });
            });
        });
    }

    private void handlePremiumConfirm(Player player) {
        UUID uniqueId = player.getUniqueId();

        if (!premiumManager.hasPendingConfirmation(uniqueId)) {
            messageUtil.sendMessage(player, MessageKey.PREMIUM_NO_PENDING);
            return;
        }

        premiumManager.removePendingConfirmation(uniqueId);

        authDao.setPremium(uniqueId, true).thenAccept(success -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;

                if (success) {
                    messageUtil.sendMessage(player, MessageKey.PREMIUM_CONFIRMED);
                    messageUtil.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                } else {
                    messageUtil.sendRawMessage(player, "<red>Lỗi kích hoạt chế độ Premium trong Database!</red>");
                }
            });
        });
    }
}
