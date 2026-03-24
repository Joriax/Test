package de.joriax.gtm.weapons.throwables;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MolotovCocktail implements Listener {

    private static final double FIRE_RADIUS = 4.0;
    private static final int FIRE_DURATION_TICKS = 100; // 5 seconds
    private static final String META_KEY = "gtm_molotov";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!Moloitem.isMolotov(item)) return;

        event.setCancelled(true);

        // Remove one
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setMetadata(META_KEY, new FixedMetadataValue(GTMPlugin.getInstance(), player.getName()));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) return;
        Snowball projectile = (Snowball) event.getEntity();
        if (!projectile.hasMetadata(META_KEY)) return;

        String shooterName = projectile.getMetadata(META_KEY).get(0).asString();
        Player shooter = Bukkit.getPlayer(shooterName);

        Location center = projectile.getLocation();
        projectile.remove();

        createFireArea(center, shooter);
    }

    private void createFireArea(Location center, Player shooter) {
        center.getWorld().playSound(center, Sound.ITEM_BOTTLE_FILL, 1.0f, 0.5f);
        center.getWorld().spawnParticle(Particle.FLAME, center, 20, 2, 0.5, 2, 0.05);

        Set<UUID> burning = new HashSet<>();

        // Apply fire and effects to nearby entities
        for (Entity entity : center.getWorld().getNearbyEntities(center, FIRE_RADIUS, FIRE_RADIUS, FIRE_RADIUS)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity.equals(shooter)) continue;

            LivingEntity living = (LivingEntity) entity;
            living.setFireTicks(FIRE_DURATION_TICKS);
            living.damage(2.0, shooter);
            burning.add(entity.getUniqueId());
        }

        // Continuous fire damage for 5 seconds
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks += 20;
                if (ticks >= FIRE_DURATION_TICKS) {
                    cancel();
                    return;
                }

                center.getWorld().spawnParticle(Particle.FLAME, center, 10, 2, 0.2, 2, 0.05);

                for (Entity entity : center.getWorld().getNearbyEntities(center, FIRE_RADIUS, 2.0, FIRE_RADIUS)) {
                    if (!(entity instanceof LivingEntity)) continue;
                    if (entity.equals(shooter)) continue;
                    LivingEntity living = (LivingEntity) entity;
                    living.setFireTicks(FIRE_DURATION_TICKS);
                    burning.add(entity.getUniqueId());
                }
            }
        }.runTaskTimer(GTMPlugin.getInstance(), 20L, 20L);
    }
}
