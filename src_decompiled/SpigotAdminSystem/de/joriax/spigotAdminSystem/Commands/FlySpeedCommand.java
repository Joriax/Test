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

public class FlySpeedCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("spigot.flyspeed.use")) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Please enter a speed between 0.1 and 10.0.");
            return true;
        }
        try {
            float speed = Float.parseFloat(args[0]);
            if ((double)speed < 0.1 || (double)speed > 10.0) {
                player.sendMessage(SpigotAdminSystem.getPrefix() + "The speed must be between 0.1 and 10.0.");
                return true;
            }
            player.setFlySpeed(speed / 10.0f);
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Your flight speed has been set to " + speed + ".");
        }
        catch (NumberFormatException e) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + "Please enter a valid number.");
        }
        return true;
    }
}

