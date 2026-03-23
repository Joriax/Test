/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Throw;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Moloitem {
    public static ItemStack createMolotovCocktail() {
        ItemStack item = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.valueOf(ChatColor.RED) + "Molotov Cocktail");
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um den Molotov Cocktail zu werfen.", String.valueOf(ChatColor.GRAY) + "Deckt einen 5x5-Bereich mit Feuer ab."));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isMolotovCocktail(ItemStack item) {
        if (item == null || item.getType() != Material.GLASS_BOTTLE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(String.valueOf(ChatColor.RED) + "Molotov Cocktail") && meta.hasLore() && meta.getLore().equals(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um den Molotov Cocktail zu werfen.", String.valueOf(ChatColor.GRAY) + "Deckt einen 5x5-Bereich mit Feuer ab."));
    }
}

