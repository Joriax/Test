/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package de.joriax.spigotAdminSystem.Vanish.Manager;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Vanish.Command.VanishCommand;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class VanishManager {
    private final SpigotAdminSystem plugin;
    private final VanishCommand vanishCommand;
    private final Set<Player> vanishedPlayers = new HashSet<Player>();

    public VanishManager(SpigotAdminSystem plugin, VanishCommand vanishCommand) {
        this.plugin = plugin;
        this.vanishCommand = vanishCommand;
    }

    public void vanishPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("spigot.vanish.see")) {
                p.hidePlayer((Plugin)this.plugin, player);
            }
            if (!p.hasPermission("spigot.vanish.notify")) continue;
            p.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GRAY) + player.getName() + " is now in Vanish.");
        }
        this.vanishedPlayers.add(player);
        this.vanishCommand.logAction("Vanish Enable", player.getName() + " entered Vanish mode");
    }

    public void unvanishPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer((Plugin)this.plugin, player);
            if (!p.hasPermission("spigot.vanish.notify")) continue;
            p.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GRAY) + player.getName() + " is no longer in Vanish.");
        }
        this.vanishedPlayers.remove(player);
        this.vanishCommand.logAction("Vanish Disable", player.getName() + " exited Vanish mode");
    }

    public boolean isVanished(Player player) {
        return this.vanishedPlayers.contains(player);
    }
}

