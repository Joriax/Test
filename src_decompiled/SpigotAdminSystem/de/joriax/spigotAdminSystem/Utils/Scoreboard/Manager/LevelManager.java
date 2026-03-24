/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.levelSystem.LevelSystem
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager;

import de.joriax.levelSystem.LevelSystem;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelManager {
    private final JavaPlugin plugin;
    private LevelSystem levelSystem;

    public LevelManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}

