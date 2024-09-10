package me.farmans.simplefarmanspk.event.PressurePlates;

import com.jeff_media.customblockdata.CustomBlockData;
import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class ParkourCheckpoint {
    public ParkourCheckpoint(SimpleFarmansPK plugin, PlayerInteractEvent event) {
        long time = System.currentTimeMillis();
        Block block = event.getClickedBlock();

        NamespacedKey nameKey = new NamespacedKey(plugin, "parkourName");
        PersistentDataContainer blockData = new CustomBlockData(block, plugin);
        if (!blockData.has(nameKey, PersistentDataType.STRING)) return;

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        NamespacedKey idKey = new NamespacedKey(plugin, "checkpointId");
        String name = blockData.get(nameKey, PersistentDataType.STRING);
        int id = blockData.get(idKey, PersistentDataType.INTEGER);

        String xyz = String.format("%s %s %s", x, y, z);
        if (!plugin.getConfig().getConfigurationSection(String.format("parkours.%s.checkpoints", name)).getValues(false).containsValue(xyz)) {
            blockData.remove(nameKey);
            blockData.remove(idKey);
            return;
        }
        if (!plugin.getConfig().get(String.format("parkours.%s.checkpoints.%s", name, id)).equals(xyz)) return;
        String playerName = event.getPlayer().getName();
        if (PlayerInteraction.checkpoints.containsKey(playerName)) {
            if (PlayerInteraction.checkpoints.get(playerName).containsKey(xyz)) return;
        }
        if (!PlayerInteraction.times.containsKey(playerName)) return;
        if (!PlayerInteraction.times.get(playerName).get(0).equals(name)) return;
        if ((PlayerInteraction.checkpoints.containsKey(playerName) && PlayerInteraction.checkpoints.get(playerName).size() != id-1) || (!PlayerInteraction.checkpoints.containsKey(playerName) && id != 1)) {
            Func.sendMessage(event.getPlayer(), "Přeskočil jsi checkpoint");
            return;
        }
        double finalTime = (double)(time - (long) PlayerInteraction.times.get(playerName).get(1))/1000;
        HashMap<String, Double> checkpoint = new HashMap<>();
        if (PlayerInteraction.checkpoints.containsKey(playerName)) checkpoint = (HashMap<String, Double>) PlayerInteraction.checkpoints.get(playerName);
        checkpoint.put(xyz, finalTime);
        PlayerInteraction.checkpoints.put(playerName, checkpoint);
        String fancyFinalTime = String.valueOf(finalTime);
        String finalTimeDecimal = fancyFinalTime.split("\\.")[1];
        if (finalTimeDecimal.length() < 3) fancyFinalTime += "0".repeat(3-finalTimeDecimal.length());
        else if (finalTimeDecimal.length() > 3) fancyFinalTime = fancyFinalTime.split(".")[0]+"."+finalTimeDecimal.substring(0, 3);
        Func.sendMessage(event.getPlayer(), String.format("Dosáhl jsi %s. checkpointu v čase %ss", PlayerInteraction.checkpoints.get(playerName).size(), fancyFinalTime));

    }
}
