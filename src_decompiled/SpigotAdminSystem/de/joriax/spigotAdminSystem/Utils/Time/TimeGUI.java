/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.joriax.spigotAdminSystem.Utils.Time;

import de.joriax.spigotAdminSystem.Utils.UtilsMain.ConfirmGUI;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TimeGUI
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private static final String TIME_GUI_TITLE = "\u00a7rSelect Time";

    public static void openTimeGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)TIME_GUI_TITLE);
        TimeGUI.fillGlassPanes(gui);
        gui.setItem(10, TimeGUI.createItem(Material.DAYLIGHT_DETECTOR, "\u00a7eMorning", Arrays.asList("\u00a77Click to set morning"), "morning"));
        gui.setItem(12, TimeGUI.createItem(Material.SUNFLOWER, "\u00a7eDay", Arrays.asList("\u00a77Click to set daytime"), "day"));
        gui.setItem(14, TimeGUI.createItem(Material.GLOWSTONE, "\u00a76Evening", Arrays.asList("\u00a77Click to set evening"), "evening"));
        gui.setItem(16, TimeGUI.createItem(Material.REDSTONE_LAMP, "\u00a7cMidnight", Arrays.asList("\u00a77Click to set midnight"), "midnight"));
        player.openInventory(gui);
    }

    private static void fillGlassPanes(Inventory gui) {
        ItemStack glassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName("\u00a7r");
        glassPane.setItemMeta(meta);
        for (int i = 0; i < gui.getSize(); ++i) {
            gui.setItem(i, glassPane);
        }
    }

    private static ItemStack createItem(Material material, String name, List<String> lore, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (!title.equals(TIME_GUI_TITLE)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasLore() || meta.getLore() == null) {
            return;
        }
        String timeType = this.getTimeTypeFromItem(clickedItem);
        if (timeType == null) {
            return;
        }
        if (!player.hasPermission("utils.time." + timeType)) {
            player.sendMessage(this.prefix + "\u00a7cNo permission for " + timeType + " time!");
            player.closeInventory();
            return;
        }
        ConfirmGUI.openConfirmGUI(player, "time", timeType);
    }

    private String getTimeTypeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore() || meta.getLore() == null) {
            return null;
        }
        String lore = (String)meta.getLore().get(0);
        if (lore.contains("morning")) {
            return "morning";
        }
        if (lore.contains("daytime")) {
            return "day";
        }
        if (lore.contains("evening")) {
            return "evening";
        }
        if (lore.contains("midnight")) {
            return "midnight";
        }
        return null;
    }
}

