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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CheckpointCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;

    public CheckpointCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!label.equalsIgnoreCase("pkcheckpoint") && !label.equalsIgnoreCase("pkcp")) || args.length == 0) return false;

        String name = args[0];
        if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.unknown_name"));
            return true;
        }

        Player player = (Player) sender;
        int x = 0, y = 0, z = 0, id;
        boolean remove = false;
        if (args.length == 2) {
            id = Integer.parseInt(args[1]);
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                if (id > 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size()) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.checkpoint_bad_count"));
                    return true;
                }
            } else {
                if (id != 1) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.checkpoint_bad_count"));
                    return true;
                }
            }
        } else if (args.length == 1) {
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                id = 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size();
            } else {
                id = 1;
            }
        } else if (remove && plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name)) && Integer.parseInt(args[1]) > plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size()) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.checkpoint_bad_count"));
            return true;
        } else {
            id = Integer.parseInt(args[1]);
            if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name))) {
                if (id > 1+plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size()) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.checkpoint_bad_count"));
                    return true;
                }
            } else {
                if (id != 1) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.checkpoint_bad_count"));
                    return true;
                }
            }
        }

        if (args.length >= 5) {
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            z = Integer.parseInt(args[4]);
        } else if (args.length >= 3 && args[2].equals("remove")) {
            remove = true;
        } else {
            Location loc = player.getLocation();
            x = loc.getBlockX();
            y = loc.getBlockY()-1;
            z = loc.getBlockZ();
        }
        if (remove) {
            int lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size();
            String[] xyz = plugin.getConfig().getString(String.format("parkours.%s.checkpoints.%s", name, id)).split(" ");
            for (int i = id+1; i<=lines; i++) {
                String text = plugin.getConfig().getString(String.format("parkours.%s.checkpoints.%s", name, i));
                plugin.getConfig().set(String.format("parkours.%s.checkpoints.%s", name, i-1), text);
            }
            plugin.getConfig().set(String.format("parkours.%s.checkpoints.%s", name, lines), null);
            plugin.saveConfig();
            new Location(player.getWorld(), Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2])).getBlock().setType(Material.AIR);
            Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.checkpoint_delete"), id));
            return true;
        }
        Block block = new Location(player.getWorld(), x, y+1, z).getBlock();

        if (block.getType() != Material.AIR) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.bad_place"));
            return true;
        }
        plugin.getConfig().set(String.format("parkours.%s.checkpoints.%s", name, id), String.format("%s %s %s", x, y+1, z));
        plugin.saveConfig();

        block.setType(Material.STONE_PRESSURE_PLATE);
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);
        blockData.set(new NamespacedKey(plugin, "parkourName"), PersistentDataType.STRING, name);
        blockData.set(new NamespacedKey(plugin, "checkpointId"), PersistentDataType.INTEGER, id);
        Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.create_checkpoint"), name, id));


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
            case 2 -> {
                int lines = 0;
                if (plugin.getConfig().contains(String.format("parkours.%s.lb", args[0]))) {
                    lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", args[0])).getKeys(false).size();
                }
                yield IntStream.range(1, lines+2).mapToObj(Integer::toString).collect(Collectors.toList());
            }
            case 3 -> List.of(block.getX()+"", "remove");
            case 4 -> {
                if (args[2].equals("remove")) yield Collections.singletonList(block.getX()+"");
                yield Collections.singletonList(block.getY() + "");
            }
            case 5 -> {
                if (args[2].equals("remove")) yield Collections.singletonList(block.getY()+"");
                yield Collections.singletonList(block.getZ() + "");
            }
            case 6 -> {
                if (args[2].equals("remove")) yield Collections.singletonList(block.getZ()+"");
                yield List.of();
            }
            default -> List.of();
        };
    }
}
