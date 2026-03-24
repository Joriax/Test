/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Melee;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MeleeWeapon {
    private String name;
    private Material material;
    private int probability;

    public MeleeWeapon(String name, Material material, int probability) {
        this.name = name;
        this.material = material;
        this.probability = probability;
    }

    private String getDisplayName() {
        return String.valueOf(ChatColor.GOLD) + this.name + String.valueOf(ChatColor.BOLD);
    }

    public ItemStack getMeleeWeaponItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.getDisplayName());
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        item.setItemMeta(meta);
        return item;
    }

    public String getName() {
        return this.name;
    }

    public int getProbability() {
        return this.probability;
    }
}

