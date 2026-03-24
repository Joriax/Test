/*
 * Decompiled with CFR 0.152.
 */
package de.joriax.spigotAdminSystem.Manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    public MySQLManager(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.database, this.username, this.password);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public long getTotalPlaytime(String playerUUID) {
        long totalPlaytime = 0L;
        if (this.connection != null) {
            try {
                String query = "SELECT total_playtime FROM player_playtime WHERE player_uuid = ?";
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.setString(1, playerUUID);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    totalPlaytime = resultSet.getLong("total_playtime");
                }
                resultSet.close();
                statement.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database connection is not established.");
        }
        return totalPlaytime;
    }

    public void updatePlaytime(String playerUUID, long playtime) {
        if (this.connection != null) {
            try {
                String query = "INSERT INTO player_playtime (player_uuid, total_playtime) VALUES (?, ?) ON DUPLICATE KEY UPDATE total_playtime = total_playtime + ?";
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.setString(1, playerUUID);
                statement.setLong(2, playtime);
                statement.setLong(3, playtime);
                statement.executeUpdate();
                statement.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database connection is not established.");
        }
    }
}

