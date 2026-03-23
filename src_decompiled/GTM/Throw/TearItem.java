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

public class TearItem {
    public static ItemStack createTearGas() {
        ItemStack item = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.valueOf(ChatColor.GREEN) + "Tr\u00e4nengas");
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um das Tr\u00e4nengas zu werfen.", String.valueOf(ChatColor.GRAY) + "Macht Spieler in der N\u00e4he f\u00fcr 5 Sekunden blind."));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isTearGas(ItemStack item) {
        if (item == null || item.getType() != Material.GLASS_BOTTLE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(String.valueOf(ChatColor.GREEN) + "Tr\u00e4nengas") && meta.hasLore() && meta.getLore().equals(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Rechtsklick, um das Tr\u00e4nengas zu werfen.", String.valueOf(ChatColor.GRAY) + "Macht Spieler in der N\u00e4he f\u00fcr 5 Sekunden blind."));
    }
}

