/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Utils.Perks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PerksManager {
    private final JavaPlugin plugin;
    private final File perksFile;
    private final YamlConfiguration perksConfig;
    private final List<String> allPerks = Arrays.asList("Speed Perk", "Jump Boost", "Strength I", "Strength II", "Invisibility I", "Invisibility II");

    public PerksManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.perksFile = new File(plugin.getDataFolder(), "perks.yml");
        this.perksConfig = YamlConfiguration.loadConfiguration((File)this.perksFile);
        if (!this.perksFile.exists()) {
            try {
                this.perksFile.createNewFile();
            }
            catch (IOException e) {
                plugin.getLogger().severe("Error creating perks.yml: " + e.getMessage());
            }
        }
    }

    public boolean hasBoughtPerk(UUID playerId, String perkName) {
        String cleanPerkName = this.sanitizePerkName(perkName);
        return this.perksConfig.getBoolean(playerId.toString() + "." + cleanPerkName, false);
    }

    public void setBoughtPerk(UUID playerId, String perkName) {
        String cleanPerkName = this.sanitizePerkName(perkName);
        if (cleanPerkName.isEmpty()) {
            this.plugin.getLogger().warning("Invalid perk name.");
            return;
        }
        this.perksConfig.set(playerId.toString() + "." + cleanPerkName, (Object)true);
        this.saveConfig();
    }

    public void removePerk(UUID playerId, String perkName) {
        String cleanPerkName = this.sanitizePerkName(perkName);
        this.perksConfig.set(playerId.toString() + "." + cleanPerkName, null);
        this.saveConfig();
    }

    public void resetAllPerks() {
        for (String playerId : this.perksConfig.getKeys(false)) {
            this.perksConfig.set(playerId, null);
        }
        this.saveConfig();
    }

    public List<String> getAllPerks() {
        return this.allPerks;
    }

    private String sanitizePerkName(String perkName) {
        return perkName.replaceAll("[\u00a7][0-9a-fk-or]", "").trim();
    }

    private void saveConfig() {
        try {
            this.perksConfig.save(this.perksFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Error saving perks.yml: " + e.getMessage());
        }
    }
}

