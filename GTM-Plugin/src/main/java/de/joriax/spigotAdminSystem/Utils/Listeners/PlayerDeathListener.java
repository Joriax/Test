/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 */
package de.joriax.spigotAdminSystem.Utils.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Player killer = deceased.getKiller();
        String message = this.prefix + String.valueOf(ChatColor.RED) + deceased.getName() + " was killed by " + (killer != null ? killer.getName() : "the environment") + "!";
        for (Player player : event.getEntity().getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
        event.setDeathMessage(null);
    }
}

