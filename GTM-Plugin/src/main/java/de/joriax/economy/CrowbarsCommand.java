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

import de.joriax.economy.EconomyAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrowbarsCommand
implements CommandExecutor {
    private final EconomyAPI economyAPI;

    public CrowbarsCommand(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgef\u00fchrt werden.");
            return true;
        }
        Player player = (Player)sender;
        switch (label.toLowerCase()) {
            case "crowbars": {
                double balance = this.economyAPI.getCrobwars(player);
                player.sendMessage("Dein Crowbars-Guthaben: " + balance);
                break;
            }
            case "seecrowbars": {
                if (args.length < 1) {
                    player.sendMessage("Bitte gib den Namen eines Spielers an.");
                    return true;
                }
                Player target = player.getServer().getPlayer(args[0]);
                if (target != null) {
                    double targetBalance = this.economyAPI.getCrobwars(target);
                    player.sendMessage(target.getName() + " hat " + targetBalance + " Crowbars.");
                    break;
                }
                player.sendMessage("Spieler nicht gefunden.");
                break;
            }
            case "addcrowbars": {
                if (args.length < 2) {
                    player.sendMessage("Bitte gib den Namen eines Spielers und den Betrag an.");
                    return true;
                }
                Player addTarget = player.getServer().getPlayer(args[0]);
                double addAmount = Double.parseDouble(args[1]);
                if (addTarget != null) {
                    this.economyAPI.addCrobwars(addTarget, addAmount);
                    player.sendMessage("Du hast " + addAmount + " Crowbars zu " + addTarget.getName() + " hinzugef\u00fcgt.");
                    break;
                }
                player.sendMessage("Spieler nicht gefunden.");
                break;
            }
            case "removecrowbars": {
                if (args.length < 2) {
                    player.sendMessage("Bitte gib den Namen eines Spielers und den Betrag an.");
                    return true;
                }
                Player removeTarget = player.getServer().getPlayer(args[0]);
                double removeAmount = Double.parseDouble(args[1]);
                if (removeTarget != null) {
                    if (this.economyAPI.removeCrobwars(removeTarget, removeAmount)) {
                        player.sendMessage("Du hast " + removeAmount + " Crowbars von " + removeTarget.getName() + " abgezogen.");
                        break;
                    }
                    player.sendMessage("Nicht gen\u00fcgend Crowbars bei " + removeTarget.getName() + ".");
                    break;
                }
                player.sendMessage("Spieler nicht gefunden.");
                break;
            }
            default: {
                player.sendMessage("Unbekannter Befehl.");
            }
        }
        return true;
    }
}

