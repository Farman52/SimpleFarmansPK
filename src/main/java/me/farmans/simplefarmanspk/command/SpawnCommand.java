package me.farmans.simplefarmanspk.command;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpawnCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;

    public SpawnCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pkspawn") || args.length == 0) return false;

        Player player = (Player)sender;
        int x, y, z;
        String f;
        String name = args[0];
        if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.unknown_name"));
            return true;
        }
        Location loc = player.getLocation();
        if (args.length >= 4) {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            if (args.length >= 5) f = args[4];
            else f = String.format("%.1f", loc.getYaw()).replace(",", ".");
        } else {
            x = loc.getBlockX();
            y = loc.getBlockY()-1;
            z = loc.getBlockZ();
            f = String.format("%.1f", loc.getYaw()).replace(",", ".");
        }

        plugin.getConfig().set(String.format("parkours.%s.spawn", name), String.format("%s.5 %s %s.5 %s", x, y+1, z, f));
        plugin.saveConfig();

        Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.create_spawn"), name));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        Block block = player.getTargetBlock(null, 5);
        List<String> names = Collections.singletonList("name");
        if (plugin.getConfig().contains("parkours") && !plugin.getConfig().getConfigurationSection("parkours").getKeys(false).isEmpty()) {
            names = plugin.getConfig().getConfigurationSection("parkours").getKeys(false).stream().toList();
        }
        return switch (args.length) {
            case 1 -> names;
            case 2 -> Collections.singletonList(block.getX() + "");
            case 3 -> Collections.singletonList(block.getY() + "");
            case 4 -> Collections.singletonList(block.getZ() + "");
            case 5 -> Collections.singletonList(String.format("%.1f", player.getLocation().getYaw()).replace(",", "."));
            default -> List.of();
        };
    }
}
