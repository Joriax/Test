/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package de.joriax.economy;

import de.joriax.economy.EconomyManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EconomyAPI {
    private final EconomyManager economyManager;

    public EconomyAPI(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    public double getBalance(Player player) {
        return this.economyManager.getBalance(player.getUniqueId());
    }

    public double getBalance(OfflinePlayer player) {
        return this.economyManager.getBalance(player.getUniqueId());
    }

    public void setBalance(Player player, double amount) {
        this.economyManager.setBalance(player.getUniqueId(), amount);
    }

    public void addBalance(Player player, double amount) {
        this.economyManager.addBalance(player.getUniqueId(), amount);
    }

    public boolean withdrawBalance(Player player, double amount) {
        double balance = this.economyManager.getBalance(player.getUniqueId());
        if (balance >= amount) {
            this.economyManager.subtractBalance(player.getUniqueId(), amount);
            return true;
        }
        return false;
    }

    public void addBalance(OfflinePlayer player, double amount) {
        this.economyManager.addBalance(player.getUniqueId(), amount);
    }

    public double seeBalance(Player player) {
        return this.economyManager.getBalance(player.getUniqueId());
    }

    public double seeBalance(OfflinePlayer player) {
        return this.economyManager.getBalance(player.getUniqueId());
    }

    public boolean removeBalance(Player player, double amount) {
        return this.withdrawBalance(player, amount);
    }

    public double getCrobwars(Player player) {
        return this.economyManager.getCrobwarsBalance(player.getUniqueId());
    }

    public double getCrobwars(OfflinePlayer player) {
        return this.economyManager.getCrobwarsBalance(player.getUniqueId());
    }

    public void addCrobwars(Player player, double amount) {
        this.economyManager.addCrobwars(player.getUniqueId(), amount);
    }

    public boolean removeCrobwars(Player player, double amount) {
        double currentBalance = this.economyManager.getCrobwarsBalance(player.getUniqueId());
        if (currentBalance >= amount) {
            this.economyManager.subtractCrobwars(player.getUniqueId(), amount);
            return true;
        }
        return false;
    }
}

