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
package de.joriax.spigotAdminSystem.Utils.UtilsMain;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.ClearLag.ClearLagGUI;
import de.joriax.spigotAdminSystem.Utils.Maintenance.MaintenanceGUI;
import de.joriax.spigotAdminSystem.Utils.Time.TimeGUI;
import de.joriax.spigotAdminSystem.Utils.Weather.WeatherGUI;
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

public class GUIManager
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private static final String MAIN_GUI_TITLE = "\u00a7rSelect Option";

    public static void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)MAIN_GUI_TITLE);
        GUIManager.fillGlassPanes(gui);
        boolean maintenanceMode = SpigotAdminSystem.getInstance().getMaintenanceManager().isMaintenanceMode();
        String maintenanceStatus = maintenanceMode ? "\u00a7a(Enabled)" : "\u00a7c(Disabled)";
        gui.setItem(10, GUIManager.createItem(Material.CLOCK, "\u00a7eTime Settings", Arrays.asList("\u00a77Click to adjust time"), "time"));
        gui.setItem(12, GUIManager.createItem(Material.TNT, "\u00a7cClearLag Settings", Arrays.asList("\u00a77Click to manage ClearLag"), "clearlag"));
        gui.setItem(14, GUIManager.createItem(Material.REDSTONE_BLOCK, "\u00a76Maintenance Settings " + maintenanceStatus, Arrays.asList("\u00a77Click to manage maintenance mode"), "maintenance"));
        gui.setItem(16, GUIManager.createItem(Material.COMPASS, "\u00a7bWeather Settings", Arrays.asList("\u00a77Click to adjust weather"), "weather"));
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
        String type;
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();
        if (!title.equals(MAIN_GUI_TITLE) || clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        event.setCancelled(true);
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        String name = meta.getDisplayName();
        if (name.contains("Time Settings")) {
            type = "time";
        } else if (name.contains("ClearLag Settings")) {
            type = "clearlag";
        } else if (name.contains("Maintenance Settings")) {
            type = "maintenance";
        } else if (name.contains("Weather Settings")) {
            type = "weather";
        } else {
            return;
        }
        if (!player.hasPermission("utils." + type)) {
            player.sendMessage(this.prefix + "\u00a7cNo permission to access " + type + " settings!");
            player.closeInventory();
            return;
        }
        if (type.equals("weather")) {
            WeatherGUI.openWeatherGUI(player);
        } else if (type.equals("time")) {
            TimeGUI.openTimeGUI(player);
        } else if (type.equals("clearlag")) {
            ClearLagGUI.openClearLagGUI(player, SpigotAdminSystem.getInstance().getClearLagManager());
        } else if (type.equals("maintenance")) {
            MaintenanceGUI.openMaintenanceGUI(player, SpigotAdminSystem.getInstance().getMaintenanceManager());
        }
    }
}

