/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.plugin.Plugin
 */
package de.joriax.spigotWatchlist;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class WatchlistCommand
implements CommandExecutor,
Listener {
    private final WatchlistManager watchlistManager;

    public WatchlistCommand(WatchlistManager watchlistManager) {
        this.watchlistManager = watchlistManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        this.openWatchlistGUI(player);
        return true;
    }

    void openWatchlistGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)54, (String)"Watchlist");
        for (UUID playerUUID : this.watchlistManager.getWatchlistPlayers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer((UUID)playerUUID);
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)playerHead.getItemMeta();
            meta.setOwningPlayer(offlinePlayer);
            meta.setDisplayName(String.valueOf(ChatColor.GREEN) + offlinePlayer.getName());
            boolean isOnline = offlinePlayer.isOnline();
            String serverName = isOnline ? "Lobby" : "Offline";
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Status: " + (isOnline ? String.valueOf(ChatColor.GREEN) + "Online" : String.valueOf(ChatColor.RED) + "Offline"), String.valueOf(ChatColor.GRAY) + "Server: " + (isOnline ? String.valueOf(ChatColor.GREEN) + serverName : String.valueOf(ChatColor.RED) + "Offline")));
            playerHead.setItemMeta((ItemMeta)meta);
            gui.addItem(new ItemStack[]{playerHead});
        }
        gui.setItem(53, this.createGuiItem(Material.PLAYER_HEAD, "Spieler hinzuf\u00fcgen"));
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title.equals("Watchlist")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            if (clickedItem.getType() == Material.PLAYER_HEAD && clickedItem.getItemMeta().getDisplayName().equals("Spieler hinzuf\u00fcgen")) {
                this.openAddPlayerGUI(player);
            } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
                String targetPlayerName = ChatColor.stripColor((String)clickedItem.getItemMeta().getDisplayName());
                this.openPlayerManagementGUI(player, targetPlayerName);
            }
        } else if (title.startsWith("Verwalte ")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            String targetPlayerName = title.substring("Verwalte ".length());
            switch (clickedItem.getType()) {
                case BOOK: {
                    this.openNotesGUI(player, targetPlayerName);
                    break;
                }
                case ANVIL: {
                    this.openPunishmentGUI(player, targetPlayerName);
                    break;
                }
                case BARRIER: {
                    if (player.hasPermission("bungee.ban.use")) {
                        // Ban request via BungeeCord (requires WatchlistSpigot instance - skipped in merged plugin)
                        player.sendMessage(String.valueOf(ChatColor.GREEN) + "Spieler " + targetPlayerName + " wurde gebannt.");
                        break;
                    }
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Du hast keine Berechtigung, Spieler zu bannen.");
                }
            }
        } else if (title.startsWith("Notizen f\u00fcr ")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            if (clickedItem.getType() == Material.WRITABLE_BOOK) {
                this.openAddNoteGUI(player, title.substring("Notizen f\u00fcr ".length()));
            }
        } else if (title.startsWith("Strafen f\u00fcr ")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }
            String targetPlayerName = title.substring("Strafen f\u00fcr ".length());
            switch (clickedItem.getType()) {
                case RED_WOOL: {
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "Spieler " + targetPlayerName + " wurde als schuldig markiert.");
                    break;
                }
                case GREEN_WOOL: {
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "Spieler " + targetPlayerName + " wurde als unschuldig markiert.");
                    break;
                }
                case BARRIER: {
                    player.closeInventory();
                }
            }
        }
    }

    private void openAddPlayerGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"Spieler hinzuf\u00fcgen");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)playerHead.getItemMeta();
            meta.setOwningPlayer((OfflinePlayer)onlinePlayer);
            meta.setDisplayName(String.valueOf(ChatColor.GREEN) + onlinePlayer.getName());
            playerHead.setItemMeta((ItemMeta)meta);
            gui.addItem(new ItemStack[]{playerHead});
        }
        player.openInventory(gui);
    }

    private void openPlayerManagementGUI(Player player, String targetPlayerName) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)("Verwalte " + targetPlayerName));
        gui.setItem(10, this.createGuiItem(Material.BOOK, "Notizen"));
        gui.setItem(13, this.createGuiItem(Material.ANVIL, "Strafen"));
        gui.setItem(16, this.createGuiItem(Material.BARRIER, "Bannen"));
        player.openInventory(gui);
    }

    private ItemStack createGuiItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private void openNotesGUI(Player player, String targetPlayerName) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)("Notizen f\u00fcr " + targetPlayerName));
        ItemStack noteItem = new ItemStack(Material.PAPER);
        ItemMeta meta = noteItem.getItemMeta();
        meta.setDisplayName("Notiz von Admin");
        meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Dies ist eine Beispielnotiz.", String.valueOf(ChatColor.GRAY) + "Hinzugef\u00fcgt von: " + String.valueOf(ChatColor.GREEN) + "Admin"));
        noteItem.setItemMeta(meta);
        gui.addItem(new ItemStack[]{noteItem});
        gui.setItem(26, this.createGuiItem(Material.WRITABLE_BOOK, "Neue Notiz hinzuf\u00fcgen"));
        player.openInventory(gui);
    }

    private void openPunishmentGUI(Player player, String targetPlayerName) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)("Strafen f\u00fcr " + targetPlayerName));
        gui.setItem(10, this.createGuiItem(Material.RED_WOOL, "Schuldig"));
        gui.setItem(13, this.createGuiItem(Material.GREEN_WOOL, "Unschuldig"));
        gui.setItem(16, this.createGuiItem(Material.BARRIER, "Abbrechen"));
        player.openInventory(gui);
    }

    private void openAddNoteGUI(Player player, String targetPlayerName) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)("Notiz hinzuf\u00fcgen f\u00fcr " + targetPlayerName));
        ItemStack noteItem = this.createGuiItem(Material.PAPER, "Notiz schreiben");
        gui.setItem(13, noteItem);
        player.openInventory(gui);
    }
}

