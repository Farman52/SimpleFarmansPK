package me.farmans.simplefarmanspk.util;

import me.farmans.simplefarmanspk.SimpleFarmansPK;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Func {
    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "SFPK: " + ChatColor.RESET + msg);
    }

    public static void updateActionBar(SimpleFarmansPK plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Map<String, List> times = PlayerInteraction.times;
                for (int i = 0; i<times.size(); i++) {
                    String playerName = (String)times.keySet().toArray()[i];
                    Player player = Bukkit.getPlayer(playerName);
                    if (!player.isOnline()) continue;
                    String finalTime = String.valueOf((double)(time-(long)times.get(playerName).get(1))/1000);
                    String finalTimeDecimal = finalTime.split("\\.")[1];
                    if (finalTimeDecimal.length() < 3) finalTime += "0".repeat(3-finalTimeDecimal.length());
                    else if (finalTimeDecimal.length() > 3) finalTime = finalTime.split(".")[0]+"."+finalTimeDecimal.substring(0, 3);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Tvuj cas: " + finalTime + "s"));
                }
            }
        }, 0, 5);
    }

    public static void hideAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getName()), true);
        plugin.saveConfig();
    }
    public static void showAll(SimpleFarmansPK plugin, Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            player.showPlayer(online);
        }
        plugin.getConfig().set(String.format("hidden.%s", player.getName()), false);
        plugin.saveConfig();

    }
}
