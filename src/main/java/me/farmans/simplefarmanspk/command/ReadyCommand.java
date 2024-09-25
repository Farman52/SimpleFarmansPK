package me.farmans.simplefarmanspk.command;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class ReadyCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;

    public ReadyCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pkready") || args.length == 0) return false;
        
        String name = args[0];
        if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.unknown_name"));
            return true;
        }
        if (plugin.getConfig().getBoolean(String.format("parkours.%s.ready", name))) {
            plugin.getConfig().set(String.format("parkours.%s.ready", name), false);
            plugin.saveConfig();
            Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.ready_false"), name));
            return true;
        }
        Set<String> issues = new HashSet<>();
        Set<String> keys = plugin.getConfig().getConfigurationSection(String.format("parkours.%s", name)).getKeys(false);
        Set<String> essentials = new HashSet<>(Arrays.asList("start", "spawn", "finish"));
        for (String essential : essentials) {
            if (!keys.contains(essential)) issues.add(essential);
        }
        if (!issues.isEmpty()) {
            Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.ready_todo"), String.join(", ", issues)));
            return true;
        }
        plugin.getConfig().set(String.format("parkours.%s.ready", name), true);
        plugin.saveConfig();
        Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.ready_true"), name));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> names = Collections.singletonList("name");
        if (plugin.getConfig().contains("parkours") && !plugin.getConfig().getConfigurationSection("parkours").getKeys(false).isEmpty()) {
            names = plugin.getConfig().getConfigurationSection("parkours").getKeys(false).stream().toList();
        }
        return switch (args.length) {
            case 1 -> names;
            default -> List.of();
        };
    }
}
