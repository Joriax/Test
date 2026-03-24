/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager;

import de.joriax.economy.EconomyAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyManager {
    private final JavaPlugin plugin;
    private EconomyAPI economy;

    public EconomyManager(JavaPlugin plugin, EconomyAPI economyAPI) {
        this.plugin = plugin;
        this.economy = economyAPI;
    }

    public double getBalance(Player player) {
        if (this.economy != null) {
            return this.economy.getBalance(player);
        }
        this.plugin.getLogger().warning("EconomyAPI is null in EconomyManager.");
        return 0.0;
    }
}

