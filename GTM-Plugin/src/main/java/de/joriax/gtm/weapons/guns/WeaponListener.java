package de.joriax.gtm.weapons.guns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponListener implements Listener {

    private final WeaponManager weaponManager;

    public WeaponListener(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        Weapon weapon = weaponManager.getWeaponFromItem(held);
        if (weapon == null) return;

        event.setCancelled(true);

        // Check fire cooldown
        if (weaponManager.isOnCooldown(player.getUniqueId(), weapon.getFireDelay())) {
            return;
        }

        // Check if reloading
        if (weaponManager.isReloading(player.getUniqueId())) {
            return;
        }

        weaponManager.updateFireCooldown(player.getUniqueId());
        weapon.shoot(player, weaponManager);
    }
}
