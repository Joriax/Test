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
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.plugin.Plugin
 */
package Regeln;

import at.Poriax.gTM.GTM;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

public class Vanish
implements Listener,
CommandExecutor {
    private final GTM plugin;
    private final Set<UUID> vanishedPlayers = new HashSet<UUID>();
    private final String prefix = String.valueOf(ChatColor.GRAY) + "[" + String.valueOf(ChatColor.AQUA) + "Vanish" + String.valueOf(ChatColor.GRAY) + "] ";

    public Vanish(GTM plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Dieser Befehl kann nur von Spielern ausgef\u00fchrt werden!");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("simplevanish.use")) {
            player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Du hast keine Berechtigung, diesen Befehl zu nutzen!");
            return true;
        }
        if (command.getName().equalsIgnoreCase("vanish")) {
            if (this.isVanished(player)) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Du bist bereits im Vanish-Modus!");
            } else {
                this.hidePlayer(player);
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("unvanish")) {
            if (!this.isVanished(player)) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Du bist nicht im Vanish-Modus!");
            } else {
                this.showPlayer(player);
            }
            return true;
        }
        return false;
    }

    public void hidePlayer(Player player) {
        this.vanishedPlayers.add(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("simplevanish.see") || onlinePlayer.equals((Object)player)) continue;
            onlinePlayer.hidePlayer((Plugin)this.plugin, player);
        }
        player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Du bist jetzt im Vanish-Modus!");
    }

    public void showPlayer(Player player) {
        this.vanishedPlayers.remove(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer((Plugin)this.plugin, player);
        }
        player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Du bist jetzt wieder sichtbar!");
    }

    public void showAllPlayers() {
        for (UUID uuid : new HashSet<UUID>(this.vanishedPlayers)) {
            Player player = Bukkit.getPlayer((UUID)uuid);
            if (player == null || !player.isOnline()) continue;
            this.showPlayer(player);
        }
        this.vanishedPlayers.clear();
    }

    public boolean isVanished(Player player) {
        return this.vanishedPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        if (!joiningPlayer.hasPermission("simplevanish.see")) {
            for (UUID uuid : this.vanishedPlayers) {
                Player vanishedPlayer = Bukkit.getPlayer((UUID)uuid);
                if (vanishedPlayer == null || !vanishedPlayer.isOnline()) continue;
                joiningPlayer.hidePlayer((Plugin)this.plugin, vanishedPlayer);
            }
        }
        if (this.vanishedPlayers.contains(joiningPlayer.getUniqueId())) {
            joiningPlayer.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Du bist im Vanish-Modus!");
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (this.isVanished(event.getPlayer()) && !event.getPlayer().hasPermission("simplevanish.pickup")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Player damaged;
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && this.isVanished(damaged = (Player)event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Player player;
        if (event.getTarget() instanceof Player && this.isVanished(player = (Player)event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player clicked;
        if (event.getRightClicked() instanceof Player && this.isVanished(clicked = (Player)event.getRightClicked())) {
            event.setCancelled(true);
        }
    }
}

