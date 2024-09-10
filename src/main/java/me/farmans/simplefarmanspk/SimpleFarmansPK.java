package me.farmans.simplefarmanspk;

import me.farmans.simplefarmanspk.command.*;
import me.farmans.simplefarmanspk.event.BlockBreak;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.event.PlayerJoin;
import me.farmans.simplefarmanspk.event.PlayerLeave;
import me.farmans.simplefarmanspk.util.Func;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleFarmansPK extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.getConfig().addDefault("main", "null");

        getCommand("pksetup").setExecutor(new SetupCommand(this));
        getCommand("pkreload").setExecutor(new ReloadCommand(this));
        getCommand("pkcheckpoint").setExecutor(new CheckpointCommand(this));
        getCommand("pkfinish").setExecutor(new FinishCommand(this));
        getCommand("pkspawn").setExecutor(new SpawnCommand(this));
        getCommand("pkmain").setExecutor(new MainCommand(this));
        getCommand("pkready").setExecutor(new ReadyCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerInteraction(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        Func.updateActionBar(this);
    }


}
