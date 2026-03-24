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

public class CarCommandExecutor
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler k\u00f6nnen Autos spawnen!");
            return true;
        }
        Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage("Verwendung: /car <name>");
            return true;
        }
        String carName = args[0].toLowerCase();
        boolean success = Autos.spawnCar(player, carName);
        if (success) {
            player.sendMessage("Auto '" + carName + "' wurde gespawnt.");
        } else {
            player.sendMessage("Dieses Auto existiert nicht.");
        }
        return true;
    }
}

