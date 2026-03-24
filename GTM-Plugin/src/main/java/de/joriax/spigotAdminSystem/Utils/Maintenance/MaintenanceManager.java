/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.plugin.Plugin
 */
package de.joriax.spigotAdminSystem.Utils.Maintenance;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

public class MaintenanceManager
implements Listener {
    private boolean maintenanceMode;
    private final List<UUID> bypassPlayers = new ArrayList<UUID>();
    private final JavaPlugin plugin;
    private final String kickMessage = "\u00a7c\u00a7lServer is in maintenance mode!\n\u00a77We'll be back soon.";

    public MaintenanceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.maintenanceMode = UtilsConfig.getMaintenanceConfig().getBoolean("enabled", false);
        List<String> bypassPlayerUUIDs = UtilsConfig.getMaintenanceConfig().getStringList("bypass-players");
        for (String uuidStr : bypassPlayerUUIDs) {
            try {
                this.bypassPlayers.add(UUID.fromString(uuidStr));
            }
            catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in maintenance bypass list: " + uuidStr);
            }
        }
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    public void setMaintenanceMode(boolean mode) {
        this.maintenanceMode = mode;
        UtilsConfig.getMaintenanceConfig().set("enabled", (Object)mode);
        UtilsConfig.saveMaintenanceConfig();
        if (mode) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (this.canBypass(player)) continue;
                player.kickPlayer("\u00a7c\u00a7lServer is in maintenance mode!\n\u00a77We'll be back soon.");
            }
        }
    }

    public boolean canBypass(Player player) {
        return player.hasPermission("utils.maintenance.bypass") || this.bypassPlayers.contains(player.getUniqueId());
    }

    public void addBypassPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.bypassPlayers.contains(uuid)) {
            this.bypassPlayers.add(uuid);
            this.saveBypassPlayers();
        }
    }

    public void removeBypassPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.bypassPlayers.contains(uuid)) {
            this.bypassPlayers.remove(uuid);
            this.saveBypassPlayers();
        }
    }

    private void saveBypassPlayers() {
        ArrayList<String> uuidStrings = new ArrayList<String>();
        for (UUID uuid : this.bypassPlayers) {
            uuidStrings.add(uuid.toString());
        }
        UtilsConfig.getMaintenanceConfig().set("bypass-players", uuidStrings);
        UtilsConfig.saveMaintenanceConfig();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (this.maintenanceMode && !this.canBypass(player)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\u00a7c\u00a7lServer is in maintenance mode!\n\u00a77We'll be back soon.");
        }
    }
}

