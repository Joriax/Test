/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.economy;

import de.joriax.economy.EconomyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand
implements CommandExecutor {
    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            double balance = this.economyManager.getBalance(player.getUniqueId());
            player.sendMessage("Dein Guthaben betr\u00e4gt: $" + balance);
        } else {
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl nutzen!");
        }
        return true;
    }
}

