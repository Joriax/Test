package de.joriax.gtm.weapons.guns;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AmmoGUIListener implements Listener {

    private final WeaponManager weaponManager;
    private static final String GUI_TITLE = ChatColor.DARK_RED + "Ammo Inventory";
    private final Set<UUID> openAmmoGUI = new HashSet<>();

    public AmmoGUIListener(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        String title = event.getView().getTitle();
        if (!title.equals(GUI_TITLE)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = meta.getDisplayName();

        // Determine which ammo type was clicked
        for (AmmoType type : AmmoType.values()) {
            if (name.contains(type.getDisplayName())) {
                int current = weaponManager.getReserveAmmo(player.getUniqueId(), type);
                player.sendMessage(ChatColor.GOLD + type.getDisplayName() + ": " + ChatColor.YELLOW + current + " rounds");
                break;
            }
        }
    }

    public static String getGUITitle() {
        return GUI_TITLE;
    }
}
