/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package de.joriax.spigotAdminSystem.Utils.Weather;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import org.bukkit.World;

public class WeatherHandler {
    public static void applyWeather(World world, String type) {
        switch (type.toLowerCase()) {
            case "clear": {
                world.setStorm(false);
                world.setThundering(false);
                break;
            }
            case "rain": {
                world.setStorm(true);
                world.setThundering(false);
                break;
            }
            case "snow": {
                world.setStorm(true);
                world.setThundering(false);
                break;
            }
            case "thunder": {
                world.setStorm(true);
                world.setThundering(true);
            }
        }
        UtilsConfig.getWeatherConfig().set("weather." + world.getName(), (Object)type);
        UtilsConfig.saveWeatherConfig();
    }
}

