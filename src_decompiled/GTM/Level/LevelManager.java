/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package Level;

import Level.LevelDatabase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.entity.Player;

public class LevelManager {
    public static void loadPlayer(Player player) throws SQLException {
        PreparedStatement stmt = LevelDatabase.getConnection().prepareStatement("INSERT IGNORE INTO player_levels (uuid) VALUES (?)");
        stmt.setString(1, player.getUniqueId().toString());
        stmt.executeUpdate();
        stmt.close();
    }

    public static int getXP(Player player) throws SQLException {
        PreparedStatement stmt = LevelDatabase.getConnection().prepareStatement("SELECT xp FROM player_levels WHERE uuid = ?");
        stmt.setString(1, player.getUniqueId().toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("xp");
        }
        return 0;
    }

    public static int getLevel(Player player) throws SQLException {
        PreparedStatement stmt = LevelDatabase.getConnection().prepareStatement("SELECT level FROM player_levels WHERE uuid = ?");
        stmt.setString(1, player.getUniqueId().toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("level");
        }
        return 1;
    }

    public static void addXP(Player player, int amount) throws SQLException {
        int xp;
        int level = LevelManager.getLevel(player);
        for (xp = LevelManager.getXP(player) + amount; xp >= LevelManager.xpNeededForNextLevel(level); xp -= LevelManager.xpNeededForNextLevel(level)) {
            player.sendMessage("\u00a7aLevel up! Du bist jetzt Level " + ++level);
        }
        PreparedStatement stmt = LevelDatabase.getConnection().prepareStatement("UPDATE player_levels SET xp = ?, level = ? WHERE uuid = ?");
        stmt.setInt(1, xp);
        stmt.setInt(2, level);
        stmt.setString(3, player.getUniqueId().toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private static int xpNeededForNextLevel(int level) {
        return 100 + level * 50;
    }
}

