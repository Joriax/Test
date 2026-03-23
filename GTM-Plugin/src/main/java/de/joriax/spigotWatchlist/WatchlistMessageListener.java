/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package de.joriax.spigotWatchlist;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class WatchlistMessageListener
implements PluginMessageListener {
    private final WatchlistCommand watchlistCommand;

    public WatchlistMessageListener(WatchlistCommand watchlistCommand) {
        this.watchlistCommand = watchlistCommand;
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
                Bukkit.getLogger().info("Received WatchlistUpdate for " + playerName + ": " + (String)(isOnline ? "Online on " + serverName : "Offline"));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getOpenInventory().getTitle().equals("Watchlist")) continue;
                    this.watchlistCommand.openWatchlistGUI(p);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

