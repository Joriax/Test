package de.joriax.gtm.level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelManager {

    private final LevelDatabase levelDatabase;
    private final Map<UUID, PlayerLevelData> cache = new HashMap<>();

    // XP required per level = level * 100
    public static final int XP_PER_LEVEL_MULTIPLIER = 100;

    public LevelManager(LevelDatabase levelDatabase) {
        this.levelDatabase = levelDatabase;
    }

    public void loadPlayer(UUID uuid) {
        if (!levelDatabase.playerExists(uuid)) {
            Player p = Bukkit.getPlayer(uuid);
            String name = p != null ? p.getName() : uuid.toString().substring(0, 8);
            levelDatabase.createPlayer(uuid, name);
        }
        PlayerLevelData data = levelDatabase.loadPlayer(uuid);
        cache.put(uuid, data);
    }

    public void loadPlayer(UUID uuid, String username) {
        if (!levelDatabase.playerExists(uuid)) {
            levelDatabase.createPlayer(uuid, username);
        } else {
            levelDatabase.updateUsername(uuid, username);
        }
        PlayerLevelData data = levelDatabase.loadPlayer(uuid);
        cache.put(uuid, data);
    }

    public void savePlayer(UUID uuid) {
        PlayerLevelData data = cache.get(uuid);
        if (data != null) {
            levelDatabase.savePlayer(data);
        }
    }

    public void unloadPlayer(UUID uuid) {
        savePlayer(uuid);
        cache.remove(uuid);
    }

    private PlayerLevelData getData(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            loadPlayer(uuid);
        }
        return cache.get(uuid);
    }

    public int getLevel(UUID uuid) {
        PlayerLevelData data = getData(uuid);
        return data != null ? data.getLevel() : 1;
    }

    public int getLevel(Player player) {
        return getLevel(player.getUniqueId());
    }

    public int getXP(UUID uuid) {
        PlayerLevelData data = getData(uuid);
        return data != null ? data.getXp() : 0;
    }

    public int getXP(Player player) {
        return getXP(player.getUniqueId());
    }

    public void setLevel(UUID uuid, int level) {
        if (level < 1) level = 1;
        PlayerLevelData data = getData(uuid);
        if (data != null) {
            data.setLevel(level);
            levelDatabase.setLevel(uuid, level);
        }
    }

    public void setLevel(Player player, int level) {
        setLevel(player.getUniqueId(), level);
    }

    public void setXP(UUID uuid, int xp) {
        if (xp < 0) xp = 0;
        PlayerLevelData data = getData(uuid);
        if (data != null) {
            data.setXp(xp);
            levelDatabase.setXP(uuid, xp);
        }
    }

    public void setXP(Player player, int xp) {
        setXP(player.getUniqueId(), xp);
    }

    public void addXP(UUID uuid, int amount) {
        if (amount <= 0) return;
        PlayerLevelData data = getData(uuid);
        if (data == null) return;

        int newXP = data.getXp() + amount;
        int newLevel = data.getLevel();

        // Check for level ups
        while (newXP >= getXPRequiredForNextLevel(newLevel)) {
            newXP -= getXPRequiredForNextLevel(newLevel);
            newLevel++;

            // Notify player of level up
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage(org.bukkit.ChatColor.GOLD + "" + org.bukkit.ChatColor.BOLD +
                        "Level Up! You are now Level " + newLevel + "!");
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }

        data.setXp(newXP);
        data.setLevel(newLevel);
        levelDatabase.savePlayer(data);
    }

    public void addXP(Player player, int amount) {
        addXP(player.getUniqueId(), amount);
    }

    public int getXPRequiredForNextLevel(int currentLevel) {
        return currentLevel * XP_PER_LEVEL_MULTIPLIER;
    }

    public int getXPRequiredForNextLevel(UUID uuid) {
        return getXPRequiredForNextLevel(getLevel(uuid));
    }

    public int getTotalXPForLevel(int level) {
        int total = 0;
        for (int i = 1; i < level; i++) {
            total += getXPRequiredForNextLevel(i);
        }
        return total;
    }

    public Map<UUID, PlayerLevelData> getCache() {
        return cache;
    }
}
