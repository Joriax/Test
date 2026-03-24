/*
 * Decompiled with CFR 0.152.
 */
package de.joriax.economy;

import de.joriax.economy.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EconomyManager {
    private final DatabaseManager databaseManager;

    public EconomyManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public double getBalance(UUID uuid) {
        try {
            return this.databaseManager.getBalance(uuid);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public void setBalance(UUID uuid, double amount) {
        try {
            this.databaseManager.setBalance(uuid, amount);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBalance(UUID uuid, double amount) {
        this.setBalance(uuid, this.getBalance(uuid) + amount);
    }

    public void subtractBalance(UUID uuid, double amount) {
        this.setBalance(uuid, this.getBalance(uuid) - amount);
    }

    public double getCrobwarsBalance(UUID playerId) {
        double crobwarsBalance = 0.0;
        try (Connection connection = this.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT crobwars FROM player_balances WHERE uuid = ?");){
            statement.setString(1, playerId.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                crobwarsBalance = resultSet.getDouble("crobwars");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return crobwarsBalance;
    }

    public void addCrobwars(UUID playerId, double amount) {
        try (Connection connection = this.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player_balances SET crobwars = crobwars + ? WHERE uuid = ?");){
            statement.setDouble(1, amount);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void subtractCrobwars(UUID playerId, double amount) {
        try (Connection connection = this.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player_balances SET crobwars = crobwars - ? WHERE uuid = ?");){
            statement.setDouble(1, amount);
            statement.setString(2, playerId.toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCrobwarsColumn() {
        String query = "ALTER TABLE player_balances ADD COLUMN crobwars DOUBLE DEFAULT 0;";
        try (Connection connection = this.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);){
            statement.executeUpdate();
            System.out.println("Column 'crobwars' added successfully.");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1060) {
                System.out.println("Column 'crobwars' already exists.");
            }
            e.printStackTrace();
        }
    }
}

