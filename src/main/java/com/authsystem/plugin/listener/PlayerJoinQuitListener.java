package com.authsystem.plugin.listener;

import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.database.dao.AuthDao;
import com.authsystem.plugin.database.model.AuthAccount;
import com.authsystem.plugin.security.PremiumManager;
import com.authsystem.plugin.security.SessionManager;
import com.authsystem.plugin.task.AuthTimeoutTask;
import com.authsystem.plugin.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import com.authsystem.plugin.language.LanguageManager;

public class PlayerJoinQuitListener implements Listener {

    private final JavaPlugin plugin;
    private final MessageUtil messageUtil;
    private final SessionManager sessionManager;
    private final PremiumManager premiumManager;
    private final AuthDao authDao;
    private final AuthTimeoutTask timeoutTask;
    private final LanguageManager languageManager;

    public PlayerJoinQuitListener(JavaPlugin plugin, MessageUtil messageUtil, SessionManager sessionManager,
                                  PremiumManager premiumManager, AuthDao authDao, AuthTimeoutTask timeoutTask,
                                  LanguageManager languageManager) {
        this.plugin = plugin;
        this.messageUtil = messageUtil;
        this.sessionManager = sessionManager;
        this.premiumManager = premiumManager;
        this.authDao = authDao;
        this.timeoutTask = timeoutTask;
        this.languageManager = languageManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        InetSocketAddress address = player.getAddress();
        String ip = address != null ? address.getAddress().getHostAddress() : "127.0.0.1";

        player.setInvulnerable(true);
        applyAuthEffects(player);

        authDao.findByUniqueId(uniqueId).thenAccept(optAccount -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }

                if (optAccount.isPresent()) {
                    AuthAccount account = optAccount.get();

                    if (account.isPremium()) {
                        completeLogin(player, account, ip, MessageKey.PREMIUM_AUTO_LOGIN);
                        return;
                    }

                    if (sessionManager.isSessionValid(uniqueId, ip)) {
                        completeLogin(player, account, ip, MessageKey.LOGIN_SESSION_RESTORED);
                        return;
                    }
                }

                timeoutTask.startTimeout(player);
                messageUtil.sendTitle(
                        player,
                        MessageKey.TITLE_WELCOME_HEADER,
                        MessageKey.TITLE_WELCOME_FOOTER,
                        Duration.ofMillis(300),
                        Duration.ofSeconds(3),
                        Duration.ofMillis(500)
                );
            });
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        timeoutTask.cancelTimeout(uniqueId);
        premiumManager.removePendingConfirmation(uniqueId);

        player.setInvulnerable(false);
        removeAuthEffects(player);

        if (!sessionManager.isLoggedIn(uniqueId)) {
            sessionManager.markLoggedOut(uniqueId, true);
        } else {
            sessionManager.markLoggedOut(uniqueId, false);
        }
    }

    private void completeLogin(Player player, AuthAccount account, String ip, MessageKey successMessageKey) {
        sessionManager.saveSession(player.getUniqueId(), ip);
        player.setInvulnerable(false);
        removeAuthEffects(player);
        timeoutTask.cancelTimeout(player.getUniqueId());
        messageUtil.clearDisplay(player);

        authDao.updateLoginMetadata(player.getUniqueId(), ip, Instant.now());

        messageUtil.sendMessage(player, successMessageKey);
        messageUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
    }

    public static void applyAuthEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 1, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, PotionEffect.INFINITE_DURATION, 6, false, false, false));
    }

    public static void removeAuthEffects(Player player) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
    }
}