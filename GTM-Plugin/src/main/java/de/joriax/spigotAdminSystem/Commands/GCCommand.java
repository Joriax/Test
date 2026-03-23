/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GCCommand
implements CommandExecutor {
    private final SpigotAdminSystem plugin;

    public GCCommand(SpigotAdminSystem plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.gc.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        long uptime = System.currentTimeMillis() - this.plugin.getStartTime();
        long uptimeSeconds = uptime / 1000L;
        long seconds = uptimeSeconds % 60L;
        long minutes = uptimeSeconds / 60L % 60L;
        long hours = uptimeSeconds / 3600L % 24L;
        long days = uptimeSeconds / 86400L;
        sender.sendMessage(SpigotAdminSystem.getPrefix() + "Memory usage: " + usedMemory / 1024L / 1024L + " MB of " + totalMemory / 1024L / 1024L + " MB");
        sender.sendMessage(SpigotAdminSystem.getPrefix() + "Maximum memory: " + maxMemory / 1024L / 1024L + " MB");
        sender.sendMessage(SpigotAdminSystem.getPrefix() + "Uptime: " + days + "d " + hours + "h " + minutes + "m " + seconds + "s");
        return true;
    }
}

