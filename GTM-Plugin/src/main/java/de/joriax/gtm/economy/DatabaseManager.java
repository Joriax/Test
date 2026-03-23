package de.joriax.gtm.economy;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final GTMPlugin plugin;
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public DatabaseManager(GTMPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString("database.host", "localhost");
        this.port = config.getInt("database.port", 3306);
        this.database = config.getString("database.name", "gtm");
        this.username = config.getString("database.user", "root");
        this.password = config.getString("database.password", "password");
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("[Economy] Connected to MySQL database.");
            setupTables();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Failed to connect to MySQL: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("[Economy] Disconnected from MySQL database.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Failed to disconnect from MySQL: " + e.getMessage(), e);
        }
    }

    private void setupTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS player_balances ("
                + "uuid VARCHAR(36) NOT NULL PRIMARY KEY, "
                + "username VARCHAR(16) NOT NULL, "
                + "balance DOUBLE NOT NULL DEFAULT 0.0, "
                + "crowbars INT NOT NULL DEFAULT 0"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );
            plugin.getLogger().info("[Economy] Tables verified/created.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Failed to create tables: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                plugin.getLogger().warning("[Economy] Database connection lost. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error checking connection: " + e.getMessage(), e);
            connect();
        }
        return connection;
    }

    public boolean playerExists(UUID uuid) {
        String sql = "SELECT uuid FROM player_balances WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error checking player: " + e.getMessage(), e);
            return false;
        }
    }

    public void createPlayer(UUID uuid, String username) {
        if (playerExists(uuid)) return;
        String sql = "INSERT INTO player_balances (uuid, username, balance, crowbars) VALUES (?, ?, 0.0, 0)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error creating player: " + e.getMessage(), e);
        }
    }

    public double getBalance(UUID uuid) {
        String sql = "SELECT balance FROM player_balances WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error getting balance: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public void setBalance(UUID uuid, double amount) {
        String sql = "UPDATE player_balances SET balance = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error setting balance: " + e.getMessage(), e);
        }
    }

    public void addBalance(UUID uuid, double amount) {
        String sql = "UPDATE player_balances SET balance = balance + ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error adding balance: " + e.getMessage(), e);
        }
    }

    public void removeBalance(UUID uuid, double amount) {
        String sql = "UPDATE player_balances SET balance = GREATEST(0, balance - ?) WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error removing balance: " + e.getMessage(), e);
        }
    }

    public int getCrowbars(UUID uuid) {
        String sql = "SELECT crowbars FROM player_balances WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("crowbars");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error getting crowbars: " + e.getMessage(), e);
        }
        return 0;
    }

    public void setCrowbars(UUID uuid, int amount) {
        String sql = "UPDATE player_balances SET crowbars = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error setting crowbars: " + e.getMessage(), e);
        }
    }

    public void addCrowbars(UUID uuid, int amount) {
        String sql = "UPDATE player_balances SET crowbars = crowbars + ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error adding crowbars: " + e.getMessage(), e);
        }
    }

    public void updateUsername(UUID uuid, String username) {
        String sql = "UPDATE player_balances SET username = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error updating username: " + e.getMessage(), e);
        }
    }

    public UUID getUUIDByName(String name) {
        String sql = "SELECT uuid FROM player_balances WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Economy] Error getting UUID by name: " + e.getMessage(), e);
        }
        return null;
    }
}
