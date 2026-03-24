/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Armour;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Armour {
    private String name;
    private Material material;
    private int probability;
    private String displayName;
    private Map<Enchantment, Integer> enchantments;
    private int priceSell;
    private int priceBuy;

    public Armour(String name, Material material, int probability, int priceSell, int priceBuy) {
        this.name = name;
        this.material = material;
        this.probability = probability;
        this.displayName = String.valueOf(ChatColor.GOLD) + name + String.valueOf(ChatColor.BOLD);
        this.enchantments = new HashMap<Enchantment, Integer>();
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
    }

    public int getPriceSell() {
        return this.priceSell;
    }

    public void setPriceSell(int priceSell) {
        this.priceSell = priceSell;
    }

    public int getPriceBuy() {
        return this.priceBuy;
    }

    public void setPriceBuy(int priceBuy) {
        this.priceBuy = priceBuy;
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getProbability() {
        return this.probability;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    public ItemStack getArmourItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.getDisplayName());
        for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            meta.addEnchant(entry.getKey(), entry.getValue().intValue(), true);
        }
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
        item.setItemMeta(meta);
        return item;
    }
}

