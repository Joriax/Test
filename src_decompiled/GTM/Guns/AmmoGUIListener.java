/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Guns;

import Guns.AmmoCommand;
import Guns.AmmoType;
import Guns.WeaponManager;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AmmoGUIListener
implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        if (!event.getView().getTitle().equals(String.valueOf(ChatColor.DARK_GRAY) + "Dein Munitionsbeutel")) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        String displayName = meta.getDisplayName();
        AmmoType ammoType = this.getAmmoTypeFromDisplayName(displayName);
        if (ammoType != null) {
            int currentAmmo = WeaponManager.getAmmo(player, ammoType);
            if (currentAmmo >= 64) {
                ItemStack ammoItem = this.createAmmoItem(ammoType);
                Location dropLocation = player.getLocation();
                dropLocation.add(player.getLocation().getDirection().normalize().multiply(1.5));
                dropLocation.setY(dropLocation.getY() + 0.5);
                Item droppedItem = player.getWorld().dropItem(dropLocation, ammoItem);
                droppedItem.setPickupDelay(20);
                WeaponManager.decreaseAmmo(player, ammoType, 64);
                player.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast 64x " + displayName + " gedroppt!");
                player.closeInventory();
                AmmoCommand.openAmmoGUI(player);
            } else {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Du hast nicht genug Munition! (Mindestens 64 ben\u00f6tigt)");
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        String displayName = meta.getDisplayName();
        AmmoType ammoType = this.getAmmoTypeFromDisplayName(displayName);
        if (ammoType != null) {
            Player player = event.getPlayer();
            WeaponManager.addAmmo(player, ammoType, 64);
            player.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast 64x " + displayName + " aufgesammelt!");
            event.getItem().remove();
            event.setCancelled(true);
        }
    }

    private AmmoType getAmmoTypeFromDisplayName(String displayName) {
        if (displayName.equals(String.valueOf(ChatColor.GRAY) + "Pistol Ammo")) {
            return AmmoType.PISTOL;
        }
        if (displayName.equals(String.valueOf(ChatColor.YELLOW) + "SMG Ammo")) {
            return AmmoType.SMG;
        }
        if (displayName.equals(String.valueOf(ChatColor.RED) + "AR Ammo")) {
            return AmmoType.AR;
        }
        if (displayName.equals(String.valueOf(ChatColor.BLUE) + "Sniper Ammo")) {
            return AmmoType.SNIPER;
        }
        return null;
    }

    private ItemStack createAmmoItem(AmmoType ammoType) {
        String displayName;
        Material material;
        switch (ammoType) {
            case PISTOL: {
                material = Material.IRON_NUGGET;
                displayName = String.valueOf(ChatColor.GRAY) + "Pistol Ammo";
                break;
            }
            case SMG: {
                material = Material.GOLD_NUGGET;
                displayName = String.valueOf(ChatColor.YELLOW) + "SMG Ammo";
                break;
            }
            case AR: {
                material = Material.COPPER_INGOT;
                displayName = String.valueOf(ChatColor.RED) + "AR Ammo";
                break;
            }
            case SNIPER: {
                material = Material.DIAMOND;
                displayName = String.valueOf(ChatColor.BLUE) + "Sniper Ammo";
                break;
            }
            default: {
                return null;
            }
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Enth\u00e4lt 64 Munition"));
            item.setItemMeta(meta);
        }
        return item;
    }
}

