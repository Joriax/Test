/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package Guns;

import Guns.Weapon;
import Guns.WeaponManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GunCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length == 1) {
                String weaponName = args[0].toLowerCase();
                Weapon weapon = WeaponManager.getWeaponByName(weaponName);
                if (weapon != null) {
                    player.getInventory().addItem(new ItemStack[]{weapon.getWeaponItem()});
                } else {
                    player.sendMessage("Unbekannte Waffe: " + weaponName);
                }
                return true;
            }
            player.sendMessage("Verwendung: /giveweapon <weapon_name>");
            return false;
        }
        sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl ausf\u00fchren.");
        return false;
    }
}

