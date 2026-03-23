/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package de.joriax.spigotWatchlist;

import de.joriax.spigotWatchlist.WatchlistCommand;
import de.joriax.spigotWatchlist.WatchlistManager;
import de.joriax.spigotWatchlist.WatchlistMessageListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class WatchlistSpigot
extends JavaPlugin
implements PluginMessageListener {
    private WatchlistCommand watchlistCommand;
    private WatchlistManager watchlistManager;

    public void onEnable() {
        this.watchlistManager = new WatchlistManager(this);
        this.watchlistCommand = new WatchlistCommand(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "watchlist:ban");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "watchlist:ban", (PluginMessageListener)this);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "watchlist:gui");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "watchlist:gui", (PluginMessageListener)this);
        this.getCommand("watchlist").setExecutor((CommandExecutor)this.watchlistCommand);
        this.getServer().getPluginManager().registerEvents((Listener)this.watchlistCommand, (Plugin)this);
        this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel((Plugin)this, "BungeeCord", (PluginMessageListener)new WatchlistMessageListener(this));
    }

    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel((Plugin)this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel((Plugin)this);
    }

    public WatchlistCommand getWatchlistCommand() {
        return this.watchlistCommand;
    }

    public WatchlistManager getWatchlistManager() {
        return this.watchlistManager;
    }

    public void sendBanRequest(String playerName, int reasonId, String adminName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("BanPlayer");
            out.writeUTF(playerName);
            out.writeInt(reasonId);
            out.writeUTF(adminName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Player player = this.getServer().getPlayer(adminName);
        if (player != null) {
            player.sendPluginMessage((Plugin)this, "watchlist:ban", stream.toByteArray());
            this.getLogger().info("Bann-Anfrage f\u00fcr " + playerName + " gesendet.");
        }
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("watchlist:gui")) {
            return;
        }
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));){
            String subChannel = in.readUTF();
            if (subChannel.equals("OpenGUI")) {
                String playerName = in.readUTF();
                Player targetPlayer = Bukkit.getPlayer((String)playerName);
                if (targetPlayer != null) {
                    this.watchlistCommand.openWatchlistGUI(targetPlayer);
                    this.getLogger().info("GUI wurde f\u00fcr " + playerName + " ge\u00f6ffnet.");
                } else {
                    this.getLogger().warning("Spieler " + playerName + " nicht gefunden!");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

