/*
 * Decompiled with CFR 0.152.
 */
package de.joriax.levelSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {
    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 300;
    private boolean leveledUp = false;
    private static final int XP_PER_LEVEL = 300;

    public int getLevel() {
        return this.level;
    }

    public int getXp() {
        return this.xp;
    }

    public int getXpToNextLevel() {
        return this.xpToNextLevel;
    }

    public void setLevel(int level) {
        this.level = level;
        this.xpToNextLevel = 300 * level * 2;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int amount) {
        this.xp += amount;
        this.leveledUp = false;
        while (this.xp >= this.xpToNextLevel) {
            this.xp -= this.xpToNextLevel;
            ++this.level;
            this.xpToNextLevel = 300 * this.level * 2;
            this.leveledUp = true;
        }
    }

    public boolean hasLeveledUp() {
        return this.leveledUp;
    }

    public void resetLeveledUp() {
        this.leveledUp = false;
    }

    public void loadFromDatabase(UUID playerId, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT level, xp FROM player_data WHERE uuid = ?");
            statement.setString(1, playerId.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                this.level = resultSet.getInt("level");
                this.xp = resultSet.getInt("xp");
                this.xpToNextLevel = 300 * this.level;
                System.out.println("Daten geladen f\u00fcr Spieler: " + String.valueOf(playerId) + " | Level: " + this.level + " | XP: " + this.xp);
            } else {
                this.level = 1;
                this.xp = 0;
                this.xpToNextLevel = 300;
                System.out.println("Keine Daten gefunden f\u00fcr Spieler: " + String.valueOf(playerId) + ". Standardwerte gesetzt.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveToDatabase(UUID playerId, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO player_data (uuid, level, xp) VALUES (?, ?, ?)");
            statement.setString(1, playerId.toString());
            statement.setInt(2, this.level);
            statement.setInt(3, this.xp);
            statement.executeUpdate();
            System.out.println("Daten gespeichert f\u00fcr Spieler: " + String.valueOf(playerId) + " | Level: " + this.level + " | XP: " + this.xp);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

