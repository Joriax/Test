package de.joriax.gtm.weapons.melee;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MeleeWeaponManager implements Listener {

    private final GTMPlugin plugin;
    private final Map<String, MeleeWeapon> weapons = new LinkedHashMap<>();

    public MeleeWeaponManager(GTMPlugin plugin) {
        this.plugin = plugin;
        registerDefaultWeapons();
    }

    private void registerDefaultWeapons() {
        registerWeapon(new MeleeWeapon(
                "knife", "Combat Knife",
                6.0, 2.0,
                Material.IRON_SWORD, 2001
        ));

        registerWeapon(new MeleeWeapon(
                "hammer", "Sledgehammer",
                10.0, 0.5,
                Material.IRON_SWORD, 2002
        ));

        registerWeapon(new MeleeWeapon(
                "bat", "Baseball Bat",
                8.0, 1.0,
                Material.IRON_SWORD, 2003
        ));
    }

    public void registerWeapon(MeleeWeapon weapon) {
        weapons.put(weapon.getName(), weapon);
    }

    public MeleeWeapon getWeapon(String name) {
        return weapons.get(name.toLowerCase());
    }

    public Collection<MeleeWeapon> getWeapons() {
        return weapons.values();
    }

    public MeleeWeapon getWeaponFromItem(ItemStack item) {
        if (item == null) return null;
        for (MeleeWeapon weapon : weapons.values()) {
            if (weapon.isMeleeItem(item)) return weapon;
        }
        return null;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player attacker = (Player) event.getDamager();

        ItemStack held = attacker.getInventory().getItemInMainHand();
        MeleeWeapon weapon = getWeaponFromItem(held);
        if (weapon == null) return;

        // Override damage
        event.setDamage(weapon.getDamage());
    }
}
