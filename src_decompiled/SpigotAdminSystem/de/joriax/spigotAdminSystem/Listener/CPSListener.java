/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 */
package de.joriax.spigotAdminSystem.Listener;

import de.joriax.spigotAdminSystem.Manager.CPSManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CPSListener
implements Listener {
    private final CPSManager cpsManager;

    public CPSListener(CPSManager cpsManager) {
        this.cpsManager = cpsManager;
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.cpsManager.isInCombat(player)) {
            this.cpsManager.click(player);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (this.cpsManager.isInCombat(player)) {
            this.cpsManager.click(player);
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player)event.getDamager();
            this.cpsManager.startCombat(player);
        }
    }

    @EventHandler
    public void onPlayerHitBy(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            this.cpsManager.startCombat(player);
        }
    }
}

