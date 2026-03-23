/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAListener;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACCEPTCommand
implements CommandExecutor {
    private final TPAManager tpaManager;
    private final TPAListener tpaListener;

    public TPACCEPTCommand(TPAManager tpaManager, TPAListener tpaListener) {
        this.tpaManager = tpaManager;
        this.tpaListener = tpaListener;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player target = (Player)sender;
        if (!this.tpaManager.isTpaEnabled(target)) {
            target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Your TPA is disabled.");
            return true;
        }
        if (!sender.hasPermission("spigot.tpaccept.use")) {
            target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        UUID senderId = this.tpaManager.getRequestSender(target);
        if (senderId == null) {
            target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You have no open teleport requests.");
            return true;
        }
        Player senderPlayer = Bukkit.getPlayer((UUID)senderId);
        if (senderPlayer == null) {
            target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "The player who sent the request is no longer online.");
            return true;
        }
        int delay = this.tpaManager.getTeleportDelay(senderPlayer);
        this.tpaListener.acceptTPA(senderPlayer, target, delay);
        return true;
    }
}

