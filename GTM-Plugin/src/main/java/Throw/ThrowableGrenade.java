/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
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

import Throw.GrenadeItem;
import at.Poriax.gTM.GTM;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
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

public class ThrowableGrenade
implements Listener {
    private final HashMap<UUID, Long> cooldowns = new HashMap();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && GrenadeItem.isThrowableItem(item)) {
            event.setCancelled(true);
            long currentTime = System.currentTimeMillis();
            long lastThrowTime = this.cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (currentTime - lastThrowTime < 3000L) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Du musst noch warten, bevor du eine weitere Granate werfen kannst!");
                return;
            }
            this.cooldowns.put(player.getUniqueId(), currentTime);
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().remove(item);
            }
            ItemStack grenadeToThrow = GrenadeItem.createThrowableItem();
            grenadeToThrow.setAmount(1);
            Vector direction = player.getLocation().getDirection().multiply(1.5);
            final Item thrownItem = player.getWorld().dropItem(player.getLocation(), grenadeToThrow);
            thrownItem.setVelocity(direction);
            thrownItem.setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable(){

                public void run() {
                    thrownItem.getWorld().createExplosion(thrownItem.getLocation(), 4.0f, false, false);
                    thrownItem.remove();
                }
            }.runTaskLater((Plugin)GTM.getInstance(), 60L);
        }
    }
}

