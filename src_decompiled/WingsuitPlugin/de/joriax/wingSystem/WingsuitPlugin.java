/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package de.joriax.wingSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class WingsuitPlugin
extends JavaPlugin
implements Listener,
CommandExecutor {
    private final double SINGLE_BOOST_STRENGTH = 0.65;
    private final double HOLD_BOOST_STRENGTH = 0.2;
    private final double MAX_SPEED = 1.6;
    private final String WINGSUIT_NAME = "\u00a7bWingsuit";
    private final String NO_FUEL_MESSAGE = "\u00a7cDu brauchst Holzkohle zum Boosten!";
    private final String WINGSUIT_PERMISSION = "wingsuit.give";
    private final int FUEL_CONSUMPTION_RATE = 10;
    private final double MIN_PITCH = -90.0;
    private final double MAX_PITCH = -10.0;
    private final HashSet<UUID> boostingPlayers = new HashSet();
    private final HashMap<UUID, Integer> boostCounts = new HashMap();

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getCommand("wing").setExecutor((CommandExecutor)this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wing")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("\u00a7cNur Spieler k\u00f6nnen diesen Befehl nutzen.");
                return true;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("wingsuit.give")) {
                player.sendMessage("\u00a7cKeine Berechtigung.");
                return true;
            }
            this.giveWingsuit(player);
            player.sendMessage("\u00a7aDu hast einen Wingsuit erhalten!");
            return true;
        }
        return false;
    }

    private void giveWingsuit(Player player) {
        ItemStack wingsuit = new ItemStack(Material.ELYTRA);
        ItemMeta meta = wingsuit.getItemMeta();
        meta.setDisplayName("\u00a7bWingsuit");
        meta.setUnbreakable(true);
        wingsuit.setItemMeta(meta);
        player.getInventory().addItem(new ItemStack[]{wingsuit});
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!this.isGlidingOrSwimming(player)) {
            return;
        }
        if (event.isSneaking()) {
            this.boostingPlayers.add(player.getUniqueId());
            this.boostPlayer(player, 0.65);
        } else {
            this.boostingPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.boostingPlayers.contains(player.getUniqueId())) {
            this.boostPlayer(player, 0.2);
        }
    }

    private void boostPlayer(Player player, double boostStrength) {
        Vector newVelocity;
        ItemStack elytra = player.getInventory().getChestplate();
        if (elytra == null || elytra.getType() != Material.ELYTRA) {
            return;
        }
        double pitch = player.getLocation().getPitch();
        if (pitch > -10.0 || pitch < -90.0) {
            return;
        }
        Vector direction = player.getLocation().getDirection().normalize().multiply(boostStrength);
        UUID playerId = player.getUniqueId();
        this.boostCounts.put(playerId, this.boostCounts.getOrDefault(playerId, 0) + 1);
        if (this.boostCounts.get(playerId) >= 10) {
            if (!this.consumeFuel(player)) {
                player.sendMessage("\u00a7cDu brauchst Holzkohle zum Boosten!");
                this.boostingPlayers.remove(playerId);
                return;
            }
            this.boostCounts.put(playerId, 0);
        }
        if ((newVelocity = player.getVelocity().add(direction)).length() > 1.6) {
            newVelocity = newVelocity.normalize().multiply(1.6);
        }
        player.setVelocity(newVelocity);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.5f, 0.5f);
        if (this.boostCounts.get(playerId) == 0) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
        }
    }

    private boolean consumeFuel(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.CHARCOAL) continue;
            item.setAmount(item.getAmount() - 1);
            return true;
        }
        return false;
    }

    private boolean isGlidingOrSwimming(Player player) {
        return player.isGliding() || player.isSwimming();
    }
}

