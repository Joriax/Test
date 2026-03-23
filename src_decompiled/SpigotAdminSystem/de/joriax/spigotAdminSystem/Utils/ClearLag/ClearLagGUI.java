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
package de.joriax.spigotAdminSystem.Utils.ClearLag;

import de.joriax.spigotAdminSystem.Utils.ClearLag.ClearLagManager;
import java.util.Arrays;
import java.util.List;
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

public class ClearLagGUI
implements Listener {
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Utils&8] &r");
    private static final String CLEARLAG_GUI_TITLE = "\u00a7rClearLag Settings";
    private final ClearLagManager clearLagManager;

    public ClearLagGUI(ClearLagManager clearLagManager) {
        this.clearLagManager = clearLagManager;
    }

    public static void openClearLagGUI(Player player, ClearLagManager clearLagManager) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)CLEARLAG_GUI_TITLE);
        ClearLagGUI.fillGlassPanes(gui);
        gui.setItem(11, ClearLagGUI.createItem(Material.TNT, "\u00a7cStart ClearLag", Arrays.asList("\u00a77Click to start ClearLag now"), "start"));
        gui.setItem(15, ClearLagGUI.createItem(Material.ANVIL, "\u00a7eSet Interval", Arrays.asList("\u00a77Click to set ClearLag interval (minutes)"), "interval"));
        player.openInventory(gui);
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
        if (!title.equals(CLEARLAG_GUI_TITLE)) {
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
        String type = ((String)meta.getLore().get(0)).contains("start") ? "start" : "interval";
        String permission = "utils.clearlag." + type;
        if (!player.hasPermission(permission)) {
            player.sendMessage(this.prefix + "\u00a7cNo permission to " + (type.equals("start") ? "start ClearLag" : "set ClearLag interval") + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            player.closeInventory();
            return;
        }
        if (type.equals("start")) {
            this.clearLagManager.startManualClearLag();
            player.sendMessage(this.prefix + "\u00a7aClearLag started! Items will be cleared in 30 seconds.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            player.closeInventory();
        } else {
            this.openAnvilGUI(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }

    private void openAnvilGUI(Player player) {
        Inventory anvilGUI = Bukkit.createInventory(null, (int)27, (String)"\u00a7rSet ClearLag Interval");
        ClearLagGUI.fillGlassPanes(anvilGUI);
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName("\u00a7eEnter Interval (minutes)");
        paperMeta.setLore(Arrays.asList("\u00a77Current: " + this.clearLagManager.getClearLagInterval() + " minutes", "\u00a77Click with a renamed paper"));
        paper.setItemMeta(paperMeta);
        anvilGUI.setItem(13, paper);
        player.openInventory(anvilGUI);
    }

    @EventHandler
    public void onAnvilGUIClick(InventoryClickEvent event) {
        ItemMeta cursorMeta;
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (!title.equals("\u00a7rSet ClearLag Interval")) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.PAPER || !clickedItem.hasItemMeta()) {
            return;
        }
        ItemStack cursorItem = event.getCursor();
        if (cursorItem != null && cursorItem.getType() == Material.PAPER && cursorItem.hasItemMeta() && (cursorMeta = cursorItem.getItemMeta()).hasDisplayName()) {
            String input = cursorMeta.getDisplayName().replaceAll("[^0-9]", "");
            try {
                long minutes = Long.parseLong(input);
                if (minutes < 1L) {
                    player.sendMessage(this.prefix + "\u00a7cInterval must be at least 1 minute!");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    return;
                }
                this.clearLagManager.setClearLagInterval(minutes);
                player.sendMessage(this.prefix + "\u00a7aClearLag interval set to " + minutes + " minutes!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.closeInventory();
            }
            catch (NumberFormatException e) {
                player.sendMessage(this.prefix + "\u00a7cInvalid number! Please enter a valid number of minutes.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
            }
        }
    }
}

