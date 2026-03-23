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

public class PrivateClearChatCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.pcc.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            for (int i = 0; i < 100; ++i) {
                player.sendMessage(" ");
            }
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Your chat has been cleared.");
        } else {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
        }
        return true;
    }
}

