/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package de.joriax.spigotAdminSystem.Utils.ClearLag;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearLagManager {
    private final JavaPlugin plugin;
    private final String prefix;
    private long clearLagInterval;
    private final long WARNING_30_SECONDS = 600L;
    private final long WARNING_10_SECONDS = 200L;
    private BukkitRunnable clearLagTask;

    public ClearLagManager(JavaPlugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
        if (UtilsConfig.getClearLagConfig() != null) {
            this.clearLagInterval = UtilsConfig.getClearLagConfig().getLong("clearlag-interval", 36000L);
        } else {
            this.clearLagInterval = 36000L;
            plugin.getLogger().warning("ClearLag config not initialized, using default interval of 30 minutes.");
        }
    }

    public void startClearLagTask() {
        if (this.clearLagTask != null) {
            this.clearLagTask.cancel();
        }
        this.clearLagTask = new BukkitRunnable(){

            public void run() {
                new BukkitRunnable(){

                    public void run() {
                        Bukkit.broadcastMessage((String)(ClearLagManager.this.prefix + String.valueOf(ChatColor.YELLOW) + "All dropped items will be removed in 30 seconds!"));
                    }
                }.runTaskLater((Plugin)ClearLagManager.this.plugin, ClearLagManager.this.clearLagInterval - 600L);
                new BukkitRunnable(){

                    public void run() {
                        Bukkit.broadcastMessage((String)(ClearLagManager.this.prefix + String.valueOf(ChatColor.YELLOW) + "All dropped items will be removed in 10 seconds!"));
                    }
                }.runTaskLater((Plugin)ClearLagManager.this.plugin, ClearLagManager.this.clearLagInterval - 200L);
                new BukkitRunnable(){

                    public void run() {
                        ClearLagManager.this.clearEntities();
                    }
                }.runTaskLater((Plugin)ClearLagManager.this.plugin, ClearLagManager.this.clearLagInterval);
            }
        };
        this.clearLagTask.runTaskTimer((Plugin)this.plugin, 0L, this.clearLagInterval);
    }

    public void startManualClearLag() {
        new BukkitRunnable(){

            public void run() {
                Bukkit.broadcastMessage((String)(ClearLagManager.this.prefix + String.valueOf(ChatColor.YELLOW) + "All dropped items will be removed in 30 seconds!"));
                new BukkitRunnable(){

                    public void run() {
                        Bukkit.broadcastMessage((String)(ClearLagManager.this.prefix + String.valueOf(ChatColor.YELLOW) + "All dropped items will be removed in 10 seconds!"));
                        new BukkitRunnable(){

                            public void run() {
                                ClearLagManager.this.clearEntities();
                            }
                        }.runTaskLater((Plugin)ClearLagManager.this.plugin, 200L);
                    }
                }.runTaskLater((Plugin)ClearLagManager.this.plugin, 400L);
            }
        }.runTask((Plugin)this.plugin);
    }

    public void setClearLagInterval(long minutes) {
        this.clearLagInterval = minutes * 60L * 20L;
        if (UtilsConfig.getClearLagConfig() != null) {
            UtilsConfig.getClearLagConfig().set("clearlag-interval", (Object)this.clearLagInterval);
            UtilsConfig.saveClearLagConfig();
        } else {
            this.plugin.getLogger().warning("ClearLag config not initialized, interval not saved.");
        }
        this.startClearLagTask();
    }

    public long getClearLagInterval() {
        return this.clearLagInterval / 1200L;
    }

    private void clearEntities() {
        int removedItems = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Item)) continue;
                entity.remove();
                ++removedItems;
            }
        }
        String message = this.prefix + String.valueOf(ChatColor.YELLOW) + removedItems + " items have been removed!";
        Bukkit.broadcastMessage((String)message);
    }
}

