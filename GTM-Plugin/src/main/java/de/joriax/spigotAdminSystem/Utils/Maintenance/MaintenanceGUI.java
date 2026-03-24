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
package de.joriax.spigotAdminSystem.Utils.Maintenance;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import de.joriax.spigotAdminSystem.Utils.Maintenance.MaintenanceManager;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.GUIManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

public class MaintenanceGUI
implements Listener {
    private static final String GUI_TITLE = "\u00a7rMaintenance Settings";
    private static final String CONFIRM_GUI_TITLE = "\u00a7rConfirm Maintenance Change";
    private static final Map<UUID, Boolean> awaitingConfirmation = new HashMap<UUID, Boolean>();
    private static final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");

    public static void openMaintenanceGUI(Player player, MaintenanceManager maintenanceManager) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)GUI_TITLE);
        MaintenanceGUI.fillGlassPanes(gui);
        boolean isEnabled = maintenanceManager.isMaintenanceMode();
        gui.setItem(4, MaintenanceGUI.createItem(Material.REDSTONE_LAMP, isEnabled ? "\u00a7aStatus: \u00a7lENABLED" : "\u00a7cStatus: \u00a7lDISABLED", Arrays.asList("\u00a77Current maintenance mode state")));
        gui.setItem(11, MaintenanceGUI.createItem(Material.LIME_WOOL, "\u00a7aEnable Maintenance Mode", Arrays.asList("\u00a77Click to enable maintenance", "\u00a77Kicks players without bypass permission")));
        gui.setItem(15, MaintenanceGUI.createItem(Material.RED_WOOL, "\u00a7cDisable Maintenance Mode", Arrays.asList("\u00a77Click to disable maintenance", "\u00a77Will allow all players to join")));
        gui.setItem(26, MaintenanceGUI.createItem(Material.BARRIER, "\u00a7cBack to Main Menu", Arrays.asList("\u00a77Return to Utils menu")));
        player.openInventory(gui);
    }

    public static void openConfirmationGUI(Player player, boolean enable, MaintenanceManager maintenanceManager) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)CONFIRM_GUI_TITLE);
        MaintenanceGUI.fillGlassPanes(gui);
        gui.setItem(4, MaintenanceGUI.createItem(Material.PAPER, enable ? "\u00a7aEnable Maintenance Mode?" : "\u00a7cDisable Maintenance Mode?", Arrays.asList("\u00a77Are you sure you want to", enable ? "\u00a77enable maintenance mode?" : "\u00a77disable maintenance mode?")));
        gui.setItem(11, MaintenanceGUI.createItem(Material.EMERALD_BLOCK, "\u00a7a\u00a7lCONFIRM", Arrays.asList("\u00a77Click to confirm")));
        gui.setItem(15, MaintenanceGUI.createItem(Material.REDSTONE_BLOCK, "\u00a7c\u00a7lCANCEL", Arrays.asList("\u00a77Click to cancel")));
        awaitingConfirmation.put(player.getUniqueId(), enable);
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

    private static ItemStack createItem(Material material, String name, List<String> lore) {
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
        ItemStack clickedItem = event.getCurrentItem();
        if ((title.equals(GUI_TITLE) || title.equals(CONFIRM_GUI_TITLE)) && clickedItem != null) {
            event.setCancelled(true);
            if (!clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }
            String displayName = clickedItem.getItemMeta().getDisplayName();
            MaintenanceManager maintenanceManager = MaintenanceGUI.getMaintenanceManager();
            if (title.equals(GUI_TITLE)) {
                if (displayName.contains("Enable Maintenance Mode")) {
                    MaintenanceGUI.openConfirmationGUI(player, true, maintenanceManager);
                } else if (displayName.contains("Disable Maintenance Mode")) {
                    MaintenanceGUI.openConfirmationGUI(player, false, maintenanceManager);
                } else if (displayName.contains("Back to Main Menu")) {
                    GUIManager.openMainGUI(player);
                }
            } else if (title.equals(CONFIRM_GUI_TITLE)) {
                Boolean enable = awaitingConfirmation.get(player.getUniqueId());
                if (enable == null) {
                    return;
                }
                if (displayName.contains("CONFIRM")) {
                    maintenanceManager.setMaintenanceMode(enable);
                    player.sendMessage(prefix + (enable != false ? "\u00a7aMaintenance mode enabled!" : "\u00a7aMaintenance mode disabled!"));
                    MaintenanceGUI.openMaintenanceGUI(player, maintenanceManager);
                    awaitingConfirmation.remove(player.getUniqueId());
                } else if (displayName.contains("CANCEL")) {
                    MaintenanceGUI.openMaintenanceGUI(player, maintenanceManager);
                    awaitingConfirmation.remove(player.getUniqueId());
                }
            }
        }
    }

    private static MaintenanceManager getMaintenanceManager() {
        return SpigotAdminSystem.getInstance().getMaintenanceManager();
    }
}

