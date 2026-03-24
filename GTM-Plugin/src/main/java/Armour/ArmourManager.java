/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 */
package Armour;

import Armour.Armour;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArmourManager {
    private static Map<String, Armour> Armours = new ConcurrentHashMap<String, Armour>();

    public static void registerArmour() {
        Armour titanium = new Armour("Titainium vest", Material.DIAMOND_CHESTPLATE, 100, 500, 1000);
        titanium.setDisplayName(String.valueOf(ChatColor.AQUA) + "Titanium vest");
        ArmourManager.addArmour(titanium);
        Armour kevler = new Armour("Kevler vest", Material.CHAINMAIL_CHESTPLATE, 100, 250, 500);
        kevler.setDisplayName(String.valueOf(ChatColor.GOLD) + "Kevler vest");
        ArmourManager.addArmour(kevler);
        Armour AliensBoots = new Armour("Jumpos", Material.LEATHER_BOOTS, 100, 10000, 0);
        AliensBoots.setDisplayName(String.valueOf(ChatColor.GOLD) + "Jumpos");
        AliensBoots.addEnchantment(Enchantment.PROTECTION, 2);
        ArmourManager.addArmour(AliensBoots);
        Armour AlienUnderwear = new Armour("Wearies", Material.CHAINMAIL_LEGGINGS, 100, 10000, 0);
        AlienUnderwear.setDisplayName(String.valueOf(ChatColor.GOLD) + "Wearies");
        AlienUnderwear.addEnchantment(Enchantment.PROTECTION, 1);
        AlienUnderwear.addEnchantment(Enchantment.FIRE_PROTECTION, 2);
        ArmourManager.addArmour(AlienUnderwear);
    }

    public static void addArmour(Armour armour) {
        Armours.put(armour.getName().toLowerCase(), armour);
    }

    public Armour getArmourByname(String name) {
        return Armours.get(name.toLowerCase());
    }

    public static Map<String, Armour> getArmours() {
        return Armours;
    }

    public static List<ItemStack> getRandomArmoursWithProbability() {
        ArrayList<Armour> armouslist = new ArrayList<Armour>(Armours.values());
        int totalWeight = 0;
        for (Armour armour : armouslist) {
            totalWeight += armour.getProbability();
        }
        ArrayList<ItemStack> selectedArmour = new ArrayList<ItemStack>();
        block1: for (int i = 0; i < 5; ++i) {
            int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
            int currentWeight = 0;
            for (Armour armour : armouslist) {
                if (randomValue >= (currentWeight += armour.getProbability())) continue;
                selectedArmour.add(armour.getArmourItem());
                continue block1;
            }
        }
        return selectedArmour;
    }
}

