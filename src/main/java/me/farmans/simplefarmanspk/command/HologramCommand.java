package me.farmans.simplefarmanspk.command;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HologramCommand implements CommandExecutor, TabExecutor {
    SimpleFarmansPK plugin;
    public HologramCommand(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((!label.equalsIgnoreCase("pkhologram") && !label.equalsIgnoreCase("pkholo") && !label.equalsIgnoreCase("pkleaderboard") && !label.equalsIgnoreCase("pklb")) || args.length < 2) return false;

        String name = args[0];
        if (!plugin.getConfig().contains(String.format("parkours.%s", name))) {
            Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.unknown_name"));
            return true;
        }
        String type = args[1];
        switch (type) {
            case "set" -> {
                if (args.length < 4) return false;
                int line = Integer.parseInt(args[2]);
                if (line < 1 || (line != 1 && !plugin.getConfig().contains(String.format("parkours.%s.lb", name))) || (plugin.getConfig().contains(String.format("parkours.%s.lb", name)) && line > 1 + plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size())) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.hologram_bad_count"));
                    return true;
                }
                if (line > plugin.getSettingsConfig().getInt("hologram_max_lines")) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.hologram_max_lines"));
                    return true;
                }
                String text = args[3];
                if (args.length > 4) {
                    for (int i = 4; i < args.length; i++) {
                        text += " " + args[i];
                    }
                }
                text = text.replace("&", "§");
                plugin.getConfig().set(String.format("parkours.%s.lb.%s", name, line), text);
                plugin.saveConfig();
                Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.hologram_set_line"), text, line));
                return true;
            }
            case "add" -> {
                if (args.length < 3) return false;
                String text = args[2];
                int line = 1;
                if (plugin.getConfig().contains(String.format("parkours.%s.lb", name))) {
                    line += plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size();
                }
                if (line > plugin.getSettingsConfig().getInt("hologram_max_lines")) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.hologram_max_lines"));
                    return true;
                }
                if (args.length > 3) {
                    for (int i = 3; i < args.length; i++) {
                        text += " " + args[i];
                    }
                }
                text = text.replace("&", "§");
                plugin.getConfig().set(String.format("parkours.%s.lb.%s", name, line), text);
                plugin.saveConfig();
                Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.hologram_set_line"), text, line));
                return true;
            }
            case "remove" -> {
                if (args.length < 3) return false;
                int line = Integer.parseInt(args[2]);
                if (!plugin.getConfig().contains(String.format("parkours.%s.lb", name)) || line > plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size()) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.hologram_bad_count"));
                    return true;
                }
                int lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size();
                for (int i = line+1; i<=lines; i++) {
                    String text = plugin.getConfig().getString(String.format("parkours.%s.lb.%s", name, i));
                    plugin.getConfig().set(String.format("parkours.%s.lb.%s", name, i-1), text);
                }
                plugin.getConfig().set(String.format("parkours.%s.lb.%s", name, lines), null);
                plugin.saveConfig();
                Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.delete_line_hologram"), line));
                return true;
            }
            case "spawn" -> {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                plugin.getConfig().set(String.format("parkours.%s.lb_coords", name), String.format("%s %s %s", loc.getX(), loc.getY(), loc.getZ()));
                plugin.saveConfig();
                Func.spawnLeaderboard(sender, plugin, name);
                return true;
            }
            case "list" -> {
                if (!plugin.getConfig().contains(String.format("parkours.%s.lb", name))) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.bad_hologram"));
                    return true;
                }
                int lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size();
                if (lines == 0) Func.sendMessage(sender, plugin,plugin.getStringConfig().getString("commands.bad_hologram"));
                for (int i = 1; i <= lines; i++) {
                    String text = plugin.getConfig().getString(String.format("parkours.%s.lb.%s", name, i));
                    Func.sendMessage(sender, plugin, String.format("%s: %s", i, text));
                }
                return true;
            }
            case "reload" -> {
                if (!plugin.getConfig().contains(String.format("parkours.%s.lb_coords", name))) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.bad_hologram"));
                    return true;
                }
                Func.spawnLeaderboard(sender, plugin, name);
                Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.reload_hologram"), name));
                return true;
            }
            case "delete" -> {
                Player player = (Player) sender;
                Collection<Object> uuids = new HashSet<>();
                if (!plugin.getConfig().contains(String.format("parkours.%s.lb_coords", name))) {
                    Func.sendMessage(sender, plugin, plugin.getStringConfig().getString("commands.bad_hologram"));
                    return true;
                }
                String[] xyz = plugin.getConfig().getString(String.format("parkours.%s.lb_coords", name)).split(" ");
                if (plugin.getConfig().contains(String.format("parkours.%s.lb_uuids", name)) && !plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb_uuids", name)).getValues(false).isEmpty()) {
                    uuids = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb_uuids", name)).getValues(false).values();
                }
                if (!uuids.isEmpty()) {
                    Collection<Entity> entities = player.getWorld().getNearbyEntities(
                            new Location(player.getWorld(), Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2])), 2, plugin.getSettingsConfig().getInt("hologram_max_lines") / 3 + 1, 2,
                            (entity) -> entity.getType() == EntityType.ARMOR_STAND);
                    for (Entity entity : entities) {
                        if (uuids.contains(entity.getUniqueId().toString())) {
                            entity.remove();
                        }
                    }
                    plugin.getConfig().set(String.format("parkours.%s.lb_uuids", name), null);
                    plugin.saveConfig();
                }

                plugin.getConfig().set(String.format("parkours.%s.lb_coords", name), null);
                plugin.saveConfig();
                Func.sendMessage(sender, plugin, String.format(plugin.getStringConfig().getString("commands.delete_hologram"), name));
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> names = Collections.singletonList("name");
        if (plugin.getConfig().contains("parkours") && !plugin.getConfig().getConfigurationSection("parkours").getKeys(false).isEmpty()) {
            names = plugin.getConfig().getConfigurationSection("parkours").getKeys(false).stream().toList();
        }
        return switch (args.length) {
            case 1 -> names;
            case 2 -> List.of("spawn", "set", "add", "remove", "list", "delete", "reload");
            case 3 -> {
                int lines = 0;
                if (plugin.getConfig().contains(String.format("parkours.%s.lb", args[0]))) {
                    lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", args[0])).getKeys(false).size();
                }
                if (args[1].equalsIgnoreCase("set")) yield IntStream.range(1, lines+2).mapToObj(Integer::toString).collect(Collectors.toList());
                else if (args[1].equalsIgnoreCase("remove")) yield IntStream.range(1, lines+1).mapToObj(Integer::toString).collect(Collectors.toList());
                else if (args[1].equalsIgnoreCase("add")) yield Collections.singletonList("text");
                yield List.of();
            }
            case 4 -> {
                if (args[1].equalsIgnoreCase("set")) yield Collections.singletonList("text");
                yield List.of();
            }
            default -> List.of();
        };
    }
}
