/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package de.joriax.spigotWatchlist;

import de.joriax.spigotWatchlist.WatchlistSpigot;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class WatchlistMessageListener
implements PluginMessageListener {
    private final WatchlistSpigot plugin;

    public WatchlistMessageListener(WatchlistSpigot plugin) {
        this.plugin = plugin;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));){
            String subChannel = in.readUTF();
            if (subChannel.equals("WatchlistUpdate")) {
                String playerName = in.readUTF();
                boolean isOnline = in.readBoolean();
                String serverName = in.readUTF();
                this.plugin.getServer().getLogger().info("Received WatchlistUpdate for " + playerName + ": " + (String)(isOnline ? "Online on " + serverName : "Offline"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getOpenInventory().getTitle().equals("Watchlist")) continue;
                    this.plugin.getWatchlistCommand().openWatchlistGUI(p);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

