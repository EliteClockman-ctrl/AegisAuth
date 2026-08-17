package com.authsystem.plugin.command;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuditDao;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminManageCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final SessionManager sessionManager;
    private final AuthDao authDao;
    private final AuditDao auditDao;

    public AdminManageCommand(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil,
                              SessionManager sessionManager, AuthDao authDao, AuditDao auditDao) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
        this.auditDao = auditDao;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.manage")) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_USAGE);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            configManager.loadConfig();
            messageUtil.sendMessage(sender, MessageKey.ADMIN_RELOAD_SUCCESS);
            auditDao.log(sender.getName(), "SERVER", "RELOAD_CONFIG", "Configuration reloaded.");
            return true;
        }

        if (sub.equals("unregister")) {
            if (args.length < 2) {
                messageUtil.sendMessage(sender, MessageKey.ADMIN_UNREGISTER_USAGE);
                return true;
            }

            String targetPlayer = args[1];
            authDao.deleteByUsername(targetPlayer).thenAccept(deleted -> {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (deleted) {
                        Player onlineTarget = Bukkit.getPlayerExact(targetPlayer);
                        if (onlineTarget != null && onlineTarget.isOnline()) {
                            sessionManager.markLoggedOut(onlineTarget.getUniqueId(), true);
                            onlineTarget.kick(messageUtil.getMessage(MessageKey.TIMEOUT_KICK));
                        }

                        auditDao.log(sender.getName(), targetPlayer, "UNREGISTER", "Account deleted from database.");
                        messageUtil.sendMessage(sender, MessageKey.ADMIN_UNREGISTER_SUCCESS, Map.of("player", targetPlayer));
                    } else {
                        messageUtil.sendMessage(sender, MessageKey.ADMIN_PLAYER_NOT_FOUND, Map.of("player", targetPlayer));
                    }
                });
            });
            return true;
        }

        messageUtil.sendMessage(sender, MessageKey.ADMIN_USAGE);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.manage")) {
            return List.of();
        }
        if (args.length == 1) {
            List<String> subs = List.of("reload", "unregister");
            List<String> result = new ArrayList<>();
            for (String s : subs) {
                if (s.startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("unregister")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return List.of();
    }
}
