package me.farmans.simplefarmanspk.event.PressurePlates;

import com.jeff_media.customblockdata.CustomBlockData;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.util.Func;
import me.farmans.simplefarmanspk.util.GiveParkourTools;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ParkourStart {
    public ParkourStart(SimpleFarmansPK plugin, PlayerInteractEvent event) {
        long time = System.currentTimeMillis();
        Block block = event.getClickedBlock();

        NamespacedKey key = new NamespacedKey(plugin, "parkourName");
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);
        if (!blockData.has(key, PersistentDataType.STRING)) return;

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String name = blockData.get(key, PersistentDataType.STRING);

        String xyz = String.format("%s %s %s", x, y, z);
        if (!plugin.getConfig().contains(String.format("parkours.%s.start", name)) || !plugin.getConfig().getString(String.format("parkours.%s.start", name)).equals(xyz)) {
            blockData.remove(key);
            return;
        }

        String playerName = event.getPlayer().getName();
        if (!PlayerInteraction.times.isEmpty() && PlayerInteraction.times.containsKey(playerName)) {
            PlayerInteraction.times.remove(playerName);
        }
        PlayerInteraction.times.put(playerName, List.of(name, time));
        if (PlayerInteraction.checkpoints.containsKey(playerName)) PlayerInteraction.checkpoints.remove(playerName);
        Func.sendMessage(event.getPlayer(), "Zacal jsi skakat, hodne stesti!");
        new GiveParkourTools(plugin, event.getPlayer(), name);
    }
}
