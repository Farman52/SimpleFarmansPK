package me.farmans.simplefarmanspk.util;

import org.bukkit.configuration.file.YamlConfiguration;

public class SetupStringConfig {
    public SetupStringConfig(YamlConfiguration yml) {
        yml.addDefault("chat_message", "§9§lSFPK > §r%s");
        yml.addDefault("action_bar", "§bTvůj čas: §r%ss");
        yml.addDefault("item.checkpoint", "§aCheckpoint");
        yml.addDefault("item.leave", "§cRestart");
        yml.addDefault("item.show", "§dZobrazit hráče");
        yml.addDefault("item.hide", "§7Skrýt hráče");
        yml.addDefault("hologram.nobody", "Zatím nikdo");
        yml.addDefault("hologram.variable", "%p: %t");
        yml.addDefault("commands.unknown_name", "Jméno parkouru neexistuje");
        yml.addDefault("commands.create_spawn", "Parkour %s spawn byl vytvořen");
        yml.addDefault("commands.create_start", "Parkour %s start byl vytvořen");
        yml.addDefault("commands.create_finish", "Parkour %s finish byl vytvořen");
        yml.addDefault("commands.create_checkpoint", "Parkour %s checkpoint %s byl vytvořen");
        yml.addDefault("commands.bad_name", "Zvol jiný jméno parkouru");
        yml.addDefault("commands.bad_place", "Nemůžu položit bro, musí být vzduch");
        yml.addDefault("commands.reload_config", "Config byl reloadnut");
        yml.addDefault("commands.ready_false", "Parkour %s již není připraven");
        yml.addDefault("commands.ready_true", "Parkour %s je připraven");
        yml.addDefault("commands.ready_todo", "Chybí ti nastavit ještě pár věcí... (%s)");
        yml.addDefault("commands.not_ready", "Parkour není připraven (/pkready)");
        yml.addDefault("commands.main_cancel", "Zrušil jsi hlavní parkour");
        yml.addDefault("commands.main_set", "Nastavil jsi %s jako hlavní parkour");
        yml.addDefault("commands.delete", "Smazal jsi parkour %s");
        yml.addDefault("commands.checkpoint_bad_count", "Počet checkpointů nevychází");
        yml.addDefault("commands.checkpoint_delete", "Checkpoint %s byl ostraněn");
        yml.addDefault("commands.bad_hologram", "Není nastavený leaderboard");
        yml.addDefault("commands.reload_hologram", "Hologram %s byl reloadnut");
        yml.addDefault("commands.delete_hologram", "Smazal jsi hologram %s");
        yml.addDefault("commands.delete_line_hologram", "Řádek %s ostraněn");
        yml.addDefault("commands.hologram_bad_count", "Počet řádků neodpovídá");
        yml.addDefault("commands.hologram_set_line", "Text \'%s§r\' nastaven na řádek %s");
        yml.addDefault("commands.hologram_max_lines", "Maximální počet řádků byl dosažen");
        yml.addDefault("parkour.start", "Začal jsi skákat, hodně štěstí!");
        yml.addDefault("parkour.missing_checkpoints", "Nemáš všechny checkpointy");
        yml.addDefault("parkour.finish", "Dokončil jsi parkour v čase %s sekund.");
        yml.addDefault("parkour.skipped_checkpoint", "Přeskočil jsi checkpoint");
        yml.addDefault("parkour.checkpoint_reached", "Dosáhl jsi %s. checkpointu v čase %ss");
    }
}
