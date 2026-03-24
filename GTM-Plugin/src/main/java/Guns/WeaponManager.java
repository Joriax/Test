/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Guns;

import Guns.AmmoType;
import Guns.Weapon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WeaponManager {
    private static final Map<String, Weapon> weapons = new ConcurrentHashMap<String, Weapon>();
    private static final Map<UUID, Map<AmmoType, Integer>> playerAmmo = new ConcurrentHashMap<UUID, Map<AmmoType, Integer>>();
    private static File ammoFile;
    private static FileConfiguration ammoConfig;

    public static void init(File dataFolder) {
        ammoFile = new File(dataFolder, "ammo.yml");
        ammoConfig = YamlConfiguration.loadConfiguration((File)ammoFile);
        WeaponManager.loadAmmoData();
    }

    public static void registerWeapons() {
        ArrayList<String> ak47Lore = new ArrayList<String>();
        ak47Lore.add("Eine m\u00e4chtige Maschinengewehr-Waffe.");
        ak47Lore.add("Schaden: 10");
        ak47Lore.add("Reichweite: 50 Bl\u00f6cke");
        ak47Lore.add("Feuerrate: Schnell");
        Particle ak47Particle = Particle.ENCHANT;
        int ak47Count = 1;
        double ak47Speed = 0.1;
        double ak47Density = 0.1;
        List<Integer> ak47ShotPattern = Arrays.asList(0, 1);
        Weapon ak47 = new Weapon("AK-47", Material.DIAMOND_SWORD, 30, 10.0, 50, 100.0, 3.0, ak47Lore, AmmoType.AR, ak47Particle, ak47Count, ak47Speed, ak47Density, 3, 25, ak47ShotPattern, 5000, 10000, Sound.BLOCK_ANVIL_BREAK, Sound.ENTITY_IRON_GOLEM_ATTACK, 0.03, 0.01, 1, 0.0);
        WeaponManager.addWeapon(ak47);
        Weapon smg = new Weapon("SMG", Material.DIAMOND_SWORD, 25, 3.0, 50, 100.0, 3.0, ak47Lore, AmmoType.SMG, ak47Particle, ak47Count, ak47Speed, ak47Density, 3, 25, ak47ShotPattern, 5000, 10000, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, Sound.ENTITY_IRON_GOLEM_ATTACK, 0.0, 0.025, 1, 0.0);
        WeaponManager.addWeapon(smg);
        Weapon mini = new Weapon("mini", Material.DIAMOND_SWORD, 300, 2.5, 50, 100.0, 6.0, ak47Lore, AmmoType.SMG, ak47Particle, ak47Count, ak47Speed, ak47Density, 3, 25, ak47ShotPattern, 5000, 10000, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.ENTITY_ARMOR_STAND_BREAK, 0.025, 0.025, 1, 0.0);
        WeaponManager.addWeapon(mini);
        Weapon pistol = new Weapon("pistol", Material.DIAMOND_SWORD, 12, 2.5, 50, 100.0, 1.0, ak47Lore, AmmoType.PISTOL, ak47Particle, ak47Count, ak47Speed, ak47Density, 3, 25, ak47ShotPattern, 5000, 10000, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, Sound.ENTITY_ARMOR_STAND_BREAK, 0.0, 0.0, 1, 0.0);
        WeaponManager.addWeapon(pistol);
        Weapon pump = new Weapon("pump", Material.DIAMOND_SWORD, 12, 2.5, 50, 100.0, 1.0, ak47Lore, AmmoType.PISTOL, ak47Particle, ak47Count, ak47Speed, ak47Density, 3, 25, ak47ShotPattern, 5000, 10000, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, Sound.ENTITY_ARMOR_STAND_BREAK, 0.03, 2.0, 10, 0.1);
        WeaponManager.addWeapon(pump);
    }

    public static void addWeapon(Weapon weapon) {
        weapons.put(weapon.getName().toLowerCase(), weapon);
    }

    public static Weapon getWeaponByName(String name) {
        return weapons.get(name.toLowerCase());
    }

    public static int getAmmo(Player player, AmmoType ammoType) {
        return (int)((Map)playerAmmo.getOrDefault(player.getUniqueId(), new EnumMap(AmmoType.class))).getOrDefault((Object)ammoType, 0);
    }

    public static void decreaseAmmo(Player player, AmmoType ammoType, int amount) {
        Map ammoMap = playerAmmo.computeIfAbsent(player.getUniqueId(), k -> new EnumMap(AmmoType.class));
        int newAmount = (int)ammoMap.getOrDefault((Object)ammoType, 0) - amount;
        ammoMap.put(ammoType, newAmount);
        WeaponManager.saveAmmoData();
    }

    public static void addAmmo(Player player, AmmoType ammoType, int amount) {
        Map ammoMap = playerAmmo.computeIfAbsent(player.getUniqueId(), k -> new EnumMap(AmmoType.class));
        int newAmount = (int)ammoMap.getOrDefault((Object)ammoType, 0) + amount;
        ammoMap.put(ammoType, newAmount);
        WeaponManager.saveAmmoData();
    }

    public static Weapon getWeaponInHand(Player player) {
        ItemMeta meta;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta() && (meta = item.getItemMeta()) != null && meta.hasDisplayName()) {
            String weaponName = meta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", "");
            return WeaponManager.getWeaponByName(weaponName);
        }
        return null;
    }

    public static Map<String, Weapon> getWeapons() {
        return weapons;
    }

    public static Weapon getWeaponFromItemStack(ItemStack item) {
        ItemMeta meta;
        if (item != null && item.hasItemMeta() && (meta = item.getItemMeta()) != null && meta.hasDisplayName()) {
            String weaponName = meta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", "");
            return WeaponManager.getWeaponByName(weaponName);
        }
        return null;
    }

    public static void saveAmmoData() {
        for (UUID uuid : playerAmmo.keySet()) {
            Map<AmmoType, Integer> ammoMap = playerAmmo.get(uuid);
            for (AmmoType type : ammoMap.keySet()) {
                ammoConfig.set(uuid.toString() + "." + type.name(), (Object)ammoMap.get((Object)type));
            }
        }
        try {
            ammoConfig.save(ammoFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAmmoData() {
        if (!ammoFile.exists()) {
            return;
        }
        for (String uuidStr : ammoConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            EnumMap<AmmoType, Integer> ammoMap = new EnumMap<AmmoType, Integer>(AmmoType.class);
            for (AmmoType type : AmmoType.values()) {
                String path = uuidStr + "." + type.name();
                if (!ammoConfig.contains(path)) continue;
                ammoMap.put(type, ammoConfig.getInt(path));
            }
            playerAmmo.put(uuid, ammoMap);
        }
    }
}

