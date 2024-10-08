package me.farmans.simplefarmanspk.command;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    SimpleFarmansPK plugin;

    public ReloadCommand(SimpleFarmansPK plugin) {this.plugin = plugin;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pkreload")) return false;

        plugin.reloadConfig();
        plugin.saveConfig();

        plugin.reloadStringConfig();
        plugin.saveStringConfig();

        plugin.reloadSettingsConfig();
        plugin.saveSettingsConfig();

        Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.reload_config"));

        return true;
    }
}
