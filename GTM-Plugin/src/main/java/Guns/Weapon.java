/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.FluidCollisionMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.Sound
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.util.RayTraceResult
 *  org.bukkit.util.Vector
 */
package Guns;

import Guns.AmmoType;
import Guns.RecoilHandler;
import Guns.SprayPattern;
import Guns.WeaponManager;
import at.Poriax.gTM.GTM;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Weapon {
    private String name;
    private Material material;
    private int clipsize;
    private int clepleft;
    private double damage;
    private int range;
    private double shotSpeed;
    private double fireRate;
    private List<String> lore;
    private AmmoType ammoType;
    private boolean isReloading = false;
    private BukkitRunnable reloadTask;
    private int buy;
    private int sell;
    private ItemStack reloadingItem;
    private Particle particleType;
    private int particleCount;
    private double particleSpeed;
    private double particleDensity;
    private int slownessLevel;
    private int probability;
    private List<Integer> shootpattern;
    private Sound shootsound;
    private Sound reloadSound;
    private RecoilHandler recoilHandler;
    private SprayPattern sprayPattern;
    private int bulletsPerShot;
    private double spread;

    public Weapon(String name, Material material, int clipsize, double damage, int range, double shotSpeed, double fireRate, List<String> lore, AmmoType ammoType, Particle particleType, int particleCount, double particleSpeed, double particleDensity, int slownessLevel, int probability, List<Integer> shootpattern, int sell, int buy, Sound shootsound, Sound reloadSound, double recoilStrength, double sprayPatternDeviation, int bulletsPerShot, double spread) {
        this.name = name;
        this.material = material;
        this.clipsize = clipsize;
        this.damage = damage;
        this.clepleft = clipsize;
        this.range = range;
        this.shotSpeed = shotSpeed;
        this.fireRate = fireRate;
        this.lore = lore;
        this.ammoType = ammoType;
        this.particleType = particleType;
        this.particleCount = particleCount;
        this.particleSpeed = particleSpeed;
        this.particleDensity = particleDensity;
        this.slownessLevel = slownessLevel;
        this.probability = probability;
        this.shootpattern = shootpattern;
        this.sell = sell;
        this.buy = buy;
        this.shootsound = shootsound;
        this.reloadSound = reloadSound;
        this.recoilHandler = new RecoilHandler(recoilStrength);
        this.sprayPattern = new SprayPattern(sprayPatternDeviation);
        this.bulletsPerShot = bulletsPerShot;
        this.spread = spread;
    }

    public int getProbability() {
        return this.probability;
    }

    private String getDisplayName(Player player) {
        return this.name + " <<" + this.clepleft + "/" + this.getCurrentAmmo(player) + ">>";
    }

    private int getCurrentAmmo(Player player) {
        return WeaponManager.getAmmo(player, this.ammoType);
    }

    public List<Integer> getShootpattern() {
        return this.shootpattern;
    }

    public void setClepleft(int clepleft) {
        this.clepleft = clepleft;
    }

    public int getBuy() {
        return this.buy;
    }

    public int getSell() {
        return this.sell;
    }

    public int getBulletsPerShot() {
        return this.bulletsPerShot;
    }

    public void shoot(Player player) {
        if (this.isReloading) {
            return;
        }
        if (this.clepleft > 0) {
            player.getWorld().playSound(player.getLocation(), this.shootsound, 1.0f, 1.0f);
            Location eyeLocation = player.getEyeLocation();
            Vector baseDirection = eyeLocation.getDirection().normalize();
            if (this.bulletsPerShot > 1) {
                Vector upVector = new Vector(0, 1, 0);
                Vector rightVector = baseDirection.clone().crossProduct(upVector).normalize();
                for (int i = 0; i < this.bulletsPerShot; ++i) {
                    double angle = Math.PI * 2 * (double)i / (double)this.bulletsPerShot;
                    double spreadX = Math.cos(angle) * this.spread;
                    double spreadY = Math.sin(angle) * this.spread;
                    Vector shotDirection = baseDirection.clone().add(rightVector.clone().multiply(spreadX)).add(upVector.clone().multiply(spreadY)).normalize();
                    this.shootSingleProjectile(player, eyeLocation, shotDirection);
                }
            } else {
                Vector sprayDirection = this.sprayPattern.applySpray(baseDirection);
                this.shootSingleProjectile(player, eyeLocation, sprayDirection);
            }
            this.recoilHandler.applyRecoil(player);
            --this.clepleft;
            this.updateWeaponName(player);
            if (this.clepleft <= 0) {
                this.reload(player);
            }
        }
    }

    private void shootSingleProjectile(Player player, Location eyeLocation, Vector direction) {
        Location particleLocation;
        Location hitLocation;
        RayTraceResult rayTraceResult = player.getWorld().rayTrace(eyeLocation, direction, (double)this.range, FluidCollisionMode.NEVER, true, 0.1, entity -> entity instanceof LivingEntity && !entity.equals((Object)player));
        if (rayTraceResult != null) {
            if (rayTraceResult.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)rayTraceResult.getHitEntity();
                target.setNoDamageTicks(0);
                target.damage(this.damage, (Entity)player);
            }
            hitLocation = rayTraceResult.getHitPosition().toLocation(player.getWorld());
        } else {
            hitLocation = eyeLocation.clone().add(direction.multiply(this.range));
        }
        Vector particleDirection = hitLocation.toVector().subtract(eyeLocation.toVector()).normalize();
        double distance = eyeLocation.distance(hitLocation);
        double stepSize = 0.2;
        for (double d = 0.0; d < distance && !(particleLocation = eyeLocation.clone().add(particleDirection.clone().multiply(d))).getBlock().getType().isSolid(); d += stepSize) {
            player.getWorld().spawnParticle(this.particleType, particleLocation, this.particleCount, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public void reload(final Player player) {
        if (this.isReloading) {
            return;
        }
        int currentAmmo = this.getCurrentAmmo(player);
        if (currentAmmo > 0) {
            int neededAmmo = this.clipsize - this.clepleft;
            final int reloadAmount = Math.min(neededAmmo, currentAmmo);
            double reloadTime = this.calculateReloadTime(reloadAmount);
            player.getWorld().playSound(player.getLocation(), this.reloadSound, 1.0f, 1.0f);
            this.isReloading = true;
            this.reloadingItem = player.getInventory().getItemInMainHand().clone();
            long stepDelay = (long)(reloadTime * 20.0 / (double)reloadAmount);
            this.reloadTask = new BukkitRunnable(){
                int loadedAmmo = 0;

                public void run() {
                    ItemStack currentItem = player.getInventory().getItemInMainHand();
                    if (!Weapon.this.isSameWeapon(currentItem, Weapon.this.reloadingItem) || this.loadedAmmo >= reloadAmount) {
                        player.sendMessage(String.valueOf(ChatColor.GREEN) + "Nachladen beendet oder Waffe gewechselt!");
                        Weapon.this.isReloading = false;
                        this.cancel();
                        return;
                    }
                    if (this.loadedAmmo < reloadAmount) {
                        ++Weapon.this.clepleft;
                        WeaponManager.decreaseAmmo(player, Weapon.this.ammoType, 1);
                        Weapon.this.updateWeaponName(player);
                        ++this.loadedAmmo;
                        player.getWorld().playSound(player.getLocation(), Weapon.this.reloadSound, 1.0f, 1.0f);
                    }
                    if (this.loadedAmmo >= reloadAmount) {
                        player.sendMessage(String.valueOf(ChatColor.GREEN) + "Nachgeladen! Munition \u00fcbrig: " + Weapon.this.getCurrentAmmo(player));
                        Weapon.this.isReloading = false;
                        this.cancel();
                    }
                }
            };
            this.reloadTask.runTaskTimer((Plugin)GTM.getInstance(), 0L, stepDelay);
        }
    }

    private boolean isSameWeapon(ItemStack currentItem, ItemStack originalItem) {
        if (currentItem == null || originalItem == null || !currentItem.hasItemMeta() || !originalItem.hasItemMeta()) {
            return false;
        }
        ItemMeta currentMeta = currentItem.getItemMeta();
        ItemMeta originalMeta = originalItem.getItemMeta();
        if (currentMeta == null || originalMeta == null) {
            return false;
        }
        String currentName = currentMeta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", "");
        String originalName = originalMeta.getDisplayName().replaceAll(" <<\\d+/\\d+>>", "");
        return currentName.equals(originalName);
    }

    public boolean isReloading() {
        return this.isReloading;
    }

    public double getFireRate() {
        return this.fireRate;
    }

    public void cancelReload() {
        if (this.reloadTask != null) {
            this.reloadTask.cancel();
            this.reloadTask = null;
        }
        this.isReloading = false;
    }

    private double calculateReloadTime(int reloadAmount) {
        double timePerBullet = this.clipsize <= 12 ? 0.3 : (this.clipsize <= 50 ? 0.1 : 0.001);
        return (double)reloadAmount * timePerBullet;
    }

    private void updateWeaponName(Player player) {
        ItemMeta meta;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.hasItemMeta() && (meta = item.getItemMeta()) != null) {
            meta.setDisplayName(this.getDisplayName(player));
            item.setItemMeta(meta);
        }
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getClepleft() {
        return this.clepleft;
    }

    public AmmoType getAmmoType() {
        return this.ammoType;
    }

    public int getSlownessLevel() {
        return this.slownessLevel;
    }

    public ItemStack getWeaponItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.name + " <<" + this.clipsize + "/" + this.clipsize + ">>");
            meta.setLore(this.lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public RecoilHandler getRecoilHandler() {
        return this.recoilHandler;
    }

    public SprayPattern getSprayPattern() {
        return this.sprayPattern;
    }
}

