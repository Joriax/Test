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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WalkspeedCommand
implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "This command can only be executed by players!");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("utils.walkspeed")) {
            player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to use this command!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Your current walk speed: " + String.valueOf(ChatColor.GOLD) + player.getWalkSpeed() * 10.0f);
            player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Usage: /walkspeed <0.1-10>");
            return true;
        }
        if (args.length == 1) {
            try {
                float speed = Float.parseFloat(args[0]);
                if (speed < 0.1f || speed > 10.0f) {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Speed must be between 0.1 and 10!");
                    return true;
                }
                if (speed > 5.0f && !player.hasPermission("utils.walkspeed.extreme")) {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission for speeds above 5!");
                    return true;
                }
                float minecraftSpeed = speed / 10.0f;
                player.setWalkSpeed(minecraftSpeed);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Your walk speed has been set to " + String.valueOf(ChatColor.GOLD) + speed + String.valueOf(ChatColor.GREEN) + ".");
                return true;
            }
            catch (NumberFormatException e) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Please enter a valid number!");
                return true;
            }
        }
        if (args.length == 2) {
            if (!player.hasPermission("utils.walkspeed.others")) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to change other players' walk speed!");
                return true;
            }
            Player target = player.getServer().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "The specified player is not online!");
                return true;
            }
            try {
                float speed = Float.parseFloat(args[0]);
                if (speed < 0.1f || speed > 10.0f) {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Speed must be between 0.1 and 10!");
                    return true;
                }
                float minecraftSpeed = speed / 10.0f;
                target.setWalkSpeed(minecraftSpeed);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Walk speed of " + String.valueOf(ChatColor.GOLD) + target.getName() + String.valueOf(ChatColor.GREEN) + " has been set to " + String.valueOf(ChatColor.GOLD) + speed + String.valueOf(ChatColor.GREEN) + ".");
                target.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Your walk speed has been set to " + String.valueOf(ChatColor.GOLD) + speed + String.valueOf(ChatColor.GREEN) + " by " + String.valueOf(ChatColor.GOLD) + player.getName() + String.valueOf(ChatColor.GREEN) + ".");
                return true;
            }
            catch (NumberFormatException e) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Please enter a valid number!");
                return true;
            }
        }
        player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Usage: /walkspeed <0.1-10> [player]");
        return true;
    }
}

