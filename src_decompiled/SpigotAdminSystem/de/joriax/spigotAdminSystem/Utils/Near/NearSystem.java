/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Utils.Near;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NearSystem
implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("near.use")) {
            sender.sendMessage(this.prefix + String.valueOf(net.md_5.bungee.api.ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "ERROR! Only Players can execute this command.");
            return true;
        }
        Player player = (Player)sender;
        int maxDistance = 50;
        if (player.hasPermission("near.default")) {
            maxDistance = 51;
        } else if (player.hasPermission("near.vip")) {
            maxDistance = 71;
        } else if (player.hasPermission("near.premium")) {
            maxDistance = 91;
        } else if (player.hasPermission("near.elite")) {
            maxDistance = 111;
        } else if (player.hasPermission("near.sponsor")) {
            maxDistance = 151;
        } else if (player.hasPermission("near.supreme")) {
            maxDistance = 201;
        } else if (player.hasPermission("near.team")) {
            maxDistance = 251;
        } else if (player.hasPermission("near.admin")) {
            maxDistance = 351;
        } else {
            sender.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You do not have the Perk to use /near!");
            return true;
        }
        Location playerLocation = player.getLocation();
        player.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + "NearSystem: ");
        boolean foundPlayer = false;
        for (Player target : Bukkit.getOnlinePlayers()) {
            Location targetLocation;
            double distance;
            if (target.equals((Object)player) || !((distance = playerLocation.distance(targetLocation = target.getLocation())) <= (double)maxDistance)) continue;
            player.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + target.getName() + " (" + String.valueOf(ChatColor.DARK_GRAY) + (int)distance + String.valueOf(ChatColor.GRAY) + " Blocks)");
            foundPlayer = true;
        }
        if (!foundPlayer) {
            player.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + "No players found nearby.");
        }
        return true;
    }
}

