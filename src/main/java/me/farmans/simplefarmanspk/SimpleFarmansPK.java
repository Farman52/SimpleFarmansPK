package me.farmans.simplefarmanspk;

import me.farmans.simplefarmanspk.command.CheckpointCommand;
import me.farmans.simplefarmanspk.command.FinishCommand;
import me.farmans.simplefarmanspk.command.ReloadCommand;
import me.farmans.simplefarmanspk.command.SetupCommand;
import me.farmans.simplefarmanspk.event.BlockBreak;
import me.farmans.simplefarmanspk.event.PlayerLeave;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SimpleFarmansPK extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("pksetup").setExecutor(new SetupCommand(this));
        getCommand("pkreload").setExecutor(new ReloadCommand(this));
        getCommand("pkcheckpoint").setExecutor(new CheckpointCommand(this));
        getCommand("pkfinish").setExecutor(new FinishCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerInteraction(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        Func.updateActionBar(this);
    }


}
