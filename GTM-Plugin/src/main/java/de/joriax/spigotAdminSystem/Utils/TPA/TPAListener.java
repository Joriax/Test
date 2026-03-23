/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TPAListener
implements Listener {
    private final TPAManager tpaManager;
    private final JavaPlugin plugin;
    private final Map<UUID, TeleportInfo> teleportDelayMap = new HashMap<UUID, TeleportInfo>();

    public TPAListener(TPAManager tpaManager, JavaPlugin plugin) {
        this.tpaManager = tpaManager;
        this.plugin = plugin;
    }

    public void acceptTPA(Player sender, Player target, int delay) {
        this.tpaManager.acceptTeleport(sender, target);
        Location startLocation = sender.getLocation();
        this.teleportDelayMap.put(sender.getUniqueId(), new TeleportInfo(sender, target, System.currentTimeMillis() + (long)delay * 1000L, startLocation));
        sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.YELLOW) + "Preparing teleport in " + delay + " seconds. Don't move!");
        target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + sender.getName() + " has accepted your teleport request.");
        Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            TeleportInfo info = this.teleportDelayMap.get(sender.getUniqueId());
            if (info != null && info.sender.isOnline() && info.target.isOnline()) {
                this.teleport(info.sender, info.target);
                this.teleportDelayMap.remove(sender.getUniqueId());
            }
        }, (long)delay * 20L);
    }

    private void teleport(Player sender, Player target) {
        sender.teleport(target.getLocation());
        sender.playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Teleport successful!");
        target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + sender.getName() + " has teleported to you.");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TeleportInfo info = this.teleportDelayMap.get(player.getUniqueId());
        if (info != null) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null || from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Teleport aborted because you moved.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                this.teleportDelayMap.remove(player.getUniqueId());
                this.tpaManager.clearRequests(player);
            }
        }
    }

    private static class TeleportInfo {
        Player sender;
        Player target;
        long endTime;
        Location startLocation;

        TeleportInfo(Player sender, Player target, long endTime, Location startLocation) {
            this.sender = sender;
            this.target = target;
            this.endTime = endTime;
            this.startLocation = startLocation;
        }
    }
}

