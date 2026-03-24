/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package Level;

import Level.LevelManager;
import java.sql.SQLException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LevelJoinListener
implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            LevelManager.loadPlayer(event.getPlayer());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

