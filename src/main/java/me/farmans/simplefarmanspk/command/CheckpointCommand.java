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

import javax.naming.Name;
import java.util.Collections;
import java.util.List;

public class CheckpointCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;

    public CheckpointCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!label.equalsIgnoreCase("pkcheckpoint") && !label.equalsIgnoreCase("pkcp")) || args.length == 0) return false;

        String name = args[0];
        if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
            Func.sendMessage(sender, "Jmeno parkouru neexistuje");
            return true;
        }

        Player player = (Player) sender;
        int x, y, z, id;
        if (args.length == 2) {
            id = Integer.parseInt(args[1]);
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                if (id > 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size()) {
                    Func.sendMessage(sender, "Pocet checkpointu nevychazi");
                    return true;
                }
            } else {
                if (id != 1) {
                    Func.sendMessage(sender, "Pocet checkpointu nevychazi");
                    return true;
                }
            }
        } else if (args.length == 1) {
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                id = 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size();
            } else {
                id = 1;
            }
        } else {
            id = Integer.parseInt(args[1]);
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                if (id > 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size()) {
                    Func.sendMessage(sender, "Pocet checkpointu nevychazi");
                    return true;
                }
            } else {
                if (id != 1) {
                    Func.sendMessage(sender, "Pocet checkpointu nevychazi");
                    return true;
                }
            }
        }
        if (args.length >= 5) {
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            z = Integer.parseInt(args[4]);
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
        plugin.getConfig().set(String.format("parkours.%s.checkpoints.%s", name, id), String.format("%s %s %s", x, y+1, z));
        plugin.saveConfig();

        block.setType(Material.STONE_PRESSURE_PLATE);
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);
        blockData.set(new NamespacedKey(plugin, "parkourName"), PersistentDataType.STRING, name);
        blockData.set(new NamespacedKey(plugin, "checkpointId"), PersistentDataType.INTEGER, id);
        Func.sendMessage(sender, "Parkour " + name + " Checkpoint " + id + " byl vytvoren");


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player)sender;
        Block block = player.getTargetBlock(null, 5);
        List<String> names = Collections.singletonList("name");
        if (plugin.getConfig().contains("parkours") && plugin.getConfig().getConfigurationSection("parkours").getKeys(false).size() != 0) {
            names = plugin.getConfig().getConfigurationSection("parkours").getKeys(false).stream().toList();
        }
        switch (args.length) {
            case 1:
                return names;
            case 2:
                return Collections.singletonList("count");
            case 3:
                return Collections.singletonList(block.getX()+"");
            case 4:
                return Collections.singletonList(block.getY()+"");
            case 5:
                return Collections.singletonList(block.getZ()+"");
        }
        return List.of();
    }
}
