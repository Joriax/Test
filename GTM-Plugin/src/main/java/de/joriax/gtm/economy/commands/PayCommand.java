package de.joriax.gtm.economy.commands;

import de.joriax.gtm.GTMPlugin;
import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final GTMPlugin plugin;

    public PayCommand(GTMPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /pay <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "You cannot pay yourself.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
            return true;
        }

        if (amount <= 0) {
            player.sendMessage(ChatColor.RED + "Amount must be greater than 0.");
            return true;
        }

        EconomyAPI api = plugin.getEconomyAPI();

        if (!api.hasBalance(player, amount)) {
            player.sendMessage(ChatColor.RED + "You don't have enough money. Your balance: "
                    + ChatColor.YELLOW + api.formatBalance(api.getBalance(player)));
            return true;
        }

        api.transfer(player, target, amount);

        player.sendMessage(ChatColor.GREEN + "You paid " + ChatColor.YELLOW + api.formatBalance(amount)
                + ChatColor.GREEN + " to " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + ".");
        target.sendMessage(ChatColor.GREEN + "You received " + ChatColor.YELLOW + api.formatBalance(amount)
                + ChatColor.GREEN + " from " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + ".");

        return true;
    }
}
