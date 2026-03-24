package de.joriax.gtm.economy.commands;

import de.joriax.gtm.GTMPlugin;
import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SeeBalanceCommand implements CommandExecutor {

    private final GTMPlugin plugin;

    public SeeBalanceCommand(GTMPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gtm.economy.seebalance")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /seebalance <player>");
            return true;
        }

        EconomyAPI api = plugin.getEconomyAPI();

        Player onlineTarget = Bukkit.getPlayer(args[0]);
        if (onlineTarget != null) {
            double balance = api.getBalance(onlineTarget);
            int crowbars = api.getCrowbars(onlineTarget);
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "── " + onlineTarget.getName() + "'s Balance ──");
            sender.sendMessage(ChatColor.YELLOW + "Money: " + ChatColor.GREEN + api.formatBalance(balance));
            sender.sendMessage(ChatColor.YELLOW + "Crowbars: " + ChatColor.AQUA + crowbars);
            return true;
        }

        UUID uuid = api.getUUIDByName(args[0]);
        if (uuid != null) {
            double balance = api.getBalance(uuid);
            int crowbars = api.getCrowbars(uuid);
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "── " + args[0] + "'s Balance ──");
            sender.sendMessage(ChatColor.YELLOW + "Money: " + ChatColor.GREEN + api.formatBalance(balance));
            sender.sendMessage(ChatColor.YELLOW + "Crowbars: " + ChatColor.AQUA + crowbars);
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' was not found.");
        }

        return true;
    }
}
