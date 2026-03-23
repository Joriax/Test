package de.joriax.gtm.economy.commands;

import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarsCommand implements CommandExecutor {

    private final EconomyAPI economyAPI;

    public CrowbarsCommand(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show own crowbars
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command without arguments.");
                return true;
            }
            Player player = (Player) sender;
            int crowbars = economyAPI.getCrowbars(player);
            player.sendMessage(ChatColor.GOLD + "Your crowbars: " + ChatColor.YELLOW + crowbars);
            return true;
        }

        if (args.length >= 1) {
            // Admin: see/modify other player's crowbars
            if (!sender.hasPermission("gtm.admin.crowbars")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
                return true;
            }

            if (args.length == 1) {
                int crowbars = economyAPI.getCrowbars(target);
                sender.sendMessage(ChatColor.GOLD + target.getName() + "'s crowbars: " + ChatColor.YELLOW + crowbars);
                return true;
            }

            if (args.length == 3) {
                String action = args[1].toLowerCase();
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                    if (amount < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid amount. Usage: /crowbars <player> <set|add|remove> <amount>");
                    return true;
                }

                switch (action) {
                    case "set":
                        economyAPI.setCrowbars(target, amount);
                        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s crowbars to " + amount + ".");
                        target.sendMessage(ChatColor.GOLD + "Your crowbars have been set to " + amount + ".");
                        break;
                    case "add":
                        economyAPI.addCrowbars(target, amount);
                        sender.sendMessage(ChatColor.GREEN + "Added " + amount + " crowbars to " + target.getName() + ".");
                        target.sendMessage(ChatColor.GOLD + "You received " + amount + " crowbars.");
                        break;
                    case "remove":
                        int current = economyAPI.getCrowbars(target);
                        int newAmount = Math.max(0, current - amount);
                        economyAPI.setCrowbars(target, newAmount);
                        sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " crowbars from " + target.getName() + ".");
                        target.sendMessage(ChatColor.RED + "You lost " + amount + " crowbars.");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Unknown action. Use: set, add, or remove.");
                        break;
                }
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Usage: /crowbars [player] [set|add|remove] [amount]");
        }
        return true;
    }
}
