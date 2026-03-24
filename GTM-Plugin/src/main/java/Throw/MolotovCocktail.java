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
 */
package Throw;

import Gang.GangPlugin;
import Throw.Moloitem;
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

public class MolotovCocktail
implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap();
    private final GangPlugin gangPlugin;

    public MolotovCocktail(GangPlugin gangPlugin) {
        this.gangPlugin = gangPlugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player thrower = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && Moloitem.isMolotovCocktail(item)) {
            event.setCancelled(true);
            long currentTime = System.currentTimeMillis();
            long lastThrowTime = this.cooldowns.getOrDefault(thrower.getUniqueId(), 0L);
            if (currentTime - lastThrowTime < 3000L) {
                thrower.sendMessage(String.valueOf(ChatColor.RED) + "Du musst noch warten, bevor du einen weiteren Molotov Cocktail werfen kannst!");
                return;
            }
            this.cooldowns.put(thrower.getUniqueId(), currentTime);
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                thrower.getInventory().remove(item);
            }
            ItemStack molotovToThrow = Moloitem.createMolotovCocktail();
            molotovToThrow.setAmount(1);
            final Item thrownItem = thrower.getWorld().dropItem(thrower.getLocation(), molotovToThrow);
            thrownItem.setVelocity(thrower.getLocation().getDirection().multiply(1.5));
            thrownItem.setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable(){

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
                                thrower.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0.0, 0.0, 0.0, 0.05);
                            }
                            for (Player nearbyPlayer : thrower.getWorld().getPlayers()) {
                                if (!(nearbyPlayer.getLocation().distance(center) <= 5.0) || !nearbyPlayer.equals((Object)thrower) && MolotovCocktail.this.isInSameGang(thrower, nearbyPlayer)) continue;
                                nearbyPlayer.setFireTicks(100);
                            }
                            ++this.ticks;
                        }
                    }.runTaskTimer((Plugin)GTM.getInstance(), 0L, 1L);
                    thrownItem.remove();
                }
            }.runTaskLater((Plugin)GTM.getInstance(), 60L);
        }
    }

    private boolean isInSameGang(Player player1, Player player2) {
        if (this.gangPlugin == null) {
            return false;
        }
        UUID leader1 = this.gangPlugin.findGangLeader(player1.getUniqueId());
        UUID leader2 = this.gangPlugin.findGangLeader(player2.getUniqueId());
        return leader1 != null && leader2 != null && leader1.equals(leader2);
    }
}

