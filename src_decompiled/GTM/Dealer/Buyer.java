/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Villager
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Dealer;

import de.joriax.economy.EconomyAPI;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Buyer
implements CommandExecutor,
Listener {
    private Villager buyer;
    private final EconomyAPI economyAPI;
    private double price = 10000.0;

    public Buyer(EconomyAPI economyAPI) {
        this.economyAPI = economyAPI;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            this.buyer = (Villager)player.getWorld().spawn(player.getLocation(), Villager.class);
            this.buyer.setCustomName("\u00a76Buyer");
            this.buyer.setCustomNameVisible(true);
            this.buyer.setAI(false);
            this.buyer.setInvulnerable(true);
            this.buyer.isSilent();
            return true;
        }
        return false;
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Villager && "\u00a76Buyer".equals(entity.getCustomName())) {
            this.openSellGUI(event.getPlayer());
        }
    }

    private void openSellGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a78Weapon Box Verkauf");
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = blackGlass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName("\u00a70");
            blackGlass.setItemMeta(glassMeta);
        }
        for (int i = 0; i < 27; ++i) {
            gui.setItem(i, blackGlass);
        }
        gui.setItem(10, this.createWeaponBox("\u00a76Verkaufe 8 Boxen", "\u00a77Erhalte: \u00a7e80 000.0$", 8));
        gui.setItem(13, this.createWeaponBox("\u00a76Verkaufe 16 Boxen", "\u00a77Erhalte: \u00a7e192 000.0$ | 20% Extra", 16));
        gui.setItem(16, this.createWeaponBox("\u00a76Verkaufe 64 Boxen", "\u00a77Erhalte: \u00a7e921 600.0$ | 44% Extra", 64));
        player.openInventory(gui);
    }

    private ItemStack createWeaponBox(String name, String lore, int amount) {
        ItemStack weaponBox = new ItemStack(Material.CHEST);
        ItemMeta boxMeta = weaponBox.getItemMeta();
        if (boxMeta != null) {
            boxMeta.setDisplayName(name);
            boxMeta.setLore(Arrays.asList(lore, "\u00a7eKlicke zum Verkaufen!"));
            weaponBox.setItemMeta(boxMeta);
        }
        return weaponBox;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("\u00a78Weapon Box Verkauf")) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player)event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        if (clickedItem.getType() == Material.CHEST) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            int amountToSell = 0;
            double reward = 0.0;
            if (displayName.contains("8 Boxen")) {
                amountToSell = 8;
                reward = 40000.0;
                this.economyAPI.addBalance(player, reward);
            } else if (displayName.contains("16 Boxen")) {
                amountToSell = 16;
                reward = 96000.0;
                this.economyAPI.addBalance(player, reward);
            } else if (displayName.contains("64 Boxen")) {
                amountToSell = 64;
                reward = 460800.0;
                this.economyAPI.addBalance(player, reward);
            }
            if (amountToSell > 0 && this.removeItems(player, "\u00a76Weapon Box", amountToSell)) {
                this.economyAPI.addBalance(player, reward);
                player.sendMessage("\u00a7aDu hast " + amountToSell + " Weapon Boxen verkauft und " + reward + "$ erhalten!");
            } else {
                player.sendMessage("\u00a7cDu hast nicht genug Weapon Boxen!");
            }
        }
    }

    private boolean removeItems(Player player, String displayName, int amount) {
        ItemStack item;
        int count = 0;
        ItemStack[] itemStackArray = player.getInventory().getContents();
        int n = itemStackArray.length;
        for (int i = 0; !(i >= n || (item = itemStackArray[i]) != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(displayName) && (count += item.getAmount()) >= amount); ++i) {
        }
        if (count < amount) {
            return false;
        }
        int remaining = amount;
        for (ItemStack item2 : player.getInventory().getContents()) {
            if (item2 == null || !item2.hasItemMeta() || !item2.getItemMeta().getDisplayName().equals(displayName)) continue;
            if (item2.getAmount() > remaining) {
                item2.setAmount(item2.getAmount() - remaining);
                break;
            }
            remaining -= item2.getAmount();
            player.getInventory().remove(item2);
        }
        return true;
    }
}

