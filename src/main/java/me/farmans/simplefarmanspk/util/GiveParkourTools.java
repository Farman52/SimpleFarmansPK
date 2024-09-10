package me.farmans.simplefarmanspk.util;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GiveParkourTools {
    public GiveParkourTools(SimpleFarmansPK plugin, Player player, String name) {
        player.getInventory().clear();

        String playerName = player.getName();
        ItemStack checkpoint = new ItemStack(Material.SLIME_BALL);
        ItemMeta checkpointData = checkpoint.getItemMeta();
        checkpointData.setDisplayName(ChatColor.GREEN + "Checkpoint");
        PersistentDataContainer checkpointCustomData = checkpointData.getPersistentDataContainer();
        String xyz = plugin.getConfig().getString(String.format("parkours.%s.start", name));
        if (PlayerInteraction.checkpoints.containsKey(playerName)) {
            int id = PlayerInteraction.checkpoints.get(playerName).size();
            xyz = plugin.getConfig().getString(String.format("parkours.%s.checkpoints.%s", name, id));
        }
        checkpointCustomData.set(new NamespacedKey(plugin, "checkpoint"), PersistentDataType.STRING, xyz);
        checkpoint.setItemMeta(checkpointData);

        ItemStack leave = new ItemStack(Material.RED_DYE);
        ItemMeta leaveData = leave.getItemMeta();
        leaveData.setDisplayName(ChatColor.RED + "Odejít");
        PersistentDataContainer leaveCustomData = leaveData.getPersistentDataContainer();
        leaveCustomData.set(new NamespacedKey(plugin, "leave"), PersistentDataType.STRING, name);
        leave.setItemMeta(leaveData);

        boolean hidden = false;
        if (plugin.getConfig().contains(String.format("hidden.%s", playerName))) {
            hidden = plugin.getConfig().getBoolean(String.format("hidden.%s", playerName));
        }
        Material hideMaterial = Material.MAGENTA_DYE;
        String hideText = ChatColor.LIGHT_PURPLE + "Zobrazit hráče";
        String hideKey = "show";
        if (hidden == false) {
            hideMaterial = Material.GRAY_DYE;
            hideText = ChatColor.GRAY + "Skrýt hráče";
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
