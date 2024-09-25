package me.farmans.simplefarmanspk.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("pktest") || args.length == 0) return false;
        final double DIFF = Double.parseDouble(args[0]);

        List<String> strings = new ArrayList<>();
        strings.add("ahooj");
        strings.add("nee");

        Player player = (Player) sender;
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        for (int i = strings.size()-1; i>=0; i--) {
            ArmorStand holo = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), x, y, z)
                    .add(0,DIFF*i,0), EntityType.ARMOR_STAND);
            holo.setGravity(false);
            holo.setVisible(false);
            holo.setCustomName(strings.get(i));
            holo.setCustomNameVisible(true);
            holo.setMarker(true);
        }

        return true;
    }
}
