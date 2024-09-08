package me.farmans.simplefarmanspk.command;

import com.jeff_media.customblockdata.CustomBlockData;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;


public class SetupCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;

    public SetupCommand(SimpleFarmansPK plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pksetup") || args.length == 0) return false;

        Player player = (Player)sender;
        int x, y, z;
        String name = args[0];
        if (args.length >= 4) {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } else {
            Location loc = player.getLocation();
            x = loc.getBlockX();
            y = loc.getBlockY()-1;
            z = loc.getBlockZ();
        }
        Block block = new Location(player.getWorld(), x, y+1, z).getBlock();

        if (block.getType() != Material.AIR) {
            Func.sendMessage(sender, "Nemuzu polozit bro, musi byt vzduch");
            return true;
        }
        block.setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        plugin.getConfig().set(String.format("parkours.%s.start", name), String.format("%s %s %s", x, y+1, z)); //parkours."epicpk".start: X Y Z
        plugin.getConfig().set(String.format("parkours.%s.ready", name), true);
        plugin.saveConfig();
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);
        blockData.set(new NamespacedKey(plugin, "parkourName"), PersistentDataType.STRING, name);
        Func.sendMessage(sender, "Parkour " + name + " start byl vytvoren");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        Block block = player.getTargetBlock(null, 5);
        switch (args.length) {
            case 1:
                return Collections.singletonList("name");
            case 2:
                return Collections.singletonList(block.getX() + "");
            case 3:
                return Collections.singletonList(block.getY() + "");
            case 4:
                return Collections.singletonList(block.getZ() + "");
        }
        return List.of();
    }
}
