/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.joriax.spigotAdminSystem.Utils.UtilsMain;

import de.joriax.spigotAdminSystem.Utils.Time.TimeHandler;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.GUIManager;
import de.joriax.spigotAdminSystem.Utils.Weather.WeatherHandler;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private static final String CONFIRM_GUI_TITLE = "\u00a7rConfirm";

    public static void openConfirmGUI(Player player, String category, String type) {
        Inventory confirmGUI = Bukkit.createInventory(null, (int)27, (String)CONFIRM_GUI_TITLE);
        ConfirmGUI.fillGlassPanes(confirmGUI);
        Material displayMaterial = category.equals("weather") ? (type.equals("clear") ? Material.SUNFLOWER : (type.equals("rain") ? Material.WATER_BUCKET : (type.equals("snow") ? Material.SNOWBALL : Material.BLAZE_ROD))) : (type.equals("morning") ? Material.DAYLIGHT_DETECTOR : (type.equals("day") ? Material.SUNFLOWER : (type.equals("evening") ? Material.GLOWSTONE : Material.REDSTONE_LAMP)));
        ItemStack displayItem = new ItemStack(displayMaterial);
        ItemMeta meta = displayItem.getItemMeta();
        meta.setDisplayName("\u00a76Selected " + (category.equals("weather") ? "Weather" : "Time") + ": " + type);
        meta.setLore(Arrays.asList("\u00a77" + (category.equals("weather") ? "Weather" : "Time") + ": " + type));
        displayItem.setItemMeta(meta);
        confirmGUI.setItem(13, displayItem);
        confirmGUI.setItem(11, ConfirmGUI.createConfirmItem(true));
        confirmGUI.setItem(15, ConfirmGUI.createConfirmItem(false));
        player.openInventory(confirmGUI);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
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

    private static ItemStack createConfirmItem(boolean confirm) {
        Material material = confirm ? Material.LIME_WOOL : Material.RED_WOOL;
        String name = confirm ? "\u00a7aConfirm" : "\u00a7cCancel";
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
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
        if (!title.equals(CONFIRM_GUI_TITLE)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) {
            return;
        }
        ItemStack displayItem = event.getInventory().getItem(13);
        if (displayItem == null || !displayItem.hasItemMeta() || !displayItem.getItemMeta().hasLore()) {
            return;
        }
        String type = ((String)displayItem.getItemMeta().getLore().get(0)).split(": ")[1];
        boolean isWeather = ((String)displayItem.getItemMeta().getLore().get(0)).contains("Weather");
        if (clickedItem.getType() == Material.LIME_WOOL) {
            if (isWeather) {
                WeatherHandler.applyWeather(player.getWorld(), type.toLowerCase());
                player.sendMessage(this.prefix + "\u00a7aWeather set to " + type + "!");
            } else {
                TimeHandler.applyTime(player.getWorld(), type.toLowerCase());
                player.sendMessage(this.prefix + "\u00a7aTime set to " + type + "!");
            }
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.closeInventory();
        } else if (clickedItem.getType() == Material.RED_WOOL) {
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            GUIManager.openMainGUI(player);
        }
    }
}

