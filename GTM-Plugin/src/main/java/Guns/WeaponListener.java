/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Camel
 *  org.bukkit.entity.Horse
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package Guns;

import Guns.Weapon;
import Guns.WeaponManager;
import at.Poriax.gTM.GTM;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeaponListener
implements Listener {
    private static final HashMap<Player, Long> lastShotTimes = new HashMap();
    private static final HashMap<Player, BukkitRunnable> activeShootTasks = new HashMap();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemMeta meta;
        final Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta() && (meta = item.getItemMeta()) != null && meta.hasDisplayName()) {
            String weaponName = meta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", "");
            final Weapon weapon = WeaponManager.getWeaponByName(weaponName);
            if ((player.getVehicle() instanceof Horse || player.getVehicle() instanceof Camel) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "You can't shoot while Driving");
                event.setCancelled(true);
            } else if (weapon != null) {
                double timeBetweenShots;
                long lastShotTime;
                long currentTime;
                if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    weapon.reload(player);
                }
                if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && weapon.getClepleft() > 0 && (double)((currentTime = System.currentTimeMillis()) - (lastShotTime = lastShotTimes.getOrDefault(player, 0L).longValue())) >= (timeBetweenShots = 1000.0 / weapon.getFireRate())) {
                    lastShotTimes.put(player, currentTime);
                    final int extraShots = Math.max(0, (int)Math.floor(weapon.getFireRate()) - 1);
                    long shotDelay = extraShots == 0 ? 0L : (extraShots == 1 ? 4L : (extraShots == 2 ? 3L : 1L));
                    BukkitRunnable shootTask = new BukkitRunnable(){
                        int shotsFired = 0;
                        int totalShots = extraShots + 1;

                        public void run() {
                            if (this.shotsFired < this.totalShots && weapon.getClepleft() > 0) {
                                weapon.shoot(player);
                                ++this.shotsFired;
                            } else {
                                this.cancel();
                                activeShootTasks.remove(player);
                            }
                        }
                    };
                    WeaponListener.addShootTask(player, shootTask);
                    shootTask.runTaskTimer((Plugin)GTM.getInstance(), 0L, shotDelay);
                }
            }
        }
    }

    private static void cancelShootTask(Player player) {
        if (activeShootTasks.containsKey(player)) {
            activeShootTasks.get(player).cancel();
            activeShootTasks.remove(player);
        }
    }

    private static void addShootTask(Player player, BukkitRunnable task) {
        WeaponListener.cancelShootTask(player);
        activeShootTasks.put(player, task);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        String weaponName;
        Weapon weapon;
        ItemMeta meta;
        Player player = event.getPlayer();
        ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());
        if (previousItem != null && previousItem.hasItemMeta() && (meta = previousItem.getItemMeta()) != null && meta.hasDisplayName() && (weapon = WeaponManager.getWeaponByName(weaponName = meta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", ""))) != null && weapon.isReloading()) {
            weapon.cancelReload();
            player.sendMessage(String.valueOf(ChatColor.YELLOW) + "Nachladen abgebrochen, da du die Waffe gewechselt hast!");
        }
        if (activeShootTasks.containsKey(player)) {
            activeShootTasks.get(player).cancel();
            activeShootTasks.remove(player);
        }
    }

    public static HashMap<Player, Long> getLastShotTimes() {
        return lastShotTimes;
    }
}

