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

import de.joriax.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveBalanceCommand
implements CommandExecutor {
    private final EconomyAPI economyAPI;

    public RemoveBalanceCommand(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("economy.removebalance")) {
            sender.sendMessage("Du hast keine Berechtigung, diesen Befehl auszuf\u00fchren.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage("Benutzung: /removebalance <Spieler> <Betrag>");
            return true;
        }
        Player targetPlayer = Bukkit.getPlayer((String)args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("Spieler nicht gefunden.");
            return true;
        }
        try {
            double amount = Double.parseDouble(args[1]);
            if (this.economyAPI.removeBalance(targetPlayer, amount)) {
                sender.sendMessage("Du hast " + amount + " von " + targetPlayer.getName() + " abgezogen.");
                targetPlayer.sendMessage("Dir wurden " + amount + " von deinem Guthaben abgezogen.");
            } else {
                sender.sendMessage("Nicht genug Guthaben auf dem Konto von " + targetPlayer.getName() + ".");
            }
        }
        catch (NumberFormatException e) {
            sender.sendMessage("Bitte gib einen g\u00fcltigen Betrag ein.");
        }
        return true;
    }
}

