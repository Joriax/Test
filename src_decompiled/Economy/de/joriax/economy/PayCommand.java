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

public class PayCommand
implements CommandExecutor {
    private final EconomyManager economyManager;

    public PayCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player payer = (Player)sender;
            if (args.length != 2) {
                payer.sendMessage("Benutzung: /pay <Spieler> <Betrag>");
                return false;
            }
            Player recipient = Bukkit.getPlayer((String)args[0]);
            if (recipient == null) {
                payer.sendMessage("Spieler nicht gefunden.");
                return false;
            }
            try {
                double amount = Double.parseDouble(args[1]);
                if (this.economyManager.getBalance(payer.getUniqueId()) < amount) {
                    payer.sendMessage("Du hast nicht genug Geld.");
                    return true;
                }
                this.economyManager.subtractBalance(payer.getUniqueId(), amount);
                this.economyManager.addBalance(recipient.getUniqueId(), amount);
                payer.sendMessage("Du hast $" + amount + " an " + recipient.getName() + " gezahlt.");
                recipient.sendMessage("Du hast $" + amount + " von " + payer.getName() + " erhalten.");
            }
            catch (NumberFormatException e) {
                payer.sendMessage("Ung\u00fcltiger Betrag.");
            }
        }
        return true;
    }
}

