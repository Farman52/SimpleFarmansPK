package me.farmans.simplefarmanspk.event;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourCheckpoint;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourFinish;
import me.farmans.simplefarmanspk.event.PressurePlates.ParkourStart;
import me.farmans.simplefarmanspk.util.Func;
import me.farmans.simplefarmanspk.util.GiveParkourTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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
            System.out.println(event.getAction());
            System.out.println(event.getHand());
            if (!event.getPlayer().getInventory().getItem(event.getHand()).hasItemMeta()) return;
            PersistentDataContainer itemData = event.getPlayer().getInventory().getItem(event.getHand()).getItemMeta().getPersistentDataContainer();
            if (itemData.has(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING)) {
                event.setCancelled(true);
                String xyz = itemData.get(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING);
                System.out.println(xyz);
            } else if (itemData.has(new NamespacedKey(plugin, "hide"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                Func.hideAll(plugin, event.getPlayer());
                new GiveParkourTools(plugin, event.getPlayer(), (String) times.get(event.getPlayer().getName()).get(0));
            } else if (itemData.has(new NamespacedKey(plugin, "show"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                Func.showAll(plugin, event.getPlayer());
                new GiveParkourTools(plugin, event.getPlayer(), (String) times.get(event.getPlayer().getName()).get(0));
            } else if (itemData.has(new NamespacedKey(plugin, "leave"), PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                String playerName = event.getPlayer().getName();
                event.getPlayer().getInventory().clear();
                if (PlayerInteraction.times.containsKey(playerName)) {
                    PlayerInteraction.times.remove(playerName);
                }
                if (PlayerInteraction.checkpoints.containsKey(playerName)) {
                    PlayerInteraction.checkpoints.remove(playerName);
                }
                Func.showAll(plugin, event.getPlayer());
            }
        }
    }
}
