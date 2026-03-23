/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package de.joriax.spigotAdminSystem.Utils.BlockedCommands;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class BlockCommandListener
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private final String[] blockedCommands = new String[]{"/op", "/deop", "/gamemode", "/summon", "/setworldspawn", "/spawnpoint", "/worldborder", "/tell", "/list", "/end", "/team", "/scoreboard", "/restart", "/server", "/send", "/alert", "/glist", "/bungeecord", "/pl", "/plugins", "/ver", "/version", "/bukkit:plugins", "/bukkit:version", "/bukkit:help", "/bukkit:pl", "/bungee", "/bukkit:?", "/bukkit:about", "/bukkit:ver"};

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];
        if (event.getPlayer().hasPermission("commandblocker.allowall")) {
            return;
        }
        for (String blockedCommand : this.blockedCommands) {
            if (!command.equalsIgnoreCase(blockedCommand)) continue;
            event.getPlayer().sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            event.setCancelled(true);
            return;
        }
    }
}

