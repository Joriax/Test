/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (!player.hasPermission("spigot.god.self") && args.length == 0) {
                player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                return true;
            }
            if (args.length > 0) {
                target = Bukkit.getPlayer((String)args[0]);
                if (target == null) {
                    player.sendMessage(SpigotAdminSystem.getPrefix() + "The player " + args[0] + " is currently not online.");
                    return true;
                }
                if (!player.hasPermission("spigot.god.others")) {
                    player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                    return true;
                }
            } else {
                target = player;
            }
        } else {
            if (args.length == 0) {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + "Please use /god <player>.");
                return true;
            }
            target = Bukkit.getPlayer((String)args[0]);
            if (target == null) {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + "The player " + args[0] + " is not online.");
                return true;
            }
        }
        if (target.getGameMode() == GameMode.SURVIVAL) {
            target.setInvulnerable(!target.isInvulnerable());
            String status = target.isInvulnerable() ? "\u00a7aactivated" : "\u00a7cdeactivated";
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "God mode for " + target.getName() + " has been " + status + ".");
            target.sendMessage(SpigotAdminSystem.getPrefix() + "You are now " + status + " in god mode.");
        } else {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "The player must be in Survival Mode to activate God Mode.");
        }
        return true;
    }
}

