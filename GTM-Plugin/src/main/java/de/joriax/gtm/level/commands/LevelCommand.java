package de.joriax.gtm.level.commands;

import de.joriax.gtm.level.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    private final LevelManager levelManager;

    public LevelCommand(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /level <player>");
                return true;
            }
            Player player = (Player) sender;
            int level = levelManager.getLevel(player);
            int xp = levelManager.getXP(player);
            int xpNeeded = levelManager.getXPRequiredForNextLevel(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Your Level: " + ChatColor.YELLOW + level);
            player.sendMessage(ChatColor.GOLD + "Your XP: " + ChatColor.YELLOW + xp + ChatColor.GRAY + " / " + xpNeeded);
            return true;
        }

        if (args.length >= 1) {
            if (!sender.hasPermission("gtm.admin.level")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view other players' levels.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
                return true;
            }
            int level = levelManager.getLevel(target);
            int xp = levelManager.getXP(target);
            int xpNeeded = levelManager.getXPRequiredForNextLevel(target.getUniqueId());
            sender.sendMessage(ChatColor.GOLD + target.getName() + "'s Level: " + ChatColor.YELLOW + level);
            sender.sendMessage(ChatColor.GOLD + target.getName() + "'s XP: " + ChatColor.YELLOW + xp + ChatColor.GRAY + " / " + xpNeeded);
        }
        return true;
    }
}
