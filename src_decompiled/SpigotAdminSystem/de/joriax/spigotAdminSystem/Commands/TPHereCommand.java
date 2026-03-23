/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPHereCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player admin = (Player)sender;
        if (!admin.hasPermission("spigot.tphere.use")) {
            admin.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            admin.sendMessage(SpigotAdminSystem.getPrefix() + "Please use /tphere <player>");
            return true;
        }
        Player targetPlayer = admin.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            admin.sendMessage(SpigotAdminSystem.getPrefix() + "The player is not online.");
            return true;
        }
        targetPlayer.teleport(admin.getLocation());
        admin.sendMessage(SpigotAdminSystem.getPrefix() + "You have teleported " + targetPlayer.getName() + " to you.");
        return true;
    }
}

