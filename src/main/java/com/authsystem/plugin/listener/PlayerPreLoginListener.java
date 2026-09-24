package com.authsystem.plugin.listener;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.security.RateLimiter;
import com.authsystem.plugin.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.Map;
import java.util.regex.Pattern;

public class PlayerPreLoginListener implements Listener {

    private static final Pattern VALID_USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    private final ConfigManager configManager;
    private final RateLimiter rateLimiter;
    private final MessageUtil messageUtil;

    public PlayerPreLoginListener(ConfigManager configManager, RateLimiter rateLimiter, MessageUtil messageUtil) {
        this.configManager = configManager;
        this.rateLimiter = rateLimiter;
        this.messageUtil = messageUtil;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        String username = event.getName();
        InetAddress address = event.getAddress();
        String ip = address != null ? address.getHostAddress() : "127.0.0.1";

        if (username == null || !VALID_USERNAME.matcher(username).matches()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, 
                    messageUtil.parse("<red>Tên người chơi không hợp lệ! Chỉ cho phép 3-16 ký tự a-z, 0-9 và _.</red>"));
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(username)) {
                Component kickReason = messageUtil.getMessage(MessageKey.DUPLICATE_ONLINE_KICK);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickReason);
                return;
            }
        }

        if (rateLimiter.isLocked(ip)) {
            Component kickReason = messageUtil.getMessage(
                    MessageKey.RATE_LIMITED_KICK,
                    Map.of("minutes", String.valueOf(configManager.getLockoutDurationMinutes()))
            );
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickReason);
        }
    }
}