package de.joriax.gtm.weapons.guns;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Weapon {

    private final String name;
    private final String displayName;
    private final AmmoType ammoType;
    private final int magazineSize;
    private final double damage;
    private final double range;
    private final int reloadTime;        // ticks
    private final int fireDelay;         // ticks between shots
    private final SprayPattern sprayPattern;
    private final RecoilHandler recoilHandler;
    private final Material material;
    private final int customModelData;

    private static final Random RANDOM = new Random();

    public Weapon(String name, String displayName, AmmoType ammoType, int magazineSize,
                  double damage, double range, int reloadTime, int fireDelay,
                  SprayPattern sprayPattern, RecoilHandler recoilHandler,
                  Material material, int customModelData) {
        this.name = name;
        this.displayName = displayName;
        this.ammoType = ammoType;
        this.magazineSize = magazineSize;
        this.damage = damage;
        this.range = range;
        this.reloadTime = reloadTime;
        this.fireDelay = fireDelay;
        this.sprayPattern = sprayPattern;
        this.recoilHandler = recoilHandler;
        this.material = material;
        this.customModelData = customModelData;
    }

    public ItemStack createItem(int ammo) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.WHITE + displayName);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Ammo: " + ChatColor.YELLOW + ammo + "/" + magazineSize,
                    ChatColor.GRAY + "Type: " + ChatColor.AQUA + ammoType.getDisplayName(),
                    ChatColor.GRAY + "Damage: " + ChatColor.RED + damage,
                    ChatColor.DARK_GRAY + "Weapon ID: " + name
            ));
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createItem() {
        return createItem(magazineSize);
    }

    /**
     * Shoot the weapon for the given player. Handles ammo decrement and bullet logic.
     * @return true if shot was fired, false if no ammo
     */
    public boolean shoot(Player shooter, WeaponManager weaponManager) {
        int currentAmmo = weaponManager.getMagazineAmmo(shooter.getUniqueId(), name);
        if (currentAmmo <= 0) {
            shooter.sendMessage(ChatColor.RED + "Out of ammo! Press Shift to reload.");
            shooter.playSound(shooter.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.0f);
            return false;
        }

        // Decrement ammo
        weaponManager.setMagazineAmmo(shooter.getUniqueId(), name, currentAmmo - 1);

        // Update item display
        updateAmmoDisplay(shooter, currentAmmo - 1);

        // Shoot raycast
        performShot(shooter);

        // Play sound
        shooter.getWorld().playSound(shooter.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8f, 1.5f);

        // Apply recoil
        if (recoilHandler != null) {
            recoilHandler.applyRecoil(shooter);
        }

        return true;
    }

    private void performShot(Player shooter) {
        Location start = shooter.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        // Apply spray
        if (sprayPattern != null) {
            double[] offset = sprayPattern.getOffset();
            direction.add(new Vector(offset[0], offset[1], offset[2]));
            direction.normalize();
        }

        // Raycast along direction
        for (double dist = 0; dist <= range; dist += 0.5) {
            Location checkLoc = start.clone().add(direction.clone().multiply(dist));

            // Spawn bullet particle
            if (dist % 2 == 0) {
                checkLoc.getWorld().spawnParticle(Particle.SMOKE, checkLoc, 1, 0, 0, 0, 0);
            }

            // Check for entities
            for (Entity entity : checkLoc.getWorld().getNearbyEntities(checkLoc, 0.5, 0.5, 0.5)) {
                if (entity.equals(shooter)) continue;
                if (entity instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entity;
                    target.damage(damage, shooter);

                    // Hit particle
                    checkLoc.getWorld().spawnParticle(Particle.CRIT, checkLoc, 5);
                    checkLoc.getWorld().playSound(checkLoc, Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);

                    if (target instanceof Player) {
                        shooter.sendMessage(ChatColor.RED + "Hit " + ((Player) target).getName() + "!");
                    }
                    return;
                }
            }

            // Check for blocks
            if (checkLoc.getBlock().getType() != Material.AIR &&
                    checkLoc.getBlock().getType() != Material.CAVE_AIR) {
                checkLoc.getWorld().spawnParticle(Particle.BLOCK, checkLoc, 5,
                        checkLoc.getBlock().getBlockData());
                return;
            }
        }
    }

    public void reload(Player player, WeaponManager weaponManager) {
        int currentAmmo = weaponManager.getMagazineAmmo(player.getUniqueId(), name);
        if (currentAmmo >= magazineSize) {
            player.sendMessage(ChatColor.YELLOW + "Magazine is already full!");
            return;
        }

        int reserveAmmo = weaponManager.getReserveAmmo(player.getUniqueId(), ammoType);
        if (reserveAmmo <= 0) {
            player.sendMessage(ChatColor.RED + "No reserve ammo left!");
            return;
        }

        if (weaponManager.isReloading(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Already reloading...");
            return;
        }

        weaponManager.setReloading(player.getUniqueId(), true);
        player.sendMessage(ChatColor.YELLOW + "Reloading " + displayName + "...");
        player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_START, 1.0f, 1.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    weaponManager.setReloading(player.getUniqueId(), false);
                    return;
                }

                int needed = magazineSize - currentAmmo;
                int available = weaponManager.getReserveAmmo(player.getUniqueId(), ammoType);
                int reloadAmount = Math.min(needed, available);

                weaponManager.setMagazineAmmo(player.getUniqueId(), name, currentAmmo + reloadAmount);
                weaponManager.setReserveAmmo(player.getUniqueId(), ammoType, available - reloadAmount);
                weaponManager.setReloading(player.getUniqueId(), false);

                updateAmmoDisplay(player, currentAmmo + reloadAmount);

                player.sendMessage(ChatColor.GREEN + "Reloaded! Ammo: " + (currentAmmo + reloadAmount) + "/" + magazineSize);
                player.playSound(player.getLocation(), Sound.ITEM_CROSSBOW_LOADING_END, 1.0f, 1.0f);
            }
        }.runTaskLater(GTMPlugin.getInstance(), reloadTime);
    }

    private void updateAmmoDisplay(Player player, int currentAmmo) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) return;
        ItemMeta meta = held.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (!meta.getDisplayName().contains(displayName)) return;

        List<String> lore = meta.getLore();
        if (lore != null && !lore.isEmpty()) {
            lore.set(0, ChatColor.GRAY + "Ammo: " + ChatColor.YELLOW + currentAmmo + "/" + magazineSize);
            meta.setLore(lore);
            held.setItemMeta(meta);
        }
    }

    // ========== GETTERS ==========

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public AmmoType getAmmoType() { return ammoType; }
    public int getMagazineSize() { return magazineSize; }
    public double getDamage() { return damage; }
    public double getRange() { return range; }
    public int getReloadTime() { return reloadTime; }
    public int getFireDelay() { return fireDelay; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }

    public boolean isWeaponItem(ItemStack item) {
        if (item == null || item.getType() != material) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        // Check lore for weapon ID
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (String line : lore) {
                    if (line.contains("Weapon ID: " + name)) return true;
                }
            }
        }
        return false;
    }
}
