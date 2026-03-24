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

public class TPAToggleCommand
implements CommandExecutor {
    private final TPAManager tpaManager;

    public TPAToggleCommand(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!sender.hasPermission("spigot.tpatoggle.use")) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        boolean newState = !this.tpaManager.isTpaEnabled(player);
        this.tpaManager.setTpaEnabled(player, newState);
        player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "TPA " + (newState ? "enabled" : "disabled") + ".");
        return true;
    }
}

