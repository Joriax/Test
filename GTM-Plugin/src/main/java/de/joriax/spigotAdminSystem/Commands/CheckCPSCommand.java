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

import de.joriax.spigotAdminSystem.Manager.CPSManager;
import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCPSCommand
implements CommandExecutor {
    private final CPSManager cpsManager;
    private final HashMap<Player, Player> monitoringPlayers;

    public CheckCPSCommand(CPSManager cpsManager) {
        this.cpsManager = cpsManager;
        this.monitoringPlayers = new HashMap();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        if (!sender.hasPermission("spigot.check.cps.use")) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        Player monitor = (Player)sender;
        if (args.length != 1) {
            monitor.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Please enter the name of the player whose CPS you want to monitor.");
            return true;
        }
        Player target = Bukkit.getPlayer((String)args[0]);
        if (target == null || !target.isOnline()) {
            monitor.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "The player is currently not online.");
            return true;
        }
        if (this.monitoringPlayers.containsKey(monitor) && this.monitoringPlayers.get(monitor).equals((Object)target)) {
            this.monitoringPlayers.remove(monitor);
            this.cpsManager.stopMonitoring(monitor, target);
            monitor.sendMessage(SpigotAdminSystem.getPrefix() + "You monitor the CPS of " + target.getName() + " not anymore.");
        } else {
            this.monitoringPlayers.put(monitor, target);
            this.cpsManager.monitorPlayer(monitor, target);
            monitor.sendMessage(SpigotAdminSystem.getPrefix() + "You are now monitoring the CPS of " + target.getName() + ".");
            this.cpsManager.startCombat(target);
        }
        return true;
    }
}

