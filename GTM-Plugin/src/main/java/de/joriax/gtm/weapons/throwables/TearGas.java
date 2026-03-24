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

public class TearGas implements Listener {

    private static final double GAS_RADIUS = 5.0;
    private static final int GAS_DURATION_SECONDS = 8;
    private static final String META_KEY = "gtm_teargas";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!TearItem.isTearGas(item)) return;

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

        String shooterName = projectile.getMetadata(META_KEY).get(0).asString();
        Player shooter = Bukkit.getPlayer(shooterName);

        Location center = projectile.getLocation();
        projectile.remove();

        deployTearGas(center, shooter);
    }

    private void deployTearGas(Location center, Player shooter) {
        center.getWorld().playSound(center, Sound.ENTITY_ARROW_HIT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= GAS_DURATION_SECONDS * 20) {
                    cancel();
                    return;
                }

                // Spawn gas particles
                center.getWorld().spawnParticle(Particle.CLOUD, center, 15, 2, 0.5, 2, 0.02);

                // Apply effects to nearby players
                for (Entity entity : center.getWorld().getNearbyEntities(center, GAS_RADIUS, GAS_RADIUS, GAS_RADIUS)) {
                    if (!(entity instanceof Player)) continue;
                    Player target = (Player) entity;
                    if (target.equals(shooter)) continue;

                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0));
                }

                ticks += 10;
            }
        }.runTaskTimer(GTMPlugin.getInstance(), 0L, 10L);
    }
}
