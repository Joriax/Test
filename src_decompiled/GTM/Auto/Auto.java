/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package Auto;

import Auto.Autos;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Auto {

    public static class CarCommandExecutor
    implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl verwenden.");
                return true;
            }
            Player player = (Player)sender;
            if (args.length < 1) {
                player.sendMessage("Benutze: /car <autoname>");
                return true;
            }
            String carName = args[0].toLowerCase();
            boolean success = Autos.spawnCar(player, carName);
            if (success) {
                player.sendMessage("Auto '" + carName + "' wurde gespawnt. Setz dich mit Rechtsklick rein.");
            } else {
                player.sendMessage("Unbekannter Autoname. Verf\u00fcgbare Autos: sport, truck, bike");
            }
            return true;
        }
    }
}

