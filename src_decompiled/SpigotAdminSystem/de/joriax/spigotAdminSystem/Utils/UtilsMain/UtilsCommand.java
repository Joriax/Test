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
package de.joriax.spigotAdminSystem.Utils.UtilsMain;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.GUIManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UtilsCommand
implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + "\u00a7cOnly players can use this command!");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("utils.gui")) {
            player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        GUIManager.openMainGUI(player);
        return true;
    }
}

