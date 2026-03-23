/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerQuitEvent
 */
package de.joriax.spigotAdminSystem.Listener;

import de.joriax.spigotAdminSystem.Manager.MySQLManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener
implements Listener {
    private final MySQLManager mysqlManager;
    private final HashMap<UUID, Long> sessionStartTime;
    private final Map<String, Long> lastSeen;

    public PlayerQuitListener(MySQLManager mysqlManager, HashMap<UUID, Long> sessionStartTime, Map<String, Long> lastSeen) {
        this.mysqlManager = mysqlManager;
        this.sessionStartTime = sessionStartTime;
        this.lastSeen = lastSeen;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.lastSeen.put(event.getPlayer().getName(), System.currentTimeMillis());
        UUID playerId = event.getPlayer().getUniqueId();
        long joinTime = this.sessionStartTime.getOrDefault(playerId, System.currentTimeMillis());
        long sessionDuration = System.currentTimeMillis() - joinTime;
        this.mysqlManager.updatePlaytime(playerId.toString(), sessionDuration);
        this.sessionStartTime.remove(playerId);
    }
}

