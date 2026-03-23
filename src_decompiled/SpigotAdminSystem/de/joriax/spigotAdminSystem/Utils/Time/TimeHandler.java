/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameRule
 *  org.bukkit.World
 */
package de.joriax.spigotAdminSystem.Utils.Time;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import org.bukkit.GameRule;
import org.bukkit.World;

public class TimeHandler {
    public static void applyTime(World world, String type) {
        long time = switch (type.toLowerCase()) {
            case "morning" -> 0L;
            case "day" -> 6000L;
            case "evening" -> 12000L;
            case "midnight" -> 18000L;
            default -> 0L;
        };
        world.setTime(time);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, (Object)false);
        UtilsConfig.getTimeConfig().set("time." + world.getName(), (Object)time);
        UtilsConfig.saveTimeConfig();
    }
}

