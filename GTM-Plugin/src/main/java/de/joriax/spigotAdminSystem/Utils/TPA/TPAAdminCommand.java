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
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAAdminCommand
implements CommandExecutor {
    private final TPAManager tpaManager;

    public TPAAdminCommand(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.tpa.admin")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Usage: /tpaadmin <toggle|clear> <player>");
            return true;
        }
        Player target = Bukkit.getPlayer((String)args[1]);
        if (target == null) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "The player is currently not online.");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "toggle": {
                boolean newState = !this.tpaManager.isTpaEnabled(target);
                this.tpaManager.setTpaEnabled(target, newState);
                sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "TPA for " + target.getName() + " " + (newState ? "enabled" : "disabled") + ".");
                target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Your TPA was " + (newState ? "enabled" : "disabled") + " by an admin.");
                break;
            }
            case "clear": {
                this.tpaManager.clearRequests(target);
                sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Cleared all TPA requests for " + target.getName() + ".");
                target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Your TPA requests were cleared by an admin.");
                break;
            }
            default: {
                sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Invalid subcommand. Use toggle or clear.");
            }
        }
        return true;
    }
}

