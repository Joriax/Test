/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 */
package de.joriax.spigotAdminSystem.Utils.Listeners;

import de.joriax.economy.EconomyAPI;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private final EconomyAPI economyAPI;
    private final Random random = new Random();

    public PlayerKillListener(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();
        if (killer != null) {
            int amount = 100 + this.random.nextInt(401);
            this.economyAPI.addBalance(killer, (double)amount);
            killer.sendMessage(this.prefix + "You killed " + killed.getName() + " and received $" + amount + "!");
        }
    }
}

