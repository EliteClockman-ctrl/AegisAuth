package com.authsystem.plugin.util;

import com.authsystem.plugin.config.ConfigManager;
import com.authsystem.plugin.config.MessageKey;
import com.authsystem.plugin.language.LanguageManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Map;

public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ConfigManager configManager;
    private LanguageManager languageManager;

    public MessageUtil(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public MessageUtil(ConfigManager configManager, LanguageManager languageManager) {
        this.configManager = configManager;
        this.languageManager = languageManager;
    }

    public void setLanguageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public String resolveLanguage(Audience audience) {
        if (languageManager != null) {
            return languageManager.getGlobalLanguage();
        }
        return configManager.getDefaultLanguage();
    }

    public Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(message);
    }

    public Component parse(String message, Map<String, String> placeholders) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        if (placeholders == null || placeholders.isEmpty()) {
            return MINI_MESSAGE.deserialize(message);
        }

        String formatted = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                formatted = formatted.replace("{" + entry.getKey() + "}", entry.getValue())
                                     .replace("<" + entry.getKey() + ">", entry.getValue());
            }
        }

        TagResolver[] resolvers = placeholders.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .map(entry -> Placeholder.unparsed(entry.getKey(), entry.getValue()))
                .toArray(TagResolver[]::new);

        return MINI_MESSAGE.deserialize(formatted, TagResolver.resolver(resolvers));
    }

    public Component getMessage(MessageKey key) {
        return getMessage(key, (String) null);
    }

    public Component getMessage(MessageKey key, String lang) {
        return parse(configManager.getPrefix(lang) + configManager.getRawMessage(key, lang));
    }

    public Component getMessage(MessageKey key, Map<String, String> placeholders) {
        return getMessage(key, placeholders, null);
    }

    public Component getMessage(MessageKey key, Map<String, String> placeholders, String lang) {
        return parse(configManager.getPrefix(lang) + configManager.getRawMessage(key, lang), placeholders);
    }

    public Component getRawMessage(MessageKey key) {
        return getRawMessage(key, (String) null);
    }

    public Component getRawMessage(MessageKey key, String lang) {
        return parse(configManager.getRawMessage(key, lang));
    }

    public Component getRawMessage(MessageKey key, Map<String, String> placeholders) {
        return getRawMessage(key, placeholders, null);
    }

    public Component getRawMessage(MessageKey key, Map<String, String> placeholders, String lang) {
        return parse(configManager.getRawMessage(key, lang), placeholders);
    }

    public void sendMessage(Audience audience, MessageKey key) {
        String lang = resolveLanguage(audience);
        audience.sendMessage(getMessage(key, lang));
    }

    public void sendMessage(Audience audience, MessageKey key, Map<String, String> placeholders) {
        String lang = resolveLanguage(audience);
        audience.sendMessage(getMessage(key, placeholders, lang));
    }

    public void sendRawMessage(Audience audience, String rawMessage) {
        String lang = resolveLanguage(audience);
        audience.sendMessage(parse(configManager.getPrefix(lang) + rawMessage));
    }

    public void sendRawMessageWithoutPrefix(Audience audience, String rawMessage) {
        audience.sendMessage(parse(rawMessage));
    }

    public void sendActionBar(Player player, MessageKey key, Map<String, String> placeholders) {
        String lang = resolveLanguage(player);
        player.sendActionBar(getRawMessage(key, placeholders, lang));
    }

    public void sendTitle(Player player, MessageKey titleKey, MessageKey subtitleKey, Duration fadeIn, Duration stay, Duration fadeOut) {
        String lang = resolveLanguage(player);
        Component titleComp = getRawMessage(titleKey, lang);
        Component subComp = getRawMessage(subtitleKey, lang);
        Title title = Title.title(titleComp, subComp, Title.Times.times(fadeIn, stay, fadeOut));
        player.showTitle(title);
    }

    public void clearActionBar(Player player) {
        if (player != null && player.isOnline()) {
            player.sendActionBar(Component.empty());
        }
    }

    public void clearTitle(Player player) {
        if (player != null && player.isOnline()) {
            player.clearTitle();
        }
    }

    public void clearDisplay(Player player) {
        if (player != null && player.isOnline()) {
            player.sendActionBar(Component.empty());
            player.clearTitle();
        }
    }

    public void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
