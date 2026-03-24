package de.joriax.gtm.economy.commands;

import de.joriax.gtm.GTMPlugin;
import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final GTMPlugin plugin;

    public BalanceCommand(GTMPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        EconomyAPI api = plugin.getEconomyAPI();
        double balance = api.getBalance(player);
        int crowbars = api.getCrowbars(player);

        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "── Your Balance ──");
        player.sendMessage(ChatColor.YELLOW + "Money: " + ChatColor.GREEN + api.formatBalance(balance));
        player.sendMessage(ChatColor.YELLOW + "Crowbars: " + ChatColor.AQUA + crowbars);
        return true;
    }
}
