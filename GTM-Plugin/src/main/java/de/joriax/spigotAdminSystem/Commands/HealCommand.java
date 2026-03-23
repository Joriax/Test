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

public class HealCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("spigot.heal.others")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length > 0) {
            if (!sender.hasPermission("spigot.heal.others")) {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You don't have permission to use this command.");
                return true;
            }
            Player target = Bukkit.getPlayer((String)args[0]);
            if (target == null) {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + "Player not found.");
                return true;
            }
            target.setHealth(target.getMaxHealth());
            target.setSaturation(20.0f);
            target.setFoodLevel(20);
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "You have healed " + target.getName() + ".");
            target.sendMessage(SpigotAdminSystem.getPrefix() + "You have been healed by " + sender.getName() + ".");
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + "Console can only heal others. Usage: /heal <player>");
                return true;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("spigot.heal.self")) {
                player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You don't have permission to use this command.");
                return true;
            }
            player.setHealth(player.getMaxHealth());
            player.setSaturation(20.0f);
            player.setFoodLevel(20);
            player.sendMessage(SpigotAdminSystem.getPrefix() + "You have been healed.");
        }
        return true;
    }
}

