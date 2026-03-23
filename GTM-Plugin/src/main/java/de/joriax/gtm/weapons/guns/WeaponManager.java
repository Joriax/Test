package de.joriax.gtm.weapons.guns;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.Material;

import java.util.*;

public class WeaponManager {

    private final GTMPlugin plugin;
    private final Map<String, Weapon> weapons = new LinkedHashMap<>();

    // Player ammo data: UUID -> (weaponName -> magazineAmmo)
    private final Map<UUID, Map<String, Integer>> magazineAmmo = new HashMap<>();
    // Player reserve ammo: UUID -> (ammoType -> amount)
    private final Map<UUID, Map<AmmoType, Integer>> reserveAmmo = new HashMap<>();
    // Reload state
    private final Set<UUID> reloading = new HashSet<>();
    // Fire cooldown: UUID -> lastFireTick
    private final Map<UUID, Long> fireCooldown = new HashMap<>();

    public WeaponManager(GTMPlugin plugin) {
        this.plugin = plugin;
        registerDefaultWeapons();
    }

    private void registerDefaultWeapons() {
        // Pistol
        registerWeapon(new Weapon(
                "pistol", "Pistol",
                AmmoType.PISTOL, 12, 4.0, 30, 40, 5,
                new SprayPattern(0.05), new RecoilHandler(1.0),
                Material.IRON_SWORD, 1001
        ));

        // SMG
        registerWeapon(new Weapon(
                "smg", "SMG",
                AmmoType.SMG, 30, 3.0, 25, 60, 2,
                new SprayPattern(0.1), new RecoilHandler(0.5),
                Material.IRON_SWORD, 1002
        ));

        // AK-47
        registerWeapon(new Weapon(
                "ak47", "AK-47",
                AmmoType.AR, 30, 6.0, 40, 60, 3,
                new SprayPattern(0.08), new RecoilHandler(1.5),
                Material.IRON_SWORD, 1003
        ));

        // M4A4
        registerWeapon(new Weapon(
                "m4a4", "M4A4",
                AmmoType.AR, 30, 5.5, 40, 55, 3,
                new SprayPattern(0.07), new RecoilHandler(1.2),
                Material.IRON_SWORD, 1004
        ));

        // Sniper
        registerWeapon(new Weapon(
                "sniper", "Sniper Rifle",
                AmmoType.SNIPER, 5, 15.0, 100, 80, 20,
                new SprayPattern(0.01), new RecoilHandler(3.0),
                Material.IRON_SWORD, 1005
        ));

        // Shotgun
        registerWeapon(new Weapon(
                "shotgun", "Shotgun",
                AmmoType.PISTOL, 8, 5.0, 15, 50, 15,
                new SprayPattern(0.2), new RecoilHandler(2.0),
                Material.IRON_SWORD, 1006
        ));
    }

    public void registerWeapon(Weapon weapon) {
        weapons.put(weapon.getName(), weapon);
    }

    public Weapon getWeapon(String name) {
        return weapons.get(name.toLowerCase());
    }

    public Collection<Weapon> getWeapons() {
        return weapons.values();
    }

    public Weapon getWeaponFromItem(org.bukkit.inventory.ItemStack item) {
        if (item == null) return null;
        for (Weapon weapon : weapons.values()) {
            if (weapon.isWeaponItem(item)) return weapon;
        }
        return null;
    }

    // ===== MAGAZINE AMMO =====

    public int getMagazineAmmo(UUID uuid, String weaponName) {
        return magazineAmmo.getOrDefault(uuid, Collections.emptyMap())
                .getOrDefault(weaponName, 0);
    }

    public void setMagazineAmmo(UUID uuid, String weaponName, int amount) {
        magazineAmmo.computeIfAbsent(uuid, k -> new HashMap<>()).put(weaponName, Math.max(0, amount));
    }

    // ===== RESERVE AMMO =====

    public int getReserveAmmo(UUID uuid, AmmoType type) {
        return reserveAmmo.getOrDefault(uuid, Collections.emptyMap())
                .getOrDefault(type, 0);
    }

    public void setReserveAmmo(UUID uuid, AmmoType type, int amount) {
        reserveAmmo.computeIfAbsent(uuid, k -> new HashMap<>()).put(type, Math.max(0, amount));
    }

    public void addReserveAmmo(UUID uuid, AmmoType type, int amount) {
        int current = getReserveAmmo(uuid, type);
        setReserveAmmo(uuid, type, current + amount);
    }

    // ===== RELOAD STATE =====

    public boolean isReloading(UUID uuid) {
        return reloading.contains(uuid);
    }

    public void setReloading(UUID uuid, boolean state) {
        if (state) reloading.add(uuid);
        else reloading.remove(uuid);
    }

    // ===== FIRE COOLDOWN =====

    public boolean isOnCooldown(UUID uuid, int fireDelayTicks) {
        long lastFire = fireCooldown.getOrDefault(uuid, 0L);
        long currentTime = System.currentTimeMillis();
        long delayMs = (long)(fireDelayTicks * 50); // 1 tick = 50ms
        return (currentTime - lastFire) < delayMs;
    }

    public void updateFireCooldown(UUID uuid) {
        fireCooldown.put(uuid, System.currentTimeMillis());
    }

    public void removePlayer(UUID uuid) {
        magazineAmmo.remove(uuid);
        reserveAmmo.remove(uuid);
        reloading.remove(uuid);
        fireCooldown.remove(uuid);
    }
}
