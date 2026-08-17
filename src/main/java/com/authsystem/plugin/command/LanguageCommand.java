package com.authsystem.plugin.command;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.language.LanguageManager;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Sound;
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

public class LanguageCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final LanguageManager languageManager;

    public LanguageCommand(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil, LanguageManager languageManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.language")) {
            messageUtil.sendMessage(sender, MessageKey.ADMIN_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            messageUtil.sendMessage(sender, MessageKey.LANGUAGE_USAGE);
            return true;
        }

        String langArg = args[0].toLowerCase();
        String targetLang;

        if (langArg.equalsIgnoreCase("vietnamese") || langArg.equalsIgnoreCase("vi") || langArg.equalsIgnoreCase("tiengviet")) {
            targetLang = "vi";
        } else if (langArg.equalsIgnoreCase("english") || langArg.equalsIgnoreCase("en")) {
            targetLang = "en";
        } else {
            messageUtil.sendMessage(sender, MessageKey.LANGUAGE_USAGE);
            return true;
        }

        languageManager.setGlobalLanguage(targetLang);
        configManager.setSystemLanguage(targetLang);

        MessageKey confirmKey = targetLang.equals("en") ? MessageKey.ADMIN_LANGUAGE_CHANGED_EN : MessageKey.ADMIN_LANGUAGE_CHANGED_VI;
        messageUtil.sendMessage(sender, confirmKey);

        if (sender instanceof Player player) {
            messageUtil.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("auth.admin.language")) {
            return List.of();
        }
        if (args.length == 1) {
            List<String> options = List.of("vietnamese", "english", "vi", "en");
            List<String> result = new ArrayList<>();
            for (String opt : options) {
                if (opt.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(opt);
                }
            }
            return result;
        }
        return List.of();
    }
}
