/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerItemHeldEvent
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package Guns;

import Guns.Weapon;
import Guns.WeaponManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeaponSwitchListener
implements Listener {
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            Weapon newWeapon = WeaponManager.getWeaponInHand(player);
            if (newWeapon != null && newWeapon.getSlownessLevel() > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, newWeapon.getSlownessLevel() - 1, false, false));
            }
        }
    }
}

