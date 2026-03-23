package de.joriax.gtm.weapons.throwables;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Moloitem {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Molotov Cocktail");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Right-click to throw",
                    ChatColor.DARK_GRAY + "Item: molotov"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isMolotov(ItemStack item) {
        if (item == null || item.getType() != Material.GLASS_BOTTLE) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        return meta.getDisplayName().equals(ChatColor.GOLD + "Molotov Cocktail");
    }
}
