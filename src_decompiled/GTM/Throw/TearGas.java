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
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.Vector
 */
package Throw;

import Throw.TearItem;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TearGas
implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && TearItem.isTearGas(item)) {
            event.setCancelled(true);
            long currentTime = System.currentTimeMillis();
            long lastThrowTime = this.cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (currentTime - lastThrowTime < 3000L) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Du musst noch warten, bevor du ein weiteres Tr\u00e4nengas werfen kannst!");
                return;
            }
            this.cooldowns.put(player.getUniqueId(), currentTime);
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().remove(item);
            }
            ItemStack tearGasToThrow = TearItem.createTearGas();
            tearGasToThrow.setAmount(1);
            Vector direction = player.getLocation().getDirection().multiply(1.5);
            final Item thrownItem = player.getWorld().dropItem(player.getLocation(), tearGasToThrow);
            thrownItem.setVelocity(direction);
            thrownItem.setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable(this){

                public void run() {
                    final Location center = thrownItem.getLocation();
                    new BukkitRunnable(){
                        int ticks = 0;

                        public void run() {
                            if (this.ticks >= 200) {
                                this.cancel();
                                return;
                            }
                            for (int i = 0; i < 50; ++i) {
                                double offsetX = (Math.random() - 0.5) * 5.0;
                                double offsetZ = (Math.random() - 0.5) * 5.0;
                                Location particleLocation = center.clone().add(offsetX, 0.25, offsetZ);
                                player.getWorld().spawnParticle(Particle.CLOUD, particleLocation, 1, 0.0, 0.0, 0.0, 0.05);
                            }
                            for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                                if (!(nearbyPlayer.getLocation().distance(center) <= 5.0)) continue;
                                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
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

