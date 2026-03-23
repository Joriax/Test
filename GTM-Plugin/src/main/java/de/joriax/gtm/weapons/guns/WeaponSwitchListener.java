package de.joriax.gtm.weapons.guns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponSwitchListener implements Listener {

    private final WeaponManager weaponManager;

    public WeaponSwitchListener(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Cancel reload if switching weapons
        if (weaponManager.isReloading(player.getUniqueId())) {
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
            ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());

            Weapon oldWeapon = weaponManager.getWeaponFromItem(oldItem);
            Weapon newWeapon = weaponManager.getWeaponFromItem(newItem);

            // Cancel reload if switching to a different weapon or non-weapon
            if (oldWeapon != null && (newWeapon == null || !newWeapon.getName().equals(oldWeapon.getName()))) {
                weaponManager.setReloading(player.getUniqueId(), false);
                player.sendMessage(org.bukkit.ChatColor.RED + "Reload cancelled.");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        weaponManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
