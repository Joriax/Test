/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package de.joriax.spigotAdminSystem.CommandSpy;

import de.joriax.spigotAdminSystem.CommandSpy.CommandSpy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandListener
implements Listener {
    private final JavaPlugin plugin;
    private final CommandSpy commandSpyCommand;
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6CommandSpy&8] &r");

    public CommandListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandSpyCommand = new CommandSpy(plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        this.commandSpyCommand.logAction("Command Executed", player.getName() + " executed: " + command);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!this.commandSpyCommand.isSpying(p) || !p.hasPermission("spigot.commandspy.use")) continue;
            p.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + player.getName() + " executed: " + command);
        }
    }
}

