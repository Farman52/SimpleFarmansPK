package me.farmans.simplefarmanspk.event;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourCheckpoint;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourFinish;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourStart;
import me.farmans.simplefarmanspk.util.Func;
import me.farmans.simplefarmanspk.util.GiveParkourTools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInteraction implements Listener {
    SimpleFarmansPK plugin;

    public PlayerInteraction(SimpleFarmansPK plugin) {
        this.plugin = plugin;
    }

    public static Map<String, List> times = new HashMap<>(); // NAME: [PKNAME, TIME]
    public static Map<String, Map<String, Double>> checkpoints = new HashMap<>(); // NAME: XYZ: TIME

    public Map<String, Long> cooldowns = new HashMap<>();
    public final int COOLDOWN = 1;

    @EventHandler
    public void onPlayerInteraction(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (event.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {//START
                new ParkourStart(plugin, event);
            } else if (event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {//CHECKPOINT
                new ParkourCheckpoint(plugin, event);
            } else if (event.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {//FINISH
                new ParkourFinish(plugin, event);
            }
        } else {
            String playerName = event.getPlayer().getName();
            if (!event.getPlayer().getInventory().getItem(event.getHand()).hasItemMeta()) return;
            PersistentDataContainer itemData = event.getPlayer().getInventory().getItem(event.getHand()).getItemMeta().getPersistentDataContainer();
            if (itemData.has(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING)) {
                event.setCancelled(true);
                if (cooldowns.containsKey(playerName) && (System.currentTimeMillis() - cooldowns.get(playerName))/1000 < COOLDOWN) return;
                String xyz = itemData.get(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING);
                System.out.println(xyz);
            } else if (itemData.has(new NamespacedKey(plugin, "hide"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                if (cooldowns.containsKey(playerName) && (System.currentTimeMillis() - cooldowns.get(playerName))/1000 < COOLDOWN) return;
                Func.hideAll(plugin, event.getPlayer());
                new GiveParkourTools(plugin, event.getPlayer(), (String) times.get(playerName).get(0));
            } else if (itemData.has(new NamespacedKey(plugin, "show"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                if (cooldowns.containsKey(playerName) && (System.currentTimeMillis() - cooldowns.get(playerName))/1000 < COOLDOWN) return;
                Func.showAll(plugin, event.getPlayer());
                new GiveParkourTools(plugin, event.getPlayer(), (String) times.get(playerName).get(0));
            } else if (itemData.has(new NamespacedKey(plugin, "leave"), PersistentDataType.STRING)) {
                event.setCancelled(true);
                if (cooldowns.containsKey(playerName) && (System.currentTimeMillis() - cooldowns.get(playerName))/1000 < COOLDOWN) return;
                event.getPlayer().getInventory().clear();
                if (PlayerInteraction.times.containsKey(playerName)) {
                    PlayerInteraction.times.remove(playerName);
                }
                if (PlayerInteraction.checkpoints.containsKey(playerName)) {
                    PlayerInteraction.checkpoints.remove(playerName);
                }
                Func.showAll(plugin, event.getPlayer());
                String name = itemData.get(new NamespacedKey(plugin, "leave"), PersistentDataType.STRING);
                String[] xyzf = plugin.getConfig().getString(String.format("parkours.%s.spawn", name)).split(" ");
                event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), Float.parseFloat(xyzf[0]), Float.parseFloat(xyzf[1]), Float.parseFloat(xyzf[2]), Float.parseFloat(xyzf[3]), 0));

            } else return;
            cooldowns.put(playerName, System.currentTimeMillis());
        }
    }
}
