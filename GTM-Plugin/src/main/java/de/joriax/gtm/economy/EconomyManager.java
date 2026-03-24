package de.joriax.gtm.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EconomyManager {

    private final DatabaseManager databaseManager;

    public EconomyManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initPlayer(UUID uuid, String username) {
        databaseManager.createPlayer(uuid, username);
    }

    public double getBalance(UUID uuid) {
        return databaseManager.getBalance(uuid);
    }

    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    public void setBalance(UUID uuid, double amount) {
        databaseManager.setBalance(uuid, Math.max(0, amount));
    }

    public void setBalance(Player player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void addBalance(UUID uuid, double amount) {
        if (amount <= 0) return;
        databaseManager.addBalance(uuid, amount);
    }

    public void addBalance(Player player, double amount) {
        addBalance(player.getUniqueId(), amount);
    }

    public void addBalance(OfflinePlayer player, double amount) {
        addBalance(player.getUniqueId(), amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        if (amount <= 0) return;
        databaseManager.removeBalance(uuid, amount);
    }

    public void removeBalance(Player player, double amount) {
        removeBalance(player.getUniqueId(), amount);
    }

    public void removeBalance(OfflinePlayer player, double amount) {
        removeBalance(player.getUniqueId(), amount);
    }

    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public boolean hasBalance(Player player, double amount) {
        return hasBalance(player.getUniqueId(), amount);
    }

    public boolean hasBalance(OfflinePlayer player, double amount) {
        return hasBalance(player.getUniqueId(), amount);
    }

    public int getCrowbars(UUID uuid) {
        return databaseManager.getCrowbars(uuid);
    }

    public int getCrowbars(Player player) {
        return getCrowbars(player.getUniqueId());
    }

    public void setCrowbars(UUID uuid, int amount) {
        databaseManager.setCrowbars(uuid, Math.max(0, amount));
    }

    public void setCrowbars(Player player, int amount) {
        setCrowbars(player.getUniqueId(), amount);
    }

    public void addCrowbars(UUID uuid, int amount) {
        if (amount <= 0) return;
        databaseManager.addCrowbars(uuid, amount);
    }

    public void addCrowbars(Player player, int amount) {
        addCrowbars(player.getUniqueId(), amount);
    }

    public boolean hasCrowbars(UUID uuid, int amount) {
        return getCrowbars(uuid) >= amount;
    }

    public boolean hasCrowbars(Player player, int amount) {
        return hasCrowbars(player.getUniqueId(), amount);
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        if (!hasBalance(from, amount)) return false;
        removeBalance(from, amount);
        addBalance(to, amount);
        return true;
    }

    public boolean transfer(Player from, Player to, double amount) {
        return transfer(from.getUniqueId(), to.getUniqueId(), amount);
    }

    public void updateUsername(UUID uuid, String username) {
        databaseManager.updateUsername(uuid, username);
    }

    public UUID getUUIDByName(String name) {
        return databaseManager.getUUIDByName(name);
    }
}
