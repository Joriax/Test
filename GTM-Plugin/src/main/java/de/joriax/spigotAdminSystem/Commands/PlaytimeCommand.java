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

import de.joriax.spigotAdminSystem.Manager.MySQLManager;
import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCommand
implements CommandExecutor {
    private final MySQLManager mysqlManager;
    private final HashMap<UUID, Long> sessionStartTime;

    public PlaytimeCommand(MySQLManager mysqlManager, HashMap<UUID, Long> sessionStartTime) {
        this.mysqlManager = mysqlManager;
        this.sessionStartTime = sessionStartTime;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("spigot.playtime.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player)sender;
            UUID playerId = player.getUniqueId();
            long totalPlaytime = this.mysqlManager.getTotalPlaytime(playerId.toString());
            long joinTime = this.sessionStartTime.getOrDefault(playerId, System.currentTimeMillis());
            long currentSessionDuration = System.currentTimeMillis() - joinTime;
            String totalPlaytimeMessage = this.formatPlaytime(totalPlaytime);
            String sessionDurationMessage = this.formatPlaytime(currentSessionDuration);
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Your total playing time on the server is: \u00a7e" + totalPlaytimeMessage);
            player.sendMessage(SpigotAdminSystem.getPrefix() + " ");
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Your current game session is: \u00a7e" + sessionDurationMessage);
        } else {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
        }
        return true;
    }

    private String formatPlaytime(long playtimeMillis) {
        long totalSeconds = playtimeMillis / 1000L;
        long days = totalSeconds / 86400L;
        long hours = (totalSeconds %= 86400L) / 3600L;
        long minutes = (totalSeconds %= 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        return days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }
}

