package com.authsystem.plugin.command;

import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuditDao;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class AdminUnregisterCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final MessageUtil messageUtil;
    private final SessionManager sessionManager;
    private final AuthDao authDao;
    private final AuditDao auditDao;

    public AdminUnregisterCommand(JavaPlugin plugin, MessageUtil messageUtil,
                                  SessionManager sessionManager, AuthDao authDao, AuditDao auditDao) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
        this.auditDao = auditDao;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.unregister")) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_UNREGISTER_USAGE);
            return true;
        }

        String targetPlayerName = args[0];

        Player onlineTarget = Bukkit.getPlayerExact(targetPlayerName);
        if (onlineTarget != null && onlineTarget.isOnline()) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_UNREGISTER_PLAYER_ONLINE, Map.of("player", targetPlayerName));
            return true;
        }

        authDao.deleteByUsername(targetPlayerName).thenAccept(deleted -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (deleted) {
                    String adminName = sender.getName();
                    auditDao.log(adminName, targetPlayerName, "UNREGISTER", "Account deleted from database.");

                    messageUtil.sendMessage(sender, MessageKey.ADMIN_UNREGISTER_SUCCESS, Map.of("player", targetPlayerName));
                } else {
                    messageUtil.sendMessage(sender, MessageKey.ADMIN_PLAYER_NOT_FOUND, Map.of("player", targetPlayerName));
                }
            });
        });

        return true;
    }
}
