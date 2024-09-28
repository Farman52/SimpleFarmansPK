package me.farmans.simplefarmanspk;

import me.farmans.simplefarmanspk.command.*;
import me.farmans.simplefarmanspk.event.BlockBreak;
import me.farmans.simplefarmanspk.event.PlayerInteraction;
import me.farmans.simplefarmanspk.event.PlayerJoin;
import me.farmans.simplefarmanspk.event.PlayerLeave;
import me.farmans.simplefarmanspk.util.Func;
import me.farmans.simplefarmanspk.util.SetupSettingsConfig;
import me.farmans.simplefarmanspk.util.SetupStringConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class SimpleFarmansPK extends JavaPlugin {
    private YamlConfiguration stringConfig;
    private File stringFile;
    private YamlConfiguration settingsConfig;
    private File settingsFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        stringFile = new File(getDataFolder(), "strings.yml");
        createStringConfig();
        reloadStringConfig();
        settingsFile = new File(getDataFolder(), "settings.yml");
        createSettingsConfig();
        reloadSettingsConfig();

        this.getConfig().addDefault("main", "null");
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        getCommand("pksetup").setExecutor(new SetupCommand(this));
        getCommand("pkreload").setExecutor(new ReloadCommand(this));
        getCommand("pkcheckpoint").setExecutor(new CheckpointCommand(this));
        getCommand("pkfinish").setExecutor(new FinishCommand(this));
        getCommand("pkspawn").setExecutor(new SpawnCommand(this));
        getCommand("pkmain").setExecutor(new MainCommand(this));
        getCommand("pkready").setExecutor(new ReadyCommand(this));
        getCommand("pkdelete").setExecutor(new DeleteCommand(this));
        getCommand("pkhologram").setExecutor(new HologramCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerInteraction(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this), this);
        Func.updateActionBar(this);
    }

    private void createStringConfig() {
        if (!this.stringFile.exists()) {
            try {
                this.stringFile.createNewFile();
                this.reloadStringConfig();
                new SetupStringConfig(this.stringConfig);
                this.stringConfig.options().copyDefaults(true);
                this.stringConfig.save(stringFile);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Chyba při vytváření strings.yml");
                e.printStackTrace();
            }
        }
    }
    public FileConfiguration getStringConfig() {
        return this.stringConfig;
    }
    public void reloadStringConfig() {
        this.stringConfig = YamlConfiguration.loadConfiguration(this.stringFile);
    }
    public void saveStringConfig() {
        try {
            getStringConfig().save(this.stringFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSettingsConfig() {
        if (!this.settingsFile.exists()) {
            try {
                this.settingsFile.createNewFile();
                this.reloadSettingsConfig();
                new SetupSettingsConfig(this.settingsConfig);
                this.settingsConfig.options().copyDefaults(true);
                this.settingsConfig.save(settingsFile);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Chyba při vytváření settings.yml");
                e.printStackTrace();
            }
        }
    }
    public FileConfiguration getSettingsConfig() {
        return this.settingsConfig;
    }
    public void reloadSettingsConfig() {
        this.settingsConfig = YamlConfiguration.loadConfiguration(this.settingsFile);
    }
    public void saveSettingsConfig() {
        try {
            getSettingsConfig().save(this.settingsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
