/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeePingCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.seeping.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "Please use /seeping <player>");
            return true;
        }
        Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "The player " + args[0] + " is not online.");
            return true;
        }
        int ping = targetPlayer.getPing();
        sender.sendMessage(SpigotAdminSystem.getPrefix() + "The ping of " + targetPlayer.getName() + " is: \u00a7e" + ping + " ms");
        return true;
    }
}

