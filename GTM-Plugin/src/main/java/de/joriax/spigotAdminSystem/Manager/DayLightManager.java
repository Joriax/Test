/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Manager;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DayLightManager {
    private final JavaPlugin plugin;
    private boolean daylightFrozen;
    private static final long FIXED_TIME = 6000L;

    public DayLightManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadState();
        this.startDaylightTask();
    }

    private void startDaylightTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, () -> {
            if (this.daylightFrozen) {
                for (World world : Bukkit.getWorlds()) {
                    world.setTime(6000L);
                }
            }
        }, 0L, 100L);
    }

    public void setDaylightFrozen(boolean frozen) {
        this.daylightFrozen = frozen;
        this.saveState();
    }

    public boolean isDaylightFrozen() {
        return this.daylightFrozen;
    }

    private void saveState() {
        File configFile = new File(this.plugin.getDataFolder(), "daylight.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)configFile);
        config.set("daylightFrozen", (Object)this.daylightFrozen);
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().warning("Could not save daylight state: " + e.getMessage());
        }
    }

    private void loadState() {
        File configFile = new File(this.plugin.getDataFolder(), "daylight.yml");
        if (!configFile.exists()) {
            this.daylightFrozen = false;
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)configFile);
        this.daylightFrozen = config.getBoolean("daylightFrozen", false);
    }
}

