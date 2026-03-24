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
import org.bukkit.scheduler.BukkitRunnable;

public class ThrowableGrenade implements Listener {

    private static final double EXPLOSION_RADIUS = 5.0;
    private static final double EXPLOSION_DAMAGE = 8.0;
    private static final String META_KEY = "gtm_grenade";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!GrenadeItem.isGrenade(item)) return;

        event.setCancelled(true);

        // Remove one from hand
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // Throw a snowball as the grenade projectile
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

        explode(projectile.getLocation(), shooter);
        projectile.remove();
    }

    private void explode(Location location, Player shooter) {
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        location.getWorld().spawnParticle(Particle.EXPLOSION, location, 1);
        location.getWorld().spawnParticle(Particle.FLAME, location, 30, 1, 1, 1, 0.1);

        // Damage nearby entities
        for (Entity entity : location.getWorld().getNearbyEntities(location, EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS)) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity.equals(shooter)) continue;

            double distance = entity.getLocation().distance(location);
            double damageMultiplier = 1.0 - (distance / EXPLOSION_RADIUS);
            double actualDamage = EXPLOSION_DAMAGE * damageMultiplier;

            ((LivingEntity) entity).damage(actualDamage, shooter);
        }
    }
}
