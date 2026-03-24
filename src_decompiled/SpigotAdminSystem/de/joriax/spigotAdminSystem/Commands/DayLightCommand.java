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

import de.joriax.spigotAdminSystem.Manager.DayLightManager;
import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DayLightCommand
implements CommandExecutor {
    private final DayLightManager daylightManager;

    public DayLightCommand(DayLightManager daylightManager) {
        this.daylightManager = daylightManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.daylight.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        boolean newState = !this.daylightManager.isDaylightFrozen();
        this.daylightManager.setDaylightFrozen(newState);
        if (newState) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "\u00a7aDaylight cycle frozen at noon.");
        } else {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "\u00a7aDaylight cycle resumed.");
        }
        return true;
    }
}

