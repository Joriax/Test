/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package Throw;

import Throw.Smoke;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveSmokeCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl verwenden.");
            return true;
        }
        Player player = (Player)sender;
        ItemStack smokeGrenade = Smoke.createSmokeGrenade();
        player.getInventory().addItem(new ItemStack[]{smokeGrenade});
        player.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast eine Smoke Granate erhalten!");
        return true;
    }
}

