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

public class GameModeCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String permissionNode;
        GameMode mode;
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            if (!this.hasAnyGameModePermission(player)) {
                player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You don't have permission to use this command.");
                return true;
            }
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Usage: /gm <0|1|2|3> [player]");
            return true;
        }
        Player target = player;
        if (args.length > 1 && (target = Bukkit.getPlayer((String)args[1])) == null) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Player not found.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "0": {
                mode = GameMode.SURVIVAL;
                permissionNode = "spigot.gm0";
                break;
            }
            case "1": {
                mode = GameMode.CREATIVE;
                permissionNode = "spigot.gm1";
                break;
            }
            case "2": {
                mode = GameMode.ADVENTURE;
                permissionNode = "spigot.gm2";
                break;
            }
            case "3": {
                mode = GameMode.SPECTATOR;
                permissionNode = "spigot.gm3";
                break;
            }
            default: {
                player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Invalid game mode. Use: 0, 1, 2, 3.");
                return true;
            }
        }
        if (!player.hasPermission(permissionNode)) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You don't have permission for this game mode.");
            return true;
        }
        target.setGameMode(mode);
        if (target.equals((Object)player)) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Your game mode has been changed to " + mode.toString().toLowerCase() + ".");
        } else {
            player.sendMessage(SpigotAdminSystem.getPrefix() + "You changed " + target.getName() + "'s game mode to " + mode.toString().toLowerCase() + ".");
            target.sendMessage(SpigotAdminSystem.getPrefix() + "Your game mode has been changed to " + mode.toString().toLowerCase() + " by " + player.getName() + ".");
        }
        return true;
    }

    private boolean hasAnyGameModePermission(Player player) {
        return player.hasPermission("spigot.gm0") || player.hasPermission("spigot.gm1") || player.hasPermission("spigot.gm2") || player.hasPermission("spigot.gm3");
    }
}

