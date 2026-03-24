/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package Guns;

import Guns.AmmoType;
import Guns.WeaponManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAmmoCommand
implements CommandExecutor {
    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length == 2) {
                int amount;
                String ammoTypeName = args[0].toUpperCase();
                try {
                    amount = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Ung\u00fcltige Menge! Bitte gib eine Zahl an.");
                    return false;
                }
                try {
                    AmmoType ammoType = AmmoType.valueOf(ammoTypeName);
                    WeaponManager.addAmmo(player, ammoType, amount);
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast " + amount + " " + ammoTypeName + "-Munition erhalten!");
                    return true;
                }
                catch (IllegalArgumentException e) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Ung\u00fcltiger Munitionstyp! Verf\u00fcgbare Typen: AR, PISTOL, SMG, SNIPER.");
                    return false;
                }
            }
            player.sendMessage(String.valueOf(ChatColor.RED) + "Verwendung: /giveammo <ammoType> <amount>");
            return false;
        }
        sender.sendMessage(String.valueOf(ChatColor.RED) + "Nur Spieler k\u00f6nnen diesen Befehl ausf\u00fchren.");
        return false;
    }
}

