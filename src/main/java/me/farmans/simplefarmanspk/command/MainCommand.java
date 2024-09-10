package me.farmans.simplefarmanspk.command;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;

public class MainCommand implements Listener, TabExecutor {
    SimpleFarmansPK plugin;
    public MainCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pkmain")) return false;

        String name;
        if (args.length == 0) {
            name = "null";
            Func.sendMessage(sender, "Zrušil jsi hlavní parkour");
        }
        else {
            name = args[0];
            if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
                Func.sendMessage(sender, "Jméno parkouru neexistuje");
                return true;
            }
            Func.sendMessage(sender, String.format("Nastavil jsi %s jako hlavní parkour", name));
        }

        plugin.getConfig().set("main", name);
        plugin.saveConfig();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> names = Collections.singletonList("name");
        if (plugin.getConfig().contains("parkours") && plugin.getConfig().getConfigurationSection("parkours").getKeys(false).size() != 0) {
            names = plugin.getConfig().getConfigurationSection("parkours").getKeys(false).stream().toList();
        }
        switch (args.length) {
            case 1:
                return names;
        }
        return List.of();
    }
}
