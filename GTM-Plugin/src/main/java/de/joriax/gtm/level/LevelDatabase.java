package de.joriax.gtm.level;

import de.joriax.gtm.GTMPlugin;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class LevelDatabase {

    private final GTMPlugin plugin;
    private Connection connection;
    private final String host = "localhost";
    private final int port = 3306;
    private final String database = "gtm";
    private final String username = "gtm";
    private final String password = "gtm";

    public LevelDatabase(GTMPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("[Level] Connected to MySQL database.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Failed to connect to MySQL: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("[Level] Disconnected from MySQL database.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Failed to disconnect from MySQL: " + e.getMessage(), e);
        }
    }

    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS player_levels (" +
                "uuid VARCHAR(36) NOT NULL PRIMARY KEY, " +
                "username VARCHAR(16) NOT NULL DEFAULT '', " +
                "xp INT NOT NULL DEFAULT 0, " +
                "level INT NOT NULL DEFAULT 1" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.executeUpdate();
            plugin.getLogger().info("[Level] Tables verified/created.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Failed to create tables: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                plugin.getLogger().warning("[Level] Database connection lost. Reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error checking connection: " + e.getMessage(), e);
            connect();
        }
        return connection;
    }

    public boolean playerExists(UUID uuid) {
        String sql = "SELECT uuid FROM player_levels WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error checking player: " + e.getMessage(), e);
            return false;
        }
    }

    public void createPlayer(UUID uuid, String username) {
        if (playerExists(uuid)) return;
        String sql = "INSERT INTO player_levels (uuid, username, xp, level) VALUES (?, ?, 0, 1)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error creating player: " + e.getMessage(), e);
        }
    }

    public PlayerLevelData loadPlayer(UUID uuid) {
        String sql = "SELECT xp, level FROM player_levels WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int xp = rs.getInt("xp");
                int level = rs.getInt("level");
                return new PlayerLevelData(uuid, xp, level);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error loading player: " + e.getMessage(), e);
        }
        return new PlayerLevelData(uuid, 0, 1);
    }

    public void savePlayer(PlayerLevelData data) {
        String sql = "UPDATE player_levels SET xp = ?, level = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, data.getXp());
            ps.setInt(2, data.getLevel());
            ps.setString(3, data.getUuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error saving player: " + e.getMessage(), e);
        }
    }

    public int getXP(UUID uuid) {
        String sql = "SELECT xp FROM player_levels WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("xp");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error getting xp: " + e.getMessage(), e);
        }
        return 0;
    }

    public int getLevel(UUID uuid) {
        String sql = "SELECT level FROM player_levels WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("level");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error getting level: " + e.getMessage(), e);
        }
        return 1;
    }

    public void setXP(UUID uuid, int xp) {
        String sql = "UPDATE player_levels SET xp = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, xp);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error setting xp: " + e.getMessage(), e);
        }
    }

    public void setLevel(UUID uuid, int level) {
        String sql = "UPDATE player_levels SET level = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, level);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error setting level: " + e.getMessage(), e);
        }
    }

    public void updateUsername(UUID uuid, String username) {
        String sql = "UPDATE player_levels SET username = ? WHERE uuid = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "[Level] Error updating username: " + e.getMessage(), e);
        }
    }
}
