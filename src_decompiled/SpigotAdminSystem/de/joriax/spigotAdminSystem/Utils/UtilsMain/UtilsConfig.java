/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameRule
 *  org.bukkit.World
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Utils.UtilsMain;

import de.joriax.spigotAdminSystem.Utils.Weather.WeatherHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class UtilsConfig {
    private static File weatherFile;
    private static File timeFile;
    private static File clearLagFile;
    private static File maintenanceFile;
    private static YamlConfiguration weatherConfig;
    private static YamlConfiguration timeConfig;
    private static YamlConfiguration clearLagConfig;
    private static YamlConfiguration maintenanceConfig;
    private static JavaPlugin plugin;

    public static void setupConfig(JavaPlugin pluginInstance) {
        plugin = pluginInstance;
        File utilsFolder = new File(plugin.getDataFolder(), "utils");
        if (!utilsFolder.exists()) {
            utilsFolder.mkdirs();
        }
        if (!(weatherFile = new File(utilsFolder, "weather.yml")).exists()) {
            try {
                weatherFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        weatherConfig = YamlConfiguration.loadConfiguration((File)weatherFile);
        timeFile = new File(utilsFolder, "time.yml");
        if (!timeFile.exists()) {
            try {
                timeFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        timeConfig = YamlConfiguration.loadConfiguration((File)timeFile);
        clearLagFile = new File(utilsFolder, "clearlag.yml");
        if (!clearLagFile.exists()) {
            try {
                clearLagFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!(clearLagConfig = YamlConfiguration.loadConfiguration((File)clearLagFile)).contains("clearlag-interval")) {
            clearLagConfig.set("clearlag-interval", (Object)36000);
            UtilsConfig.saveClearLagConfig();
        }
        if (!(maintenanceFile = new File(utilsFolder, "maintenance.yml")).exists()) {
            try {
                maintenanceFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!(maintenanceConfig = YamlConfiguration.loadConfiguration((File)maintenanceFile)).contains("enabled")) {
            maintenanceConfig.set("enabled", (Object)false);
            maintenanceConfig.set("bypass-players", new ArrayList());
            UtilsConfig.saveMaintenanceConfig();
        }
        for (World world : plugin.getServer().getWorlds()) {
            String weather = weatherConfig.getString("weather." + world.getName());
            if (weather == null) continue;
            WeatherHandler.applyWeather(world, weather);
        }
        for (World world : plugin.getServer().getWorlds()) {
            Long time = timeConfig.getLong("time." + world.getName(), -1L);
            if (time == -1L) continue;
            world.setTime(time.longValue());
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, (Object)false);
        }
    }

    public static YamlConfiguration getWeatherConfig() {
        return weatherConfig;
    }

    public static YamlConfiguration getTimeConfig() {
        return timeConfig;
    }

    public static YamlConfiguration getClearLagConfig() {
        return clearLagConfig;
    }

    public static YamlConfiguration getMaintenanceConfig() {
        return maintenanceConfig;
    }

    public static void saveWeatherConfig() {
        try {
            weatherConfig.save(weatherFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveTimeConfig() {
        try {
            timeConfig.save(timeFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClearLagConfig() {
        try {
            clearLagConfig.save(clearLagFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveMaintenanceConfig() {
        try {
            maintenanceConfig.save(maintenanceFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

