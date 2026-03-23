/*
 * Decompiled with CFR 0.152.
 */
package Level;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LevelDatabase {
    private static Connection connection;

    public static void connect(String host, String database, String user, String password) throws SQLException {
        String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false";
        connection = DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void setupTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player_levels (uuid VARCHAR(36) PRIMARY KEY,level INT NOT NULL DEFAULT 1,xp INT NOT NULL DEFAULT 0)");
        stmt.close();
    }
}

