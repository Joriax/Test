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

public class Smoke {
    public static ItemStack createSmokeGrenade() {
        ItemStack item = new ItemStack(Material.STONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.valueOf(ChatColor.GRAY) + "Smoke Granate");
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um die Smoke Granate zu werfen.", String.valueOf(ChatColor.GRAY) + "Erzeugt eine Rauchwolke in einem 7x7x5-Bereich."));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isSmokeGrenade(ItemStack item) {
        if (item == null || item.getType() != Material.STONE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(String.valueOf(ChatColor.GRAY) + "Smoke Granate") && meta.hasLore() && meta.getLore().equals(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um die Smoke Granate zu werfen.", String.valueOf(ChatColor.GRAY) + "Erzeugt eine Rauchwolke in einem 7x7x5-Bereich."));
    }
}

