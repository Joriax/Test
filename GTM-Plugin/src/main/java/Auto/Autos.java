/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.attribute.Attribute
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.Camel
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Horse
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package Auto;

import at.Poriax.gTM.GTM;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Autos
implements Listener {
    public static boolean spawnCar(Player player, final String name) {
        Location loc = player.getLocation();
        Entity car = name.equalsIgnoreCase("rocket") ? loc.getWorld().spawnEntity(loc, EntityType.CAMEL) : loc.getWorld().spawnEntity(loc, EntityType.HORSE);
        if (car instanceof Horse) {
            final Horse horse = (Horse)car;
            horse.setTamed(true);
            horse.setOwner((AnimalTamer)player);
            horse.setInvulnerable(true);
            horse.setInvisible(false);
            horse.setAI(false);
            horse.setSilent(true);
            horse.setJumpStrength(0.0);
            horse.setRemoveWhenFarAway(false);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.setCustomNameVisible(true);
            Autos.setStats(horse, name);
            new BukkitRunnable(){

                public void run() {
                    if (horse.isDead() || !horse.isValid()) {
                        this.cancel();
                        return;
                    }
                    double health = horse.getHealth();
                    double maxHealth = horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    String healthBar = Autos.generateHealthBar(health, maxHealth);
                    ChatColor color = ChatColor.GREEN;
                    double percent = health / maxHealth;
                    if (percent <= 0.25) {
                        color = ChatColor.RED;
                    } else if (percent <= 0.5) {
                        color = ChatColor.GOLD;
                    }
                    horse.setCustomName(String.valueOf(color) + name + " ||" + healthBar);
                }
            }.runTaskTimer((Plugin)GTM.getInstance(), 0L, 20L);
        } else if (car instanceof Camel) {
            final Camel camel = (Camel)car;
            camel.setTamed(true);
            camel.setOwner((AnimalTamer)player);
            camel.setInvulnerable(true);
            camel.setInvisible(false);
            camel.setAI(false);
            camel.setSilent(true);
            camel.setJumpStrength(0.0);
            camel.setRemoveWhenFarAway(false);
            camel.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            camel.setCustomNameVisible(true);
            camel.setGravity(false);
            Autos.setStats(camel, name);
            new BukkitRunnable(){

                public void run() {
                    if (camel.isDead() || !camel.isValid()) {
                        this.cancel();
                        return;
                    }
                    double health = camel.getHealth();
                    double maxHealth = camel.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    String healthBar = Autos.generateHealthBar(health, maxHealth);
                    ChatColor color = ChatColor.GREEN;
                    double percent = health / maxHealth;
                    if (percent <= 0.25) {
                        color = ChatColor.RED;
                    } else if (percent <= 0.5) {
                        color = ChatColor.GOLD;
                    }
                    camel.setCustomName(String.valueOf(color) + name + " ||" + healthBar);
                }
            }.runTaskTimer((Plugin)GTM.getInstance(), 0L, 20L);
        }
        return true;
    }

    private static void setStats(Horse horse, String name) {
        switch (name.toLowerCase()) {
            case "sport": {
                horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
                horse.setMaxHealth(300.0);
                horse.setHealth(300.0);
                break;
            }
            case "truck": {
                horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.22);
                horse.setMaxHealth(500.0);
                horse.setHealth(500.0);
                break;
            }
            case "bike": {
                horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
                horse.setMaxHealth(150.0);
                horse.setHealth(150.0);
                break;
            }
            case "rocket": {
                horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.75);
                horse.setMaxHealth(700.0);
                horse.setHealth(700.0);
                break;
            }
            default: {
                horse.remove();
            }
        }
    }

    private static void setStats(Camel camel, String name) {
        switch (name.toLowerCase()) {
            case "sport": {
                camel.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
                camel.setMaxHealth(300.0);
                camel.setHealth(300.0);
                break;
            }
            case "truck": {
                camel.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.22);
                camel.setMaxHealth(500.0);
                camel.setHealth(500.0);
                break;
            }
            case "bike": {
                camel.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
                camel.setMaxHealth(150.0);
                camel.setHealth(150.0);
                break;
            }
            case "rocket": {
                camel.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                camel.setMaxHealth(200.0);
                camel.setHealth(200.0);
                break;
            }
            default: {
                camel.remove();
            }
        }
    }

    private static String generateHealthBar(double health, double maxHealth) {
        int totalBars = 20;
        int filledBars = (int)(health / maxHealth * (double)totalBars);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < totalBars; ++i) {
            bar.append(i < filledBars ? "|" : " ");
        }
        return bar.toString();
    }

    @EventHandler
    public void onHorseDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Horse || event.getEntity() instanceof Camel) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }

    @EventHandler
    public void onOpenInventory(PlayerInteractEntityEvent event) {
        if ((event.getRightClicked() instanceof Horse || event.getRightClicked() instanceof Camel) && event.getPlayer().isSneaking()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Horse || event.getInventory().getHolder() instanceof Camel) {
            event.setCancelled(true);
        }
    }
}

