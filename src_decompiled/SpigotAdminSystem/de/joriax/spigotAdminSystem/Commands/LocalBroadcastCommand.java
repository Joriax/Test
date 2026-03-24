/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LocalBroadcastCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.lbc.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "Please enter a message.");
            return true;
        }
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        String finalMessage = message.toString().trim();
        String formattedMessage = this.formatMessage(finalMessage);
        Bukkit.broadcastMessage((String)" ");
        Bukkit.broadcastMessage((String)" ");
        Bukkit.broadcastMessage((String)(String.valueOf(ChatColor.GREEN) + "[Broadcast] " + formattedMessage));
        Bukkit.broadcastMessage((String)" ");
        Bukkit.broadcastMessage((String)" ");
        return true;
    }

    private String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)message);
    }
}

