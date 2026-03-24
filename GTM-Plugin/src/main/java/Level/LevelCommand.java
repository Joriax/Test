/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package Level;

import Level.LevelManager;
import java.sql.SQLException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl nutzen.");
            return true;
        }
        Player player = (Player)sender;
        try {
            int level = LevelManager.getLevel(player);
            int xp = LevelManager.getXP(player);
            sender.sendMessage("\u00a7eDein Level: \u00a7a" + level + " \u00a7e| XP: \u00a7a" + xp);
        }
        catch (SQLException e) {
            sender.sendMessage("\u00a7cFehler beim Laden deiner Daten.");
            e.printStackTrace();
        }
        return true;
    }
}

