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

public class GrenadeItem {
    public static ItemStack createThrowableItem() {
        ItemStack item = new ItemStack(Material.HAY_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.valueOf(ChatColor.RED) + "Granate");
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um die Granate zu werfen.", String.valueOf(ChatColor.GRAY) + "Explodiert nach 3 Sekunden."));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isThrowableItem(ItemStack item) {
        if (item == null || item.getType() != Material.HAY_BLOCK || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(String.valueOf(ChatColor.RED) + "Granate") && meta.hasLore() && meta.getLore().equals(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um die Granate zu werfen.", String.valueOf(ChatColor.GRAY) + "Explodiert nach 3 Sekunden."));
    }
}

