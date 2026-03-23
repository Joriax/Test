/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.joriax.tradeSystem;

import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeSession {
    public static final int ACCEPT_SLOT = 33;
    public static final int DECLINE_SLOT = 29;
    private final Player player1;
    private final Player player2;
    private final Inventory player1Inventory;
    private final Inventory player2Inventory;
    private double player1Money = 0.0;
    private double player2Money = 0.0;
    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private final TradeManager tradeManager;
    private final EconomyAPI economyAPI;

    public TradeSession(Player player1, Player player2, TradeManager tradeManager, EconomyAPI economyAPI) {
        this.player1 = player1;
        this.player2 = player2;
        this.tradeManager = tradeManager;
        this.economyAPI = economyAPI;
        this.player1Inventory = Bukkit.createInventory((InventoryHolder)player1, (int)36, (String)("Handel mit " + player2.getName()));
        this.player2Inventory = Bukkit.createInventory((InventoryHolder)player2, (int)36, (String)("Handel mit " + player1.getName()));
        this.addConfirmationButtons(this.player1Inventory);
        this.addConfirmationButtons(this.player2Inventory);
        this.start();
    }

    private void addConfirmationButtons(Inventory inventory) {
        ItemStack acceptButton = new ItemStack(Material.GREEN_WOOL);
        ItemMeta acceptMeta = acceptButton.getItemMeta();
        acceptMeta.setDisplayName(String.valueOf(ChatColor.GREEN) + "Handel best\u00e4tigen");
        acceptButton.setItemMeta(acceptMeta);
        inventory.setItem(33, acceptButton);
        ItemStack declineButton = new ItemStack(Material.RED_WOOL);
        ItemMeta declineMeta = declineButton.getItemMeta();
        declineMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Handel abbrechen");
        declineButton.setItemMeta(declineMeta);
        inventory.setItem(29, declineButton);
    }

    public void start() {
        this.player1.openInventory(this.player1Inventory);
        this.player2.openInventory(this.player2Inventory);
        this.player1.sendMessage("Handel gestartet mit " + this.player2.getName() + ". Lege deine Items und Geld in das Inventar.");
        this.player2.sendMessage("Handel gestartet mit " + this.player1.getName() + ". Lege deine Items und Geld in das Inventar.");
    }

    public void handleClick(Player player, int slot) {
        if (slot == 33) {
            this.confirmTrade(player);
        } else if (slot == 29) {
            this.cancelTrade();
        }
    }

    public void confirmTrade(Player player) {
        if (player.equals((Object)this.player1)) {
            this.player1Ready = true;
            this.player1.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast den Handel best\u00e4tigt. Warte auf " + this.player2.getName() + ".");
        } else if (player.equals((Object)this.player2)) {
            this.player2Ready = true;
            this.player2.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast den Handel best\u00e4tigt. Warte auf " + this.player1.getName() + ".");
        }
        if (this.player1Ready && this.player2Ready) {
            this.completeTrade();
        }
    }

    private void completeTrade() {
        for (ItemStack item : this.player1Inventory.getContents()) {
            if (item == null || item.getType() == Material.GREEN_WOOL || item.getType() == Material.RED_WOOL) continue;
            this.player2.getInventory().addItem(new ItemStack[]{item});
        }
        for (ItemStack item : this.player2Inventory.getContents()) {
            if (item == null || item.getType() == Material.GREEN_WOOL || item.getType() == Material.RED_WOOL) continue;
            this.player1.getInventory().addItem(new ItemStack[]{item});
        }
        this.economyAPI.addBalance(this.player1, this.player2Money);
        this.economyAPI.addBalance(this.player2, this.player1Money);
        this.player1.sendMessage(String.valueOf(ChatColor.GREEN) + "Handel erfolgreich abgeschlossen!");
        this.player2.sendMessage(String.valueOf(ChatColor.GREEN) + "Handel erfolgreich abgeschlossen!");
        this.player1.closeInventory();
        this.player2.closeInventory();
        this.tradeManager.removeTradeSession(this.player1);
        this.tradeManager.removeTradeSession(this.player2);
    }

    public void cancelTrade() {
        for (ItemStack item : this.player1Inventory.getContents()) {
            if (item == null || item.getType() == Material.GREEN_WOOL || item.getType() == Material.RED_WOOL) continue;
            this.player1.getInventory().addItem(new ItemStack[]{item});
        }
        for (ItemStack item : this.player2Inventory.getContents()) {
            if (item == null || item.getType() == Material.GREEN_WOOL || item.getType() == Material.RED_WOOL) continue;
            this.player2.getInventory().addItem(new ItemStack[]{item});
        }
        this.economyAPI.addBalance(this.player1, this.player1Money);
        this.economyAPI.addBalance(this.player2, this.player2Money);
        this.player1.sendMessage(String.valueOf(ChatColor.RED) + "Handel abgebrochen!");
        this.player2.sendMessage(String.valueOf(ChatColor.RED) + "Handel abgebrochen!");
        this.player1.closeInventory();
        this.player2.closeInventory();
        this.tradeManager.removeTradeSession(this.player1);
        this.tradeManager.removeTradeSession(this.player2);
    }

    public Inventory getPlayer1Inventory() {
        return this.player1Inventory;
    }

    public Inventory getPlayer2Inventory() {
        return this.player2Inventory;
    }
}

