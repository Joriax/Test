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
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand
implements CommandExecutor {
    private final TPAManager tpaManager;

    public TPACommand(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!this.tpaManager.isTpaEnabled(player)) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Your TPA is disabled.");
            return true;
        }
        if (!sender.hasPermission("spigot.tpa.use")) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Usage: /tpa <player>");
            return true;
        }
        Player targetPlayer = player.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "The player is currently not online.");
            return true;
        }
        if (!this.tpaManager.isTpaEnabled(targetPlayer)) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + targetPlayer.getName() + " has TPA disabled.");
            return true;
        }
        this.tpaManager.requestTeleport(player, targetPlayer);
        return true;
    }
}

