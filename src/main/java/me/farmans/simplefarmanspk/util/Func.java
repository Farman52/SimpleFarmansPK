package me.farmans.simplefarmanspk.util;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
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
        }, 0, plugin.getSettingsConfig().getInt("action_bar_tick_timer"));
    }

    public static void hideAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(plugin, online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getUniqueId()), true);
        plugin.saveConfig();
    }
    public static void showAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getUniqueId()), false);
        plugin.saveConfig();
    }

    public static void spawnLeaderboard(CommandSender sender, SimpleFarmansPK plugin, String name) {
        Player player = (Player) sender;
        final double DIFFERENCE = plugin.getSettingsConfig().getDouble("hologram_difference");
        int count = 0;
        LinkedHashMap<String, Object> sortedMap = new LinkedHashMap<>();
        if (plugin.getConfig().contains(String.format("parkours.%s.times", name))) {
            HashMap<String, Object> originalMap = (HashMap<String, Object>) plugin.getConfig().getConfigurationSection(String.format("parkours.%s.times", name)).getValues(false);
            sortedMap = originalMap.entrySet().stream()
                    .sorted(Comparator.comparingDouble(e -> (Double) e.getValue())) //(e1, e2) -> Double.compare((Double) e1.getValue(), (Double) e2.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        }
        if (!plugin.getConfig().contains(String.format("parkours.%s.lb", name))) {
            Func.sendMessage(player, plugin, plugin.getStringConfig().getString("commands.bad_hologram"));
            return;
        }
        int lines = plugin.getConfig().getConfigurationSection(String.format("parkours.%s.lb", name)).getKeys(false).size();
        if (lines == 0) Func.sendMessage(player, plugin, plugin.getStringConfig().getString("commands.bad_hologram"));

        Collection<Object> uuids = new HashSet<>();
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

        for (int i = lines; i > 0; i--) {
            String text = plugin.getConfig().getString(String.format("parkours.%s.lb.%s", name, lines - (i - 1)));
            if (text.contains("%s")) {
                if (sortedMap.keySet().toArray().length <= count) {
                    text = String.format(text, plugin.getStringConfig().getString("hologram.nobody"));
                } else {
                    String uuid = (String) sortedMap.keySet().toArray()[count];
                    String playerName = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                    String time = sortedMap.get(uuid).toString();
                    String finalTimeDecimal = time.split("\\.")[1];
                    if (finalTimeDecimal.length() < 3) time += "0".repeat(3 - finalTimeDecimal.length());
                    else if (finalTimeDecimal.length() > 3)
                        time = time.split("\\.")[0] + "." + finalTimeDecimal.substring(0, 3);
                    text = String.format(text, plugin.getStringConfig().getString("hologram.variable").replace("%p", playerName).replace("%t", time));
                }
                count++;
            }
            ArmorStand hologram = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), Double.parseDouble(xyz[0]), Double.parseDouble(xyz[1]), Double.parseDouble(xyz[2])).add(0, DIFFERENCE * i, 0), EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setCustomNameVisible(true);
            hologram.setGravity(false);
            hologram.setMarker(true);
            hologram.setCustomName(text);
            plugin.getConfig().set(String.format("parkours.%s.lb_uuids.%s", name, i), hologram.getUniqueId().toString());
            plugin.saveConfig();
        }
    }

    public static void giveParkourTools(SimpleFarmansPK plugin, Player player, String name) {
        player.getInventory().clear();

        String playerName = player.getName();
        ItemStack checkpoint = new ItemStack(Material.SLIME_BALL);
        ItemMeta checkpointData = checkpoint.getItemMeta();
        checkpointData.setDisplayName(plugin.getStringConfig().getString("item.checkpoint"));
        PersistentDataContainer checkpointCustomData = checkpointData.getPersistentDataContainer();
        String xyz = plugin.getConfig().getString(String.format("parkours.%s.spawn", name));
        if (PlayerInteraction.checkpoints.containsKey(playerName)) {
            int id = PlayerInteraction.checkpoints.get(playerName).size();
            xyz = plugin.getConfig().getString(String.format("parkours.%s.checkpoints.%s", name, id));
        }
        checkpointCustomData.set(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING, xyz);
        checkpoint.setItemMeta(checkpointData);

        ItemStack leave = new ItemStack(Material.RED_DYE);
        ItemMeta leaveData = leave.getItemMeta();
        leaveData.setDisplayName(plugin.getStringConfig().getString("item.leave"));
        PersistentDataContainer leaveCustomData = leaveData.getPersistentDataContainer();
        leaveCustomData.set(new NamespacedKey(plugin, "leave"), PersistentDataType.STRING, name);
        leave.setItemMeta(leaveData);

        boolean hidden = false;
        if (plugin.getConfig().contains(String.format("hidden.%s", player.getUniqueId()))) {
            hidden = plugin.getConfig().getBoolean(String.format("hidden.%s", player.getUniqueId()));
        }
        Material hideMaterial = Material.MAGENTA_DYE;
        String hideText = plugin.getStringConfig().getString("item.show");
        String hideKey = "show";
        if (!hidden) {
            hideMaterial = Material.GRAY_DYE;
            hideText = plugin.getStringConfig().getString("item.hide");
            hideKey = "hide";
        }
        ItemStack hide = new ItemStack(hideMaterial);
        ItemMeta hideData = hide.getItemMeta();
        hideData.setDisplayName(hideText);
        PersistentDataContainer hideCustomData = hideData.getPersistentDataContainer();
        hideCustomData.set(new NamespacedKey(plugin, hideKey), PersistentDataType.BOOLEAN, true);
        hide.setItemMeta(hideData);

        player.getInventory().setItem(0, checkpoint);
        player.getInventory().setItem(4, hide);
        player.getInventory().setItem(8, leave);
    }
}
