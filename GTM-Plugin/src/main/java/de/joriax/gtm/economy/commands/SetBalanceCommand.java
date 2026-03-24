package de.joriax.gtm.economy.commands;

import de.joriax.gtm.GTMPlugin;
import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetBalanceCommand implements CommandExecutor {

    private final GTMPlugin plugin;

    public SetBalanceCommand(GTMPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gtm.economy.setbalance")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /setbalance <player> <amount>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative.");
            return true;
        }

        EconomyAPI api = plugin.getEconomyAPI();

        Player onlineTarget = Bukkit.getPlayer(args[0]);
        if (onlineTarget != null) {
            api.setBalance(onlineTarget, amount);
            sender.sendMessage(ChatColor.GREEN + "Set " + onlineTarget.getName() + "'s balance to "
                    + ChatColor.YELLOW + api.formatBalance(amount) + ChatColor.GREEN + ".");
            onlineTarget.sendMessage(ChatColor.GOLD + "Your balance was set to "
                    + ChatColor.YELLOW + api.formatBalance(amount) + ChatColor.GOLD + " by an admin.");
            return true;
        }

        UUID uuid = api.getUUIDByName(args[0]);
        if (uuid != null) {
            api.setBalance(uuid, amount);
            sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + "'s balance to "
                    + ChatColor.YELLOW + api.formatBalance(amount) + ChatColor.GREEN + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' was not found.");
        }

        return true;
    }
}
