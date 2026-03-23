/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Particle
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.Vector
 */
package Throw;

import Throw.Smoke;
import at.Poriax.gTM.GTM;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SmokeGrenade
implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && Smoke.isSmokeGrenade(item)) {
            event.setCancelled(true);
            long currentTime = System.currentTimeMillis();
            long lastThrowTime = this.cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (currentTime - lastThrowTime < 3000L) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Du musst noch warten, bevor du eine weitere Smoke Granate werfen kannst!");
                return;
            }
            this.cooldowns.put(player.getUniqueId(), currentTime);
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().remove(item);
            }
            ItemStack smokeGrenadeToThrow = Smoke.createSmokeGrenade();
            smokeGrenadeToThrow.setAmount(1);
            Vector direction = player.getLocation().getDirection().multiply(1.5);
            final Item thrownItem = player.getWorld().dropItem(player.getLocation(), smokeGrenadeToThrow);
            thrownItem.setVelocity(direction);
            thrownItem.setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable(this){

                public void run() {
                    final Location center = thrownItem.getLocation();
                    new BukkitRunnable(){
                        int ticks = 0;

                        public void run() {
                            if (this.ticks >= 60) {
                                this.cancel();
                                return;
                            }
                            double radius = 5.0;
                            for (double theta = 0.0; theta <= Math.PI; theta += 0.19634954084936207) {
                                for (double phi = 0.0; phi <= Math.PI * 2; phi += 0.19634954084936207) {
                                    double x = radius * Math.sin(theta) * Math.cos(phi);
                                    double y = radius * Math.cos(theta);
                                    double z = radius * Math.sin(theta) * Math.sin(phi);
                                    Location surfaceLocation = center.clone().add(x, y, z);
                                    player.getWorld().spawnParticle(Particle.CLOUD, surfaceLocation, 1, 0.0, 0.0, 0.0, 0.05);
                                    for (double r = 0.0; r < radius; r += 0.5) {
                                        double innerX = r * Math.sin(theta) * Math.cos(phi);
                                        double innerY = r * Math.cos(theta);
                                        double innerZ = r * Math.sin(theta) * Math.sin(phi);
                                        Location innerLocation = center.clone().add(innerX, innerY, innerZ);
                                        player.getWorld().spawnParticle(Particle.CLOUD, innerLocation, 1, 0.0, 0.0, 0.0, 0.05);
                                    }
                                }
                            }
                            ++this.ticks;
                        }
                    }.runTaskTimer((Plugin)GTM.getInstance(), 0L, 1L);
                    thrownItem.remove();
                }
            }.runTaskLater((Plugin)GTM.getInstance(), 60L);
        }
    }
}

