/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package Melee;

import Melee.MeleeWeapon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MeleeWeaponManager {
    private static Map<String, MeleeWeapon> melees = new ConcurrentHashMap<String, MeleeWeapon>();

    public static void registerMeleeWeapons() {
        MeleeWeapon hammer = new MeleeWeapon("Hammer", Material.WOODEN_AXE, 60);
        MeleeWeaponManager.addMeele(hammer);
        MeleeWeapon crowbar = new MeleeWeapon("Crowbar", Material.IRON_AXE, 40);
        MeleeWeaponManager.addMeele(crowbar);
    }

    public static void addMeele(MeleeWeapon melee) {
        melees.put(melee.getName().toLowerCase(), melee);
    }

    public static MeleeWeapon getMeleeWeaponName(String name) {
        return melees.get(name.toLowerCase());
    }

    public static Map<String, MeleeWeapon> getMeleeWeapons() {
        return melees;
    }

    public static List<ItemStack> getRandomMeleeWeaponsWithProbability() {
        ArrayList<MeleeWeapon> meleeList = new ArrayList<MeleeWeapon>(melees.values());
        int totalWeight = 0;
        for (MeleeWeapon melee : meleeList) {
            totalWeight += melee.getProbability();
        }
        ArrayList<ItemStack> selectedMeleeWeapons = new ArrayList<ItemStack>();
        block1: for (int i = 0; i < 5; ++i) {
            int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
            int currentWeight = 0;
            for (MeleeWeapon melee : meleeList) {
                if (randomValue >= (currentWeight += melee.getProbability())) continue;
                selectedMeleeWeapons.add(melee.getMeleeWeaponItem());
                continue block1;
            }
        }
        return selectedMeleeWeapons;
    }
}

