/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.tradeSystem;

import de.joriax.tradeSystem.TradePlugin;
import de.joriax.tradeSystem.TradeSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand
implements CommandExecutor {
    private final TradePlugin plugin;

    public TradeCommand(TradePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cNur Spieler k\u00f6nnen diesen Befehl verwenden.");
            return true;
        }
        Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage("\u00a7cVerwendung: /trade <Spieler>");
            return true;
        }
        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("\u00a7cSpieler nicht gefunden.");
            return true;
        }
        if (player.getLocation().distance(target.getLocation()) > 10.0) {
            player.sendMessage("\u00a7cDu bist zu weit von " + target.getName() + " entfernt.");
            return true;
        }
        TradeSession tradeSession = new TradeSession(player, target, this.plugin);
        this.plugin.getTradeManager().addTradeSession(player, target, tradeSession);
        return true;
    }
}

