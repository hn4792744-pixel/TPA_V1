package me.uc_hussein.ultrastpa.service;

import me.uc_hussein.ultrastpa.ULTRASTPAPlugin;
import me.uc_hussein.ultrastpa.model.RequestType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class MessageService {
    private final ULTRASTPAPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final LegacyComponentSerializer serializer =
            LegacyComponentSerializer.legacyAmpersand();

    public MessageService(ULTRASTPAPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        messages.clear();

        String language = plugin.getConfig().getString(
                "plugin.default-language", "en");

        var file = new java.io.File(
                plugin.getDataFolder(),
                "lang/" + language + ".yml");

        if (!file.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }

        org.bukkit.configuration.file.YamlConfiguration yaml =
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);

        for (String key : yaml.getKeys(false)) {
            messages.put(key, yaml.getString(key, ""));
        }
    }

    public String raw(String key) {
        return color(replace(messages.getOrDefault(key, key)));
    }

    public void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(component(key, replacements));
    }

    public void actionbar(Player player, String key, String... replacements) {
        player.sendActionBar(component(key, replacements));
    }

    public Component component(String key, String... replacements) {
        return serializer.deserialize(
                replace(messages.getOrDefault(key, key), replacements));
    }

    private String replace(String text, String... replacements) {
        if (text == null) return "";

        text = text.replace("%prefix%",
                messages.getOrDefault("prefix", ""));

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(
                    replacements[i],
                    replacements[i + 1]
            );
        }

        return text;
    }

    private String color(String text) {
        return text == null ? "" : text;
    }

    public String type(RequestType type) {
        return switch (type) {
            case TPA -> messages.getOrDefault("request-types.tpa", "TPA");
            case TPAHERE -> messages.getOrDefault("request-types.tpahere", "TPAHERE");
            case TPA_ALL -> messages.getOrDefault("request-types.tpa-all", "TPA ALL");
        };
    }

    public String state(boolean enabled) {
        return messages.getOrDefault(
                enabled ? "state-on" : "state-off",
                enabled ? "&aENABLED" : "&cDISABLED"
        );
    }

    public void requestButton(Player target, String sender, String type) {
        String message = replace(
                messages.getOrDefault("request-received", ""),
                "%sender%", sender,
                "%type%", type
        );

        target.sendMessage(serializer.deserialize(message));
    }
}
