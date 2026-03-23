/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package Dealer;

import Dealer.DealerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DealerCommand
implements CommandExecutor {
    private final DealerPlugin dealerPlugin;

    public DealerCommand(DealerPlugin dealerPlugin) {
        this.dealerPlugin = dealerPlugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dealer")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                this.dealerPlugin.spawnDealer(player);
                return true;
            }
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl verwenden.");
        }
        return false;
    }
}

