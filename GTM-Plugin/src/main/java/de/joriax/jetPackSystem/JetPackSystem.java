/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.levelSystem.LevelSystem
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerToggleFlightEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.persistence.PersistentDataType
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.Vector
 */
package de.joriax.jetPackSystem;

import de.joriax.levelSystem.LevelSystem;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class JetPackSystem
implements Listener,
CommandExecutor {
    private final JavaPlugin plugin;
    private final Set<Player> flyingPlayers = new HashSet<Player>();
    private final int fuelConsumptionRate = 1;
    private final NamespacedKey jetpackKey;
    private final int customMaxDurability = 500;
    private LevelSystem levelSystem;
    private final Map<UUID, Double> playerFallHeights = new HashMap<UUID, Double>();

    public JetPackSystem(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jetpackKey = new NamespacedKey((Plugin)plugin, "jetpack");
        this.startJetpackCheckTask();
    }

    public void onDisable() {
        this.flyingPlayers.clear();
        this.playerFallHeights.clear();
    }

    private void startJetpackCheckTask() {
        new BukkitRunnable(){

            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setAllowFlight(JetPackSystem.this.isJetpack(player.getInventory().getChestplate()));
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.isJetpack(player.getInventory().getChestplate())) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.flyingPlayers.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack chestplate = player.getInventory().getChestplate();
        if (!this.isJetpack(chestplate)) {
            player.setAllowFlight(false);
            return;
        }
        event.setCancelled(true);
        if (this.flyingPlayers.contains(player)) {
            this.stopJetpack(player);
        } else {
            this.startJetpack(player);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && this.isJetpack(player.getInventory().getChestplate()) && this.flyingPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    private void startJetpack(final Player player) {
        this.flyingPlayers.add(player);
        player.setFlying(true);
        new BukkitRunnable(this){
            private Location lastLocation;
            private int soundCooldown;
            private int fuelCooldown;
            private int durabilityCooldown;
            final /* synthetic */ JetPackSystem this$0;
            {
                this.this$0 = this$0;
                this.lastLocation = player.getLocation();
                this.soundCooldown = 0;
                this.fuelCooldown = 0;
                this.durabilityCooldown = 0;
            }

            public void run() {
                if (!this.this$0.flyingPlayers.contains(player) || !player.isOnline()) {
                    this.this$0.stopJetpack(player);
                    this.cancel();
                    return;
                }
                ItemStack chestplate = player.getInventory().getChestplate();
                if (!this.this$0.isJetpack(chestplate)) {
                    this.this$0.stopJetpack(player);
                    this.cancel();
                    return;
                }
                this.this$0.spawnParticles(player, this.lastLocation);
                if (this.soundCooldown <= 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);
                    this.soundCooldown = 4;
                } else {
                    --this.soundCooldown;
                }
                if (this.fuelCooldown <= 0) {
                    if (!this.this$0.consumeFuel(player)) {
                        this.this$0.stopJetpack(player);
                        this.cancel();
                        return;
                    }
                    this.fuelCooldown = 40;
                } else {
                    --this.fuelCooldown;
                }
                if (this.durabilityCooldown <= 0) {
                    if (!this.this$0.reduceDurability(player)) {
                        this.this$0.stopJetpack(player);
                        this.cancel();
                        return;
                    }
                    this.durabilityCooldown = 50;
                } else {
                    --this.durabilityCooldown;
                }
                this.lastLocation = player.getLocation();
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 1L);
    }

    private void spawnParticles(Player player, Location lastLocation) {
        Location currentLocation = player.getLocation();
        Vector direction = currentLocation.toVector().subtract(lastLocation.toVector()).normalize();
        double distance = lastLocation.distance(currentLocation);
        int particleCount = (int)(distance * 5.0);
        for (int i = 0; i < particleCount; ++i) {
            double ratio = (double)i / (double)particleCount;
            Location particleLoc = lastLocation.clone().add(direction.clone().multiply(ratio * distance));
            player.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0.0, 0.0, 0.0, 0.0);
            player.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void stopJetpack(Player player) {
        this.flyingPlayers.remove(player);
        player.setFlying(false);
        if (!this.isJetpack(player.getInventory().getChestplate())) {
            player.setAllowFlight(false);
        }
    }

    private boolean consumeFuel(Player player) {
        ItemStack fuel = new ItemStack(Material.CHARCOAL, 1);
        if (player.getInventory().containsAtLeast(fuel, 1)) {
            player.getInventory().removeItem(new ItemStack[]{fuel});
            return true;
        }
        player.sendMessage("\u00a7cDein Jetpack hat keinen Treibstoff mehr!");
        this.stopJetpack(player);
        return false;
    }

    private boolean reduceDurability(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || !this.isJetpack(chestplate)) {
            return false;
        }
        int newDurability = chestplate.getDurability() + 1;
        if (newDurability >= 499) {
            player.getInventory().setChestplate(null);
            player.getInventory().addItem(new ItemStack[]{chestplate});
            player.sendMessage("\u00a7cDein Jetpack ist abgenutzt!");
            this.stopJetpack(player);
            return false;
        }
        chestplate.setDurability((short)newDurability);
        return true;
    }

    private boolean isJetpack(ItemStack item) {
        if (item == null || item.getType() != Material.DIAMOND_CHESTPLATE) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(this.jetpackKey, PersistentDataType.BYTE);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cNur Spieler k\u00f6nnen diesen Befehl verwenden!");
            return false;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("jetpack.use")) {
            player.sendMessage("\u00a7cKeine Berechtigung!");
            return false;
        }
        ItemStack jetpack = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta meta = jetpack.getItemMeta();
        meta.setDisplayName("\u00a76Jetpack");
        meta.setLore(Collections.singletonList("\u00a77Fliegen mit Holzkohle"));
        meta.getPersistentDataContainer().set(this.jetpackKey, PersistentDataType.BYTE, (Object)1);
        jetpack.setItemMeta(meta);
        player.getInventory().addItem(new ItemStack[]{jetpack});
        player.sendMessage("\u00a7aJetpack erhalten!");
        return true;
    }
}

