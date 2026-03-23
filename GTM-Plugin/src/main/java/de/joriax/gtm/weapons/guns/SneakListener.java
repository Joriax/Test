package de.joriax.gtm.weapons.guns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class SneakListener implements Listener {

    private final WeaponManager weaponManager;

    public SneakListener(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        Weapon weapon = weaponManager.getWeaponFromItem(held);
        if (weapon == null) return;

        // Trigger reload on sneak
        weapon.reload(player, weaponManager);
    }
}
