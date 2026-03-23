/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Horse
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 */
package Auto;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class AutoProtectionListener
implements Listener {
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        Horse horse;
        Entity entity;
        Player player;
        Entity entity2 = event.getEntity();
        if (entity2 instanceof Player && (player = (Player)entity2).isInsideVehicle() && (entity = player.getVehicle()) instanceof Horse && (horse = (Horse)entity).isInvisible() && !horse.hasAI()) {
            event.setCancelled(true);
        }
    }
}

