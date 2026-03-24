/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetPosCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.getpos.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "Please use /getpos <player>");
            return true;
        }
        String playerName = args[0];
        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "The player " + playerName + " is currently not online.");
            return true;
        }
        Location location = targetPlayer.getLocation();
        String positionMessage = String.format(SpigotAdminSystem.getPrefix() + "\u00a7a%s is at coordinates: X: %.2f, Y: %.2f, Z: %.2f", targetPlayer.getName(), location.getX(), location.getY(), location.getZ());
        sender.sendMessage(positionMessage);
        return true;
    }
}

