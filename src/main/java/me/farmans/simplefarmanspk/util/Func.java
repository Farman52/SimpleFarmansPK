package me.farmans.simplefarmanspk.util;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Func {
    public static void sendMessage(CommandSender sender, SimpleFarmansPK plugin, String msg) {
        sender.sendMessage(String.format(plugin.getStringConfig().getString("chat_message"), msg));
    }

    public static void updateActionBar(SimpleFarmansPK plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            long time = System.currentTimeMillis();
            Map<String, List> times = PlayerInteraction.times;
            for (int i = 0; i<times.size(); i++) {
                String playerName = (String)times.keySet().toArray()[i];
                Player player = Bukkit.getPlayer(playerName);
                if (!player.isOnline()) continue;
                String finalTime = String.valueOf((double)(time-(long)times.get(playerName).get(1))/1000);
                String finalTimeDecimal = finalTime.split("\\.")[1];
                if (finalTimeDecimal.length() < 3) finalTime += "0".repeat(3-finalTimeDecimal.length());
                else if (finalTimeDecimal.length() > 3) finalTime = finalTime.split("\\.")[0]+"."+finalTimeDecimal.substring(0, 3);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format(plugin.getStringConfig().getString("action_bar"), finalTime)));
            }
        }, 0, 5);
    }

    public static void hideAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(plugin, online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getName()), true);
        plugin.saveConfig();
    }
    public static void showAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getName()), false);
        plugin.saveConfig();
    }

    public static void leaderboard(CommandSender sender, SimpleFarmansPK plugin, String name) {
        Player player = (Player) sender;
        final double DIFFERENCE = 0.3;
        int count = 0;
        LinkedHashMap<String, Object> sortedMap = new LinkedHashMap<>();
        if (plugin.getConfig().contains(String.format("parkours.%s.times", name))) {
            HashMap<String, Object> originalMap = (HashMap<String, Object>) plugin.getConfig().getConfigurationSection(String.format("parkours.%s.times", name)).getValues(false);
            sortedMap = originalMap.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare((Double) e1.getValue(), (Double) e2.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        if (!plugin.getConfig().contains(String.format("parkours.%s.lb", name))) {
            Func.sendMessage(sender, plugin,"Není nastavený leaderboard");
            return;
        }
        int lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size();
        if (lines == 0) Func.sendMessage(sender, plugin,"Není nastavený leaderboard");
        String[] xyz = plugin.getConfig().getString(String.format("parkours.%s.lb_coords", name)).split(" ");
        for (int i = lines; i > 0; i--) {
            String text = plugin.getConfig().getString(String.format("parkours.%s.lb.%s", name, lines-(i-1)));
            if (text.contains("%s")) {
                if (sortedMap.keySet().toArray().length <= count) {
                    text = String.format(text, plugin.getStringConfig().getString("hologram.nobody"));
                }
                else {
                    String playerName = (String) sortedMap.keySet().toArray()[count];
                    String time = sortedMap.get(playerName).toString();
                    String finalTimeDecimal = time.split("\\.")[1];
                    if (finalTimeDecimal.length() < 3) time += "0".repeat(3 - finalTimeDecimal.length());
                    else if (finalTimeDecimal.length() > 3) time = time.split("\\.")[0] + "." + finalTimeDecimal.substring(0, 3);
                    text = String.format(text, plugin.getStringConfig().getString("hologram.variable").replace("%p", playerName).replace("%t", time));
                }
                count++;
            }
            ArmorStand hologram = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), Double.valueOf(xyz[0]), Double.valueOf(xyz[1]), Double.valueOf(xyz[2])).add(0, DIFFERENCE*i, 0), EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
            hologram.setMarker(true);
            hologram.setCustomName(text);
            hologram.setMetadata("lb", new FixedMetadataValue(plugin, name));
        }
    }
}
