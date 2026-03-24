/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.economy;

import de.joriax.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeeBalanceCommand
implements CommandExecutor {
    private final EconomyManager economyManager;

    public SeeBalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("economy.seebalance")) {
            if (args.length != 1) {
                sender.sendMessage("Benutzung: /seebalance <Spieler>");
                return false;
            }
            Player player = Bukkit.getPlayer((String)args[0]);
            if (player == null) {
                sender.sendMessage("Spieler nicht gefunden.");
                return false;
            }
            double balance = this.economyManager.getBalance(player.getUniqueId());
            sender.sendMessage("Das Guthaben von " + player.getName() + " betr\u00e4gt $" + balance);
        } else {
            sender.sendMessage("Du hast keine Berechtigung f\u00fcr diesen Befehl.");
        }
        return true;
    }
}

