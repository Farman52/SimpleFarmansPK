package me.farmans.simplefarmanspk.event;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    SimpleFarmansPK plugin;

    public PlayerLeave(SimpleFarmansPK plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();

        PlayerInteraction.times.remove(playerName);
        PlayerInteraction.checkpoints.remove(playerName);
        event.getPlayer().getInventory().clear();
        Func.showAll(plugin, event.getPlayer());
    }
}
