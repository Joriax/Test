/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package de.joriax.tradeSystem;

import de.joriax.tradeSystem.TradeSession;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class TradeManager {
    private final Map<Player, TradeSession> tradeSessions = new HashMap<Player, TradeSession>();

    public void addTradeSession(Player player1, Player player2, TradeSession tradeSession) {
        this.tradeSessions.put(player1, tradeSession);
        this.tradeSessions.put(player2, tradeSession);
    }

    public TradeSession getTradeSession(Player player) {
        return this.tradeSessions.get(player);
    }

    public void removeTradeSession(Player player) {
        this.tradeSessions.remove(player);
    }
}

