/*
 * Decompiled with CFR 0.152.
 */
package de.joriax.economy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {
    private Connection connection;

    public void connect(String url, String username, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    public double getBalance(UUID uuid) throws SQLException {
        String query = "SELECT balance FROM player_balances WHERE uuid = ?";
        try (PreparedStatement stmt = this.getConnection().prepareStatement(query);){
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double d = rs.getDouble("balance");
                return d;
            }
            double d = 0.0;
            return d;
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connect("jdbc:mysql://localhost:3306/gtm", "gtm", "gtm");
        }
        return this.connection;
    }

    public void setBalance(UUID uuid, double balance) throws SQLException {
        String query = "INSERT INTO player_balances (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(query);){
            stmt.setString(1, uuid.toString());
            stmt.setDouble(2, balance);
            stmt.setDouble(3, balance);
            stmt.executeUpdate();
        }
    }

    public void resetBalance(UUID uuid) throws SQLException {
        String query = "UPDATE player_balances SET balance = 0 WHERE uuid = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(query);){
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        }
    }
}

