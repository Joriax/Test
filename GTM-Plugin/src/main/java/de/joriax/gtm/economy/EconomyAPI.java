package de.joriax.gtm.economy;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Public API for the GTM Economy system.
 * Can be accessed via: GTMPlugin.getInstance().getEconomyAPI()
 * Or via Bukkit service: Bukkit.getServicesManager().getRegistration(EconomyAPI.class).getProvider()
 */
public class EconomyAPI {

    private static EconomyAPI instance;
    private final EconomyManager economyManager;

    public EconomyAPI(EconomyManager economyManager) {
        this.economyManager = economyManager;
        instance = this;
    }

    public static EconomyAPI getInstance() {
        if (instance == null) {
            instance = GTMPlugin.getInstance().getEconomyAPI();
        }
        return instance;
    }

    // ===== BALANCE METHODS =====

    public double getBalance(Player player) {
        return economyManager.getBalance(player);
    }

    public double getBalance(OfflinePlayer player) {
        return economyManager.getBalance(player);
    }

    public double getBalance(UUID uuid) {
        return economyManager.getBalance(uuid);
    }

    public void setBalance(Player player, double amount) {
        economyManager.setBalance(player, amount);
    }

    public void setBalance(OfflinePlayer player, double amount) {
        economyManager.setBalance(player, amount);
    }

    public void setBalance(UUID uuid, double amount) {
        economyManager.setBalance(uuid, amount);
    }

    public void addBalance(Player player, double amount) {
        economyManager.addBalance(player, amount);
    }

    public void addBalance(OfflinePlayer player, double amount) {
        economyManager.addBalance(player, amount);
    }

    public void addBalance(UUID uuid, double amount) {
        economyManager.addBalance(uuid, amount);
    }

    public void removeBalance(Player player, double amount) {
        economyManager.removeBalance(player, amount);
    }

    public void removeBalance(OfflinePlayer player, double amount) {
        economyManager.removeBalance(player, amount);
    }

    public void removeBalance(UUID uuid, double amount) {
        economyManager.removeBalance(uuid, amount);
    }

    public boolean hasBalance(Player player, double amount) {
        return economyManager.hasBalance(player, amount);
    }

    public boolean hasBalance(OfflinePlayer player, double amount) {
        return economyManager.hasBalance(player, amount);
    }

    public boolean hasBalance(UUID uuid, double amount) {
        return economyManager.hasBalance(uuid, amount);
    }

    /**
     * Transfer money from one player to another.
     * @return true if transfer was successful, false if from player has insufficient funds
     */
    public boolean transfer(Player from, Player to, double amount) {
        return economyManager.transfer(from, to, amount);
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        return economyManager.transfer(from, to, amount);
    }

    // ===== CROWBAR METHODS =====

    public int getCrowbars(Player player) {
        return economyManager.getCrowbars(player);
    }

    public int getCrowbars(UUID uuid) {
        return economyManager.getCrowbars(uuid);
    }

    public void setCrowbars(Player player, int amount) {
        economyManager.setCrowbars(player, amount);
    }

    public void setCrowbars(UUID uuid, int amount) {
        economyManager.setCrowbars(uuid, amount);
    }

    public void addCrowbars(Player player, int amount) {
        economyManager.addCrowbars(player, amount);
    }

    public void addCrowbars(UUID uuid, int amount) {
        economyManager.addCrowbars(uuid, amount);
    }

    public boolean hasCrowbars(Player player, int amount) {
        return economyManager.hasCrowbars(player, amount);
    }

    public boolean hasCrowbars(UUID uuid, int amount) {
        return economyManager.hasCrowbars(uuid, amount);
    }

    // ===== PLAYER INIT =====

    public void initPlayer(UUID uuid, String username) {
        economyManager.initPlayer(uuid, username);
    }

    public void updateUsername(UUID uuid, String username) {
        economyManager.updateUsername(uuid, username);
    }

    public UUID getUUIDByName(String name) {
        return economyManager.getUUIDByName(name);
    }

    public String formatBalance(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fK", amount / 1_000);
        }
        return String.format("%.2f$", amount);
    }
}
