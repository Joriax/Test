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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeenCommand
implements CommandExecutor {
    private final Map<String, Long> lastSeen;

    public SeenCommand(Map<String, Long> lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.seen.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "Bitte benutze /seen <spieler>");
            return true;
        }
        String playerName = args[0];
        Long lastSeenTime = this.lastSeen.get(playerName);
        if (lastSeenTime == null) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + "Der Spieler " + playerName + " ist nie online gewesen oder existiert nicht.");
            return true;
        }
        Player targetPlayer = sender.getServer().getPlayer(playerName);
        String status = targetPlayer != null && targetPlayer.isOnline() ? "\u00a7aonline" : "\u00a7coffline";
        String formattedTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(lastSeenTime));
        sender.sendMessage(SpigotAdminSystem.getPrefix() + "\u00a7a" + playerName + " ist derzeit " + status + ". Letzter Beitritt: " + formattedTime);
        return true;
    }
}

