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

public class PlayerPreLoginListener implements Listener {

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
        String ip = address.getHostAddress();

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
