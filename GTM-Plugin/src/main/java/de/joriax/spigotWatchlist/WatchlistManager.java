/*
 * Decompiled with CFR 0.152.
 */
package de.joriax.spigotWatchlist;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WatchlistManager {
    private final Set<UUID> watchlistPlayers;

    public WatchlistManager() {
        this.watchlistPlayers = new HashSet<UUID>();
    }

    public Set<UUID> getWatchlistPlayers() {
        return this.watchlistPlayers;
    }

    public void addToWatchlist(UUID playerUUID) {
        this.watchlistPlayers.add(playerUUID);
    }

    public void removeFromWatchlist(UUID playerUUID) {
        this.watchlistPlayers.remove(playerUUID);
    }

    public boolean isOnWatchlist(UUID playerUUID) {
        return this.watchlistPlayers.contains(playerUUID);
    }
}

