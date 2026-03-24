package de.joriax.gtm.weapons.throwables;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
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

public class SmokeGrenade implements Listener {

    private static final double SMOKE_RADIUS = 6.0;
    private static final int SMOKE_DURATION_SECONDS = 10;
    private static final String META_KEY = "gtm_smoke";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!Smoke.isSmoke(item)) return;

        event.setCancelled(true);

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

        Location center = projectile.getLocation();
        projectile.remove();

        deploySmokeGrenade(center);
    }

    private void deploySmokeGrenade(Location center) {
        center.getWorld().playSound(center, Sound.ENTITY_ARROW_HIT, 1.0f, 1.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= SMOKE_DURATION_SECONDS * 20) {
                    cancel();
                    return;
                }

                // Dense smoke particles
                center.getWorld().spawnParticle(Particle.CLOUD, center, 30, 3, 0.5, 3, 0.01);
                center.getWorld().spawnParticle(Particle.SMOKE, center, 20, 3, 0.5, 3, 0.02);

                // Apply blindness to players in smoke
                for (Entity entity : center.getWorld().getNearbyEntities(center, SMOKE_RADIUS, SMOKE_RADIUS, SMOKE_RADIUS)) {
                    if (!(entity instanceof Player)) continue;
                    Player target = (Player) entity;
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
                }

                ticks += 10;
            }
        }.runTaskTimer(GTMPlugin.getInstance(), 0L, 10L);
    }
}
