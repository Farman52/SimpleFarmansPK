package me.farmans.simplefarmanspk.event;

import com.jeff_media.customblockdata.CustomBlockData;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockBreak implements Listener {
    SimpleFarmansPK plugin;

    public BlockBreak(SimpleFarmansPK plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE || block.getType() == Material.STONE_PRESSURE_PLATE || block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            PersistentDataContainer blockData = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "parkourName");
            if (blockData.has(key, PersistentDataType.STRING)) {
                String name = blockData.get(key, PersistentDataType.STRING);
                String xyz = String.format("%s %s %s", block.getX(), block.getY(), block.getZ());
                if (block.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                    if (plugin.getConfig().contains(String.format("parkours.%s.start", name)) && plugin.getConfig().get(String.format("parkours.%s.start", name)).equals(xyz)) {
                        event.setCancelled(true);
                        return;
                    }
                } else if (block.getType() == Material.STONE_PRESSURE_PLATE) {
                    if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name)) && plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getValues(false).containsValue(xyz)) {
                        event.setCancelled(true);
                        return;
                    }
                    blockData.remove(new NamespacedKey(plugin, "checkpointId"));
                } else if (block.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                    if (plugin.getConfig().contains(String.format("parkours.%s.finish", name)) && plugin.getConfig().get(String.format("parkours.%s.finish", name)).equals(xyz)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                blockData.remove(key);
            }
        }
    }
}
