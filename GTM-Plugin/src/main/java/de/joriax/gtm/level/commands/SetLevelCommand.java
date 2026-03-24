package de.joriax.gtm.level.commands;

import de.joriax.gtm.level.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLevelCommand implements CommandExecutor {

    private final LevelManager levelManager;

    public SetLevelCommand(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gtm.admin.setlevel")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /setlevel <player> <level>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
            if (level < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid level. Must be a positive number.");
            return true;
        }

        levelManager.setLevel(target, level);
        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s level to " + level + ".");
        target.sendMessage(ChatColor.GOLD + "Your level has been set to " + ChatColor.YELLOW + level + ChatColor.GOLD + " by an admin.");
        return true;
    }
}
