/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Dealer;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Ware {
    private final String name;
    private final Material material;
    private final String displayName;
    private final List<String> guiLore;
    private final List<String> itemLore;
    private final int customModelData;
    private final int amount;

    public Ware(String name, Material material, String displayName, List<String> guiLore, List<String> itemLore, int customModelData, int amount) {
        this.name = name;
        this.material = material;
        this.displayName = displayName;
        this.guiLore = guiLore;
        this.itemLore = itemLore;
        this.customModelData = customModelData;
        this.amount = amount;
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<String> getGUILore() {
        return this.guiLore;
    }

    public List<String> getItemLore() {
        return this.itemLore;
    }

    public int getCustomModelData() {
        return this.customModelData;
    }

    public int getAmount() {
        return this.amount;
    }

    public ItemStack createItemForGUI() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.displayName);
            meta.setLore(this.guiLore);
            meta.setCustomModelData(Integer.valueOf(this.customModelData));
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createItemForPlayer() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("\u00a76Weapon Box");
            meta.setLore(this.itemLore);
            meta.setCustomModelData(Integer.valueOf(this.customModelData));
            item.setItemMeta(meta);
        }
        return item;
    }
}

