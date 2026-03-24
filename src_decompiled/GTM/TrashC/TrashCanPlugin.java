/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package TrashC;

import at.Poriax.gTM.GTM;
import de.joriax.economy.EconomyAPI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TrashCanPlugin
implements Listener {
    private Map<Player, Inventory> openInventories = new HashMap<Player, Inventory>();
    private EconomyAPI economyAPI;

    public TrashCanPlugin(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (clickedBlock = event.getClickedBlock()) != null && clickedBlock.getType() == Material.DISPENSER) {
            this.openTrashCan(event.getPlayer());
            event.setCancelled(true);
        }
    }

    public void openTrashCan(Player player) {
        Inventory inv = Bukkit.createInventory(null, (int)54, (String)"Trash Can");
        for (int i = 0; i < 54; ++i) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                if (i % 2 == 0) {
                    inv.setItem(i, this.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
                    continue;
                }
                inv.setItem(i, this.createItem(Material.WHITE_STAINED_GLASS_PANE, " "));
                continue;
            }
            inv.setItem(i, null);
        }
        inv.setItem(53, this.createItem(Material.DIAMOND, String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.BOLD) + "SELL INVENTORY"));
        inv.setItem(44, this.createItem(Material.PAPER, String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.BOLD) + "SELL"));
        player.openInventory(inv);
        this.openInventories.put(player, inv);
        this.updateSellButton(player);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        Inventory inv = this.openInventories.get(player);
        if (inv == null || !event.getInventory().equals((Object)inv)) {
            return;
        }
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE || clickedItem.getType() == Material.WHITE_STAINED_GLASS_PANE)) {
            event.setCancelled(true);
            return;
        }
        if (event.getSlot() == 53 || event.getSlot() == 44) {
            event.setCancelled(true);
            if (event.getSlot() == 44) {
                double total = this.sellGUIInventory(player, inv);
                if (total > 0.0) {
                    player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + total + " $");
                } else {
                    player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.RED) + "No items to sell");
                }
            } else if (event.getSlot() == 53) {
                double total = this.sellPlayerInventory(player);
                if (total > 0.0) {
                    player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + total + " $");
                } else {
                    player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.RED) + "No items to sell");
                }
            }
            return;
        }
        new BukkitRunnable(){

            public void run() {
                TrashCanPlugin.this.updateSellButton(player);
            }
        }.runTaskLater((Plugin)GTM.getInstance(), 5L);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player player = (Player)event.getWhoClicked();
        Inventory inv = this.openInventories.get(player);
        if (inv == null || !event.getInventory().equals((Object)inv)) {
            return;
        }
        Iterator iterator = event.getRawSlots().iterator();
        while (iterator.hasNext()) {
            int slot = (Integer)iterator.next();
            ItemStack draggedItem = inv.getItem(slot);
            if (draggedItem == null || draggedItem.getType() != Material.BLACK_STAINED_GLASS_PANE && draggedItem.getType() != Material.WHITE_STAINED_GLASS_PANE) continue;
            event.setCancelled(true);
            return;
        }
        new BukkitRunnable(){

            public void run() {
                TrashCanPlugin.this.updateSellButton(player);
            }
        }.runTaskLater((Plugin)GTM.getInstance(), 5L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getPlayer();
        Inventory inv = this.openInventories.get(player);
        if (inv == null || !event.getInventory().equals((Object)inv)) {
            return;
        }
        double total = this.sellGUIInventory(player, inv);
        if (total > 0.0) {
            player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + total + " $");
        } else {
            player.sendMessage(String.valueOf(ChatColor.DARK_AQUA) + String.valueOf(ChatColor.BOLD) + "BANK >  " + String.valueOf(ChatColor.RED) + "No items to sell");
        }
        this.openInventories.remove(player);
    }

    private double sellPlayerInventory(Player player) {
        double total = 0.0;
        for (int i = 9; i < 36; ++i) {
            double price;
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !((price = this.getSellPrice(item)) > 0.0)) continue;
            total += price;
            player.getInventory().setItem(i, null);
        }
        if (total > 0.0) {
            this.economyAPI.addBalance(player, total);
        }
        this.updateSellButton(player);
        return total;
    }

    private double sellGUIInventory(Player player, Inventory inv) {
        double total = 0.0;
        for (int i = 0; i < 54; ++i) {
            double price;
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.PAPER || item.getType() == Material.DIAMOND || !((price = this.getSellPrice(item)) > 0.0)) continue;
            total += price;
            inv.setItem(i, null);
        }
        if (total > 0.0) {
            this.economyAPI.addBalance(player, total);
        }
        this.updateSellButton(player);
        return total;
    }

    private double getTotal(Player player) {
        double total = 0.0;
        Inventory inv = this.openInventories.get(player);
        if (inv != null) {
            for (int i = 0; i < 54; ++i) {
                ItemStack item = inv.getItem(i);
                if (item == null || item.getType() == Material.PAPER || item.getType() == Material.DIAMOND) continue;
                total += this.getSellPrice(item);
            }
        }
        return total;
    }

    private void updateSellButton(Player player) {
        ItemMeta meta;
        ItemStack sellItem;
        double total = this.getTotal(player);
        String totalString = total > 0.0 ? String.valueOf(ChatColor.GREEN) + "Sell for " + total + " $" : String.valueOf(ChatColor.RED) + "No items to sell";
        Inventory inv = this.openInventories.get(player);
        if (inv != null && (sellItem = inv.getItem(44)) != null && sellItem.getType() == Material.PAPER && (meta = sellItem.getItemMeta()) != null) {
            meta.setDisplayName(totalString);
            sellItem.setItemMeta(meta);
            inv.setItem(44, sellItem);
            player.updateInventory();
        }
    }

    private double getSellPrice(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return 0.0;
        }
        ItemMeta meta = item.getItemMeta();
        String displayName = meta.getDisplayName();
        double pricePerItem = 0.0;
        if (displayName.contains("AK-47")) {
            pricePerItem = 50.0;
        } else if (displayName.contains("Titanium Vest")) {
            pricePerItem = 100.0;
        } else if (displayName.contains("Kevlar Vest")) {
            pricePerItem = 75.0;
        } else if (displayName.contains("Jumpos")) {
            pricePerItem = 60.0;
        } else if (displayName.contains("Weapon Box")) {
            pricePerItem = 5000.0;
        } else if (displayName.contains("Smoke Granate")) {
            pricePerItem = 20.0;
        }
        return pricePerItem * (double)item.getAmount();
    }
}

