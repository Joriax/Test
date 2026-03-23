package de.joriax.gtm.weapons.melee;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class MeleeWeapon {

    private final String name;
    private final String displayName;
    private final double damage;
    private final double attackSpeed;
    private final Material material;
    private final int customModelData;

    public MeleeWeapon(String name, String displayName, double damage, double attackSpeed,
                       Material material, int customModelData) {
        this.name = name;
        this.displayName = displayName;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.material = material;
        this.customModelData = customModelData;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + displayName);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Damage: " + ChatColor.RED + damage,
                    ChatColor.GRAY + "Type: " + ChatColor.YELLOW + "Melee",
                    ChatColor.DARK_GRAY + "Melee ID: " + name
            ));
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isMeleeItem(ItemStack item) {
        if (item == null || item.getType() != material) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        List<String> lore = meta.getLore();
        if (lore == null) return false;
        for (String line : lore) {
            if (line.contains("Melee ID: " + name)) return true;
        }
        return false;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public double getDamage() { return damage; }
    public double getAttackSpeed() { return attackSpeed; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
}
