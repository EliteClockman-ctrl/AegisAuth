package com.authsystem.plugin.task;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthTimeoutTask {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final MessageUtil messageUtil;
    private final SessionManager sessionManager;
    private final AuthDao authDao;

    private final Map<UUID, BukkitTask> activeTasks = new ConcurrentHashMap<>();

    public AuthTimeoutTask(JavaPlugin plugin, ConfigManager configManager, MessageUtil messageUtil,
                           SessionManager sessionManager, AuthDao authDao) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messageUtil = messageUtil;
        this.sessionManager = sessionManager;
        this.authDao = authDao;
    }

    public void startTimeout(Player player) {
        cancelTimeout(player.getUniqueId());

        UUID uniqueId = player.getUniqueId();
        int totalSeconds = configManager.getAuthTimeoutSeconds();
        int reminderInterval = configManager.getReminderIntervalSeconds();

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            boolean isRegistered = optAccount.isPresent();

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline() || sessionManager.isLoggedIn(uniqueId)) {
                    return;
                }

                BukkitTask task = new BukkitRunnable() {
                    int remaining = totalSeconds;

                    @Override
                    public void run() {
                        if (!player.isOnline() || sessionManager.isLoggedIn(uniqueId)) {
                            cancelTimeout(uniqueId);
                            return;
                        }

                        if (remaining <= 0) {
                            cancel();
                            activeTasks.remove(uniqueId);
                            Component kickReason = messageUtil.getMessage(MessageKey.TIMEOUT_KICK);
                            player.kick(kickReason);
                            return;
                        }

                        MessageKey actionKey = isRegistered ? MessageKey.ACTIONBAR_LOGIN_COUNTDOWN : MessageKey.ACTIONBAR_REGISTER_COUNTDOWN;
                        messageUtil.sendActionBar(player, actionKey, Map.of("seconds", String.valueOf(remaining)));

                        if (remaining % reminderInterval == 0 || remaining == totalSeconds) {
                            MessageKey chatKey = isRegistered ? MessageKey.LOGIN_PROMPT : MessageKey.REGISTER_PROMPT;
                            messageUtil.sendMessage(player, chatKey);
                        }

                        remaining--;
                    }
                }.runTaskTimer(plugin, 0L, 20L);

                activeTasks.put(uniqueId, task);
            });
        });
    }

    public void cancelTimeout(UUID uniqueId) {
        BukkitTask task = activeTasks.remove(uniqueId);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null && player.isOnline()) {
            messageUtil.clearDisplay(player);
        }
    }

    public void cancelAll() {
        activeTasks.values().forEach(BukkitTask::cancel);
        activeTasks.clear();
    }
}
