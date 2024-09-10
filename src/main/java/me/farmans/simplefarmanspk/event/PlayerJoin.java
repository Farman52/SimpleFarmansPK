package me.farmans.simplefarmanspk.event;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    SimpleFarmansPK plugin;

    public PlayerJoin(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().get("main").equals("null")) {
            String name = plugin.getConfig().getString("main");
            String[] xyzf = plugin.getConfig().getString(String.format("parkours.%s.spawn", name)).split(" ");
            event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), Float.parseFloat(xyzf[0]), Float.parseFloat(xyzf[1]), Float.parseFloat(xyzf[2]), Float.parseFloat(xyzf[3]), 0));
        }
        if (plugin.getConfig().contains("hidden")) {
            for (String player : plugin.getConfig().getConfigurationSection("hidden").getKeys(false)) {
                if (!Bukkit.getPlayer(player).isOnline()) continue;
                if ((boolean)plugin.getConfig().getConfigurationSection("hidden").getValues(false).get(player) == true) {
                    Bukkit.getPlayer(player).hidePlayer(event.getPlayer());
                } else {
                    Bukkit.getPlayer(player).showPlayer(event.getPlayer());
                }
            }
        }
    }
}
