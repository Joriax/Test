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
package de.joriax.spigotAdminSystem.Utils.Weather;

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

public class WeatherGUI
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private static final String WEATHER_GUI_TITLE = "\u00a7rSelect Weather";

    public static void openWeatherGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)WEATHER_GUI_TITLE);
        WeatherGUI.fillGlassPanes(gui);
        gui.setItem(10, WeatherGUI.createItem(Material.SUNFLOWER, "\u00a7aClear Weather", Arrays.asList("\u00a77Click to set clear weather"), "clear"));
        gui.setItem(12, WeatherGUI.createItem(Material.WATER_BUCKET, "\u00a7bRain", Arrays.asList("\u00a77Click to set rain"), "rain"));
        gui.setItem(14, WeatherGUI.createItem(Material.SNOWBALL, "\u00a7fSnow", Arrays.asList("\u00a77Click to set snow in snowy biomes"), "snow"));
        gui.setItem(16, WeatherGUI.createItem(Material.BLAZE_ROD, "\u00a7eThunder", Arrays.asList("\u00a77Click to set thunder"), "thunder"));
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
        if (!title.equals(WEATHER_GUI_TITLE)) {
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
        String weatherType = this.getWeatherTypeFromItem(clickedItem);
        if (weatherType == null) {
            return;
        }
        if (!player.hasPermission("utils.weather." + weatherType)) {
            player.sendMessage(this.prefix + "\u00a7cNo permission for " + weatherType + " weather!");
            player.closeInventory();
            return;
        }
        ConfirmGUI.openConfirmGUI(player, "weather", weatherType);
    }

    private String getWeatherTypeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore() || meta.getLore() == null) {
            return null;
        }
        String lore = (String)meta.getLore().get(0);
        if (lore.contains("clear weather")) {
            return "clear";
        }
        if (lore.contains("rain")) {
            return "rain";
        }
        if (lore.contains("snow")) {
            return "snow";
        }
        if (lore.contains("thunder")) {
            return "thunder";
        }
        return null;
    }
}

