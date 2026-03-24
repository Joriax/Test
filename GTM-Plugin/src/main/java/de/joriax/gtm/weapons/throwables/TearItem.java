package de.joriax.gtm.weapons.throwables;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TearItem {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.POTION);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + "Tear Gas");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Right-click to throw",
                    ChatColor.DARK_GRAY + "Item: teargas"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isTearGas(ItemStack item) {
        if (item == null || item.getType() != Material.POTION) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        return meta.getDisplayName().equals(ChatColor.WHITE + "Tear Gas");
    }
}
