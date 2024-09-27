package me.farmans.simplefarmanspk.util;

import org.bukkit.configuration.file.YamlConfiguration;

public class SetupSettingsConfig {
    public SetupSettingsConfig(YamlConfiguration yml) {
        yml.addDefault("hologram_difference", 0.3d);
        yml.addDefault("visibility_cooldown", 1d);
        yml.addDefault("action_bar_tick_timer", 5);
        yml.addDefault("hologram_max_lines", 21);
    }
}
