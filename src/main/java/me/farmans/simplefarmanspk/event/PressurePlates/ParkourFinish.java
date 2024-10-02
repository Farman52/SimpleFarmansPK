package me.farmans.simplefarmanspk.event.PressurePlates;

import com.jeff_media.customblockdata.CustomBlockData;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.util.Func;
import me.farmans.simplefarmanspk.util.MongoDB;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ParkourFinish {
    public ParkourFinish(SimpleFarmansPK plugin, PlayerInteractEvent event) {
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
        if (!plugin.getConfig().contains(String.format("parkours.%s.finish", name)) || !plugin.getConfig().getString(String.format("parkours.%s.finish", name)).equals(xyz)) {
            blockData.remove(key);
            return;
        }

        String playerName = event.getPlayer().getName();
        if (!PlayerInteraction.times.containsKey(playerName)) return;
        if (!PlayerInteraction.times.get(playerName).get(0).equals(name)) return;
        if (plugin.getConfig().contains(String.format("parkours.%s.checkpoints", name)) && (!PlayerInteraction.checkpoints.containsKey(playerName) || PlayerInteraction.checkpoints.get(playerName).size() != plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getKeys(false).size())) {
            Func.sendMessage(event.getPlayer(), plugin,plugin.getStringConfig().getString("parkour.missing_checkpoints"));
            return;
        }
        double finalTime = (double)(time - (long) PlayerInteraction.times.get(playerName).get(1))/1000;
        UUID uuid = event.getPlayer().getUniqueId();
        if (!plugin.getConfig().contains(String.format("parkours.%s.times.%s", name, uuid)) || plugin.getConfig().getDouble(String.format("parkours.%s.times.%s", name, uuid)) > finalTime) {
            plugin.getConfig().set(String.format("parkours.%s.times.%s", name, uuid), finalTime);
            plugin.saveConfig();
        }
        String fancyFinalTime = String.valueOf(finalTime);
        String finalTimeDecimal = fancyFinalTime.split("\\.")[1];
        if (finalTimeDecimal.length() < 3) fancyFinalTime += "0".repeat(3-finalTimeDecimal.length());
        else if (finalTimeDecimal.length() > 3) fancyFinalTime = fancyFinalTime.split("\\.")[0]+"."+finalTimeDecimal.substring(0, 3);
        Func.sendMessage(event.getPlayer(), plugin, String.format(plugin.getStringConfig().getString("parkour.finish"), fancyFinalTime));
        PlayerInteraction.times.remove(playerName);
        PlayerInteraction.checkpoints.remove(playerName);
        if (plugin.getConfig().contains(String.format("parkours.%s.lb_coords", name))) {
            Func.spawnLeaderboard(event.getPlayer(), plugin, name);
        }
        event.getPlayer().getInventory().clear();
        Func.showAll(plugin, event.getPlayer());
        String[] xyzSpawn = plugin.getConfig().getString(String.format("parkours.%s.spawn", name)).split(" ");
        event.getPlayer().teleport(new Location(block.getWorld(), Double.parseDouble(xyzSpawn[0]), Double.parseDouble(xyzSpawn[1]), Double.parseDouble(xyzSpawn[2]), Float.parseFloat(xyzSpawn[3]), 0));

        List<Document> documents = MongoDB.read(event.getPlayer(), name);
        if (documents.isEmpty() || (double)documents.get(0).get("time") > finalTime) MongoDB.insert(event.getPlayer(), name, finalTime);
    }
}
