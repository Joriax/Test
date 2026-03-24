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

public class AddBalanceCommand
implements CommandExecutor {
    private final EconomyManager economyManager;

    public AddBalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("economy.addbalance")) {
            if (args.length != 2) {
                sender.sendMessage("Benutzung: /addbalance <Spieler> <Betrag>");
                return false;
            }
            Player player = Bukkit.getPlayer((String)args[0]);
            if (player == null) {
                sender.sendMessage("Spieler nicht gefunden.");
                return false;
            }
            try {
                double amount = Double.parseDouble(args[1]);
                this.economyManager.addBalance(player.getUniqueId(), amount);
                sender.sendMessage("Dem Spieler " + player.getName() + " wurden $" + amount + " hinzugef\u00fcgt.");
            }
            catch (NumberFormatException e) {
                sender.sendMessage("Ung\u00fcltiger Betrag.");
            }
        } else {
            sender.sendMessage("Du hast keine Berechtigung f\u00fcr diesen Befehl.");
        }
        return true;
    }
}

