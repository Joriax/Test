/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package de.joriax.tradeSystem;

import de.joriax.tradeSystem.TradeManager;
import de.joriax.tradeSystem.TradeSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TradeListener
implements Listener {
    private final TradeManager plugin;

    public TradeListener(TradeManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        int slot;
        Player player = (Player)event.getWhoClicked();
        TradeSession tradeSession = this.plugin.getTradeSession(player);
        if (tradeSession != null && ((slot = event.getRawSlot()) == 33 || slot == 29)) {
            event.setCancelled(true);
            tradeSession.handleClick(player, slot);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        TradeSession tradeSession = this.plugin.getTradeSession(player);
        if (tradeSession != null) {
            tradeSession.cancelTrade();
            this.plugin.removeTradeSession(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TradeSession tradeSession = this.plugin.getTradeSession(player);
        if (tradeSession != null) {
            tradeSession.cancelTrade();
            this.plugin.removeTradeSession(player);
        }
    }
}

