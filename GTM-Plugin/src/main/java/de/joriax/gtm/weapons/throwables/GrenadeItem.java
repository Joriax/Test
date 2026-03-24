package de.joriax.gtm.weapons.throwables;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GrenadeItem {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Grenade");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Right-click to throw",
                    ChatColor.DARK_GRAY + "Item: grenade"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isGrenade(ItemStack item) {
        if (item == null || item.getType() != Material.FIRE_CHARGE) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        return meta.getDisplayName().equals(ChatColor.RED + "Grenade");
    }
}
