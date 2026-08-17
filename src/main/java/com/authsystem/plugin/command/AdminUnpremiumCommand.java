package com.authsystem.plugin.command;

import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuditDao;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AdminUnpremiumCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final MessageUtil messageUtil;
    private final SessionManager sessionManager;
    private final AuthDao authDao;
    private final AuditDao auditDao;

    public AdminUnpremiumCommand(JavaPlugin plugin, MessageUtil messageUtil,
                                 SessionManager sessionManager, AuthDao authDao, AuditDao auditDao) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
        this.auditDao = auditDao;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.unpremium")) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_UNPREMIUM_USAGE);
            return true;
        }

        String targetPlayerName = args[0];

        authDao.findByUsername(targetPlayerName).thenAccept(optAccount -> {
            if (optAccount.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(sender, MessageKey.ADMIN_PLAYER_NOT_FOUND, Map.of("player", targetPlayerName));
                });
                return;
            }

            AuthAccount account = optAccount.get();
            if (!account.isPremium()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    messageUtil.sendMessage(sender, MessageKey.ADMIN_UNPREMIUM_NOT_PREMIUM, Map.of("player", targetPlayerName));
                });
                return;
            }

            authDao.setPremium(account.getUniqueId(), false).thenAccept(updated -> {
                if (updated) {
                    sessionManager.invalidateSession(account.getUniqueId());

                    String adminName = sender.getName();
                    auditDao.log(adminName, targetPlayerName, "UNPREMIUM", "Admin removed premium mode.");

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        messageUtil.sendMessage(sender, MessageKey.ADMIN_UNPREMIUM_SUCCESS, Map.of("player", targetPlayerName));
                    });
                }
            });
        });

        return true;
    }
}
