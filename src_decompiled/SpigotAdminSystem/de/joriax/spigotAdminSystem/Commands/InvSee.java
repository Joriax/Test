/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem.Commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class InvSee
implements Listener,
CommandExecutor {
    private final JavaPlugin plugin;
    private File logDir;
    private File logFile;
    private Logger logger;
    private FileHandler logHandler;
    private boolean loggingEnabled = true;
    private boolean notificationsEnabled = true;
    private final Map<UUID, Long> inventoryOpenTimes = new ConcurrentHashMap<UUID, Long>();
    private final Map<UUID, Inventory> activeInventories = new ConcurrentHashMap<UUID, Inventory>();
    private final Map<UUID, Boolean> clearLogConfirmations = new HashMap<UUID, Boolean>();
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6InvSee&8] &r");

    public InvSee(JavaPlugin plugin) {
        this.plugin = plugin;
        this.setupLogging();
    }

    private void setupLogging() {
        this.logger = Logger.getLogger("InvSeeLogger");
        try {
            this.logDir = new File(this.plugin.getDataFolder(), "invseelog");
            if (!this.logDir.exists()) {
                this.logDir.mkdirs();
            }
            this.logFile = new File(this.logDir, "invsee.log");
            this.logHandler = new FileHandler(this.logFile.getPath(), 1000000, 1, true);
            this.logHandler.setFormatter(new SimpleFormatter(this){
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] %s: %s%n", this.dateFormat.format(new Date(record.getMillis())), record.getLevel().getName(), record.getMessage());
                }
            });
            this.logger.addHandler(this.logHandler);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Failed to setup InvSee logging: " + e.getMessage());
        }
    }

    private void logAction(String action, String actor) {
        if (!this.loggingEnabled) {
            return;
        }
        String logMessage = String.format("%s: %s", action, actor);
        this.logger.info(logMessage);
        if (this.notificationsEnabled) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("invsee.notify")) continue;
                p.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + "[LOG] " + logMessage);
            }
        }
    }

    private void clearLogFile(Player player) {
        try {
            if (this.logHandler != null) {
                this.logHandler.close();
                this.logger.removeHandler(this.logHandler);
            }
            this.logDir = new File(this.plugin.getDataFolder(), "invseelog");
            if (!this.logDir.exists()) {
                this.logDir.mkdirs();
            }
            this.logFile = new File(this.logDir, "invsee.log");
            if (this.logFile.exists()) {
                if (!this.logFile.delete()) {
                    this.plugin.getLogger().warning("Failed to delete InvSee log file: " + this.logFile.getName());
                } else {
                    this.plugin.getLogger().info("Successfully deleted InvSee log file: " + this.logFile.getName());
                }
            }
            this.logFile.createNewFile();
            this.logHandler = new FileHandler(this.logFile.getPath(), 1000000, 1, true);
            this.logHandler.setFormatter(new SimpleFormatter(this){
                private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] %s: %s%n", this.dateFormat.format(new Date(record.getMillis())), record.getLevel().getName(), record.getMessage());
                }
            });
            this.logger.addHandler(this.logHandler);
            player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "InvSee log file cleared.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            this.logAction("Clear Log", player.getName() + " cleared the InvSee log file");
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Failed to clear InvSee log file: " + e.getMessage());
            player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Failed to clear InvSee log file: " + e.getMessage());
        }
    }

    private boolean isBlacklistedItem(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        String displayName = ChatColor.stripColor((String)item.getItemMeta().getDisplayName());
        List blacklist = this.plugin.getConfig().getStringList("item-blacklist");
        return blacklist.stream().anyMatch(blacklisted -> ChatColor.stripColor((String)ChatColor.translateAlternateColorCodes((char)'&', (String)blacklisted)).equals(displayName));
    }

    private String getItemDisplayName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "Nothing";
        }
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().name();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command!");
            return true;
        }
        Player player = (Player)sender;
        if (command.getName().equalsIgnoreCase("invsee")) {
            if (!player.hasPermission("invsee.see")) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to use this command!");
                return true;
            }
            if (args.length != 1) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Usage: /invsee <player>");
                return true;
            }
            Player target = Bukkit.getPlayer((String)args[0]);
            if (target == null) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Player not found!");
                return true;
            }
            this.openPlayerInventory(player, target);
            this.logAction("Inventory Opened", String.format("%s opened inventory of %s", player.getName(), target.getName()));
            return true;
        }
        if (command.getName().equalsIgnoreCase("invseeadmin")) {
            if (!player.hasPermission("invsee.admin")) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            this.openAdminMenu(player);
            return true;
        }
        return false;
    }

    private void openPlayerInventory(Player viewer, Player target) {
        int i;
        Inventory inv = Bukkit.createInventory(null, (int)54, (String)("Inventory: " + target.getName()));
        for (i = 0; i < 9; ++i) {
            inv.setItem(i, target.getInventory().getItem(i + 27));
        }
        for (i = 0; i < 27; ++i) {
            inv.setItem(i + 18, target.getInventory().getItem(i));
        }
        inv.setItem(45, target.getInventory().getHelmet());
        inv.setItem(46, target.getInventory().getChestplate());
        inv.setItem(47, target.getInventory().getLeggings());
        inv.setItem(48, target.getInventory().getBoots());
        inv.setItem(49, target.getInventory().getItemInOffHand());
        this.activeInventories.put(viewer.getUniqueId(), inv);
        viewer.openInventory(inv);
        this.inventoryOpenTimes.put(viewer.getUniqueId(), System.currentTimeMillis());
    }

    private void openAdminMenu(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"InvSee Admin");
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; ++i) {
            gui.setItem(i, filler);
        }
        ItemStack loggingItem = new ItemStack(this.loggingEnabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta loggingMeta = loggingItem.getItemMeta();
        loggingMeta.setDisplayName(this.loggingEnabled ? String.valueOf(ChatColor.GREEN) + "Logging Enabled" : String.valueOf(ChatColor.RED) + "Logging Disabled");
        loggingMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to toggle logging"));
        loggingItem.setItemMeta(loggingMeta);
        gui.setItem(10, loggingItem);
        ItemStack notifyItem = new ItemStack(this.notificationsEnabled ? Material.BELL : Material.BARRIER);
        ItemMeta notifyMeta = notifyItem.getItemMeta();
        notifyMeta.setDisplayName(this.notificationsEnabled ? String.valueOf(ChatColor.GREEN) + "Notifications Enabled" : String.valueOf(ChatColor.RED) + "Notifications Disabled");
        notifyMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to toggle notifications"));
        notifyItem.setItemMeta(notifyMeta);
        gui.setItem(14, notifyItem);
        ItemStack clearItem = new ItemStack(Material.REDSTONE);
        ItemMeta clearMeta = clearItem.getItemMeta();
        clearMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Clear Logs");
        clearMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to clear all logs"));
        clearItem.setItemMeta(clearMeta);
        gui.setItem(16, clearItem);
        ItemStack viewLogsItem = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta viewLogsMeta = viewLogsItem.getItemMeta();
        viewLogsMeta.setDisplayName(String.valueOf(ChatColor.YELLOW) + "View Logs");
        viewLogsMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to view recent actions"));
        viewLogsItem.setItemMeta(viewLogsMeta);
        gui.setItem(18, viewLogsItem);
        player.openInventory(gui);
    }

    private void openClearLogConfirmGui(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"Confirm Clear Logs");
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; ++i) {
            gui.setItem(i, filler);
        }
        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(String.valueOf(ChatColor.GREEN) + "[ACCEPT]");
        confirmMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to confirm clearing logs"));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(12, confirm);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(String.valueOf(ChatColor.RED) + "[CANCEL]");
        cancelMeta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Click to cancel"));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(14, cancel);
        this.clearLogConfirmations.put(player.getUniqueId(), true);
        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private void openLogViewer(Player player, int page) {
        List<String> logEntries = this.readLogEntries();
        int entriesPerPage = 45;
        int totalPages = (int)Math.ceil((double)logEntries.size() / (double)entriesPerPage);
        totalPages = Math.max(1, totalPages);
        page = Math.max(0, Math.min(page, totalPages - 1));
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)54, (String)("Log Viewer (Page " + (page + 1) + "/" + totalPages + ")"));
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 45; i < 54; ++i) {
            gui.setItem(i, filler);
        }
        if (!logEntries.isEmpty()) {
            int startIndex = page * entriesPerPage;
            int endIndex = Math.min(startIndex + entriesPerPage, logEntries.size());
            for (int i = startIndex; i < endIndex; ++i) {
                String[] parts;
                String entry = logEntries.get(i);
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(String.valueOf(ChatColor.YELLOW) + "Action #" + (i + 1));
                ArrayList<CallSite> lore = new ArrayList<CallSite>();
                for (String part : parts = entry.split("(?<=\\G.{40})")) {
                    lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + part.trim())));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
                gui.setItem(i - startIndex, item);
            }
            if (page > 0) {
                ItemStack prev = new ItemStack(Material.PAPER);
                ItemMeta prevMeta = prev.getItemMeta();
                prevMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Previous Page");
                prev.setItemMeta(prevMeta);
                gui.setItem(45, prev);
            }
            if (page < totalPages - 1 && logEntries.size() > (page + 1) * entriesPerPage) {
                ItemStack next = new ItemStack(Material.PAPER);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName(String.valueOf(ChatColor.GREEN) + "Next Page");
                next.setItemMeta(nextMeta);
                gui.setItem(53, next);
            }
        }
        player.openInventory(gui);
    }

    private List<String> readLogEntries() {
        ArrayList<String> entries = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(this.logFile));){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                entries.add(0, line);
            }
        }
        catch (IOException e) {
            this.plugin.getLogger().warning("Failed to read InvSee log file: " + e.getMessage());
        }
        return entries;
    }

    private void toggleLogging(Player player) {
        this.loggingEnabled = !this.loggingEnabled;
        player.sendMessage(this.prefix + (this.loggingEnabled ? String.valueOf(ChatColor.GREEN) + "Logging enabled!" : String.valueOf(ChatColor.RED) + "Logging disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Logging Toggle", player.getName() + " set to " + (this.loggingEnabled ? "on" : "off"));
        this.openAdminMenu(player);
    }

    private void toggleNotifications(Player player) {
        this.notificationsEnabled = !this.notificationsEnabled;
        player.sendMessage(this.prefix + (this.notificationsEnabled ? String.valueOf(ChatColor.GREEN) + "Notifications enabled!" : String.valueOf(ChatColor.RED) + "Notifications disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Notifications Toggle", player.getName() + " set to " + (this.notificationsEnabled ? "on" : "off"));
        this.openAdminMenu(player);
    }

    private void syncInventoryToTarget(Inventory inv, Player target) {
        int i;
        for (i = 0; i < 9; ++i) {
            target.getInventory().setItem(i + 27, inv.getItem(i));
        }
        for (i = 0; i < 27; ++i) {
            target.getInventory().setItem(i, inv.getItem(i + 18));
        }
        target.getInventory().setHelmet(inv.getItem(45));
        target.getInventory().setChestplate(inv.getItem(46));
        target.getInventory().setLeggings(inv.getItem(47));
        target.getInventory().setBoots(inv.getItem(48));
        target.getInventory().setItemInOffHand(inv.getItem(49));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String displayName;
        ItemStack clickedItem;
        Player player = (Player)event.getWhoClicked();
        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();
        ClickType clickType = event.getClick();
        if (title.startsWith("Inventory: ")) {
            if (!player.hasPermission("invsee.see")) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "No permission to view inventory!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            if (!player.hasPermission("invsee.edit")) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "No permission to edit inventory!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            if (event.getClickedInventory() != null && event.getClickedInventory().equals((Object)player.getInventory())) {
                return;
            }
            if (event.getClickedInventory() == null) {
                return;
            }
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();
            if (this.isBlacklistedItem(currentItem) || this.isBlacklistedItem(cursorItem)) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "This item is blacklisted and cannot be modified!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            String targetName = title.replace("Inventory: ", "");
            Player target = Bukkit.getPlayer((String)targetName);
            if (target != null) {
                ItemStack[] beforeInv = (ItemStack[])inv.getContents().clone();
                ItemStack[] beforePlayerInv = (ItemStack[])player.getInventory().getContents().clone();
                Bukkit.getScheduler().runTaskLater((Plugin)this.plugin, () -> {
                    int amountDiff;
                    ItemStack after;
                    ItemStack before;
                    int i;
                    ItemStack[] afterInv = inv.getContents();
                    ItemStack[] afterPlayerInv = player.getInventory().getContents();
                    for (i = 0; i < beforeInv.length; ++i) {
                        before = beforeInv[i];
                        after = afterInv[i];
                        if (before == null && after != null && after.getType() != Material.AIR) {
                            this.logAction("Item Placed", String.format("%s placed %dx%s in %s's inventory (slot %d)", player.getName(), after.getAmount(), this.getItemDisplayName(after), targetName, i));
                            continue;
                        }
                        if (before != null && before.getType() != Material.AIR && after == null) {
                            this.logAction("Item Taken", String.format("%s took %dx%s from %s's inventory (slot %d)", player.getName(), before.getAmount(), this.getItemDisplayName(before), targetName, i));
                            continue;
                        }
                        if (before != null && after != null && !before.isSimilar(after)) {
                            this.logAction("Item Taken", String.format("%s took %dx%s from %s's inventory (slot %d)", player.getName(), before.getAmount(), this.getItemDisplayName(before), targetName, i));
                            this.logAction("Item Placed", String.format("%s placed %dx%s in %s's inventory (slot %d)", player.getName(), after.getAmount(), this.getItemDisplayName(after), targetName, i));
                            continue;
                        }
                        if (before == null || after == null || before.getAmount() == after.getAmount()) continue;
                        amountDiff = after.getAmount() - before.getAmount();
                        if (amountDiff > 0) {
                            this.logAction("Item Placed", String.format("%s placed %dx%s in %s's inventory (slot %d)", player.getName(), amountDiff, this.getItemDisplayName(after), targetName, i));
                            continue;
                        }
                        this.logAction("Item Taken", String.format("%s took %dx%s from %s's inventory (slot %d)", player.getName(), -amountDiff, this.getItemDisplayName(before), targetName, i));
                    }
                    for (i = 0; i < beforePlayerInv.length; ++i) {
                        before = beforePlayerInv[i];
                        after = afterPlayerInv[i];
                        if (before != null && before.getType() != Material.AIR && after == null) {
                            this.logAction("Item Placed", String.format("%s placed %dx%s in %s's inventory (shift-click)", player.getName(), before.getAmount(), this.getItemDisplayName(before), targetName));
                            continue;
                        }
                        if (before == null && after != null && after.getType() != Material.AIR) {
                            this.logAction("Item Taken", String.format("%s took %dx%s from %s's inventory (shift-click)", player.getName(), after.getAmount(), this.getItemDisplayName(after), targetName));
                            continue;
                        }
                        if (before != null && after != null && before.getAmount() > after.getAmount()) {
                            amountDiff = before.getAmount() - after.getAmount();
                            this.logAction("Item Placed", String.format("%s placed %dx%s in %s's inventory (shift-click)", player.getName(), amountDiff, this.getItemDisplayName(before), targetName));
                            continue;
                        }
                        if (before == null || after == null || before.getAmount() >= after.getAmount()) continue;
                        amountDiff = after.getAmount() - before.getAmount();
                        this.logAction("Item Taken", String.format("%s took %dx%s from %s's inventory (shift-click)", player.getName(), amountDiff, this.getItemDisplayName(after), targetName));
                    }
                    this.syncInventoryToTarget(inv, target);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.2f);
                }, 1L);
            }
        }
        if (title.equals("InvSee Admin")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(String.valueOf(ChatColor.GREEN) + "Logging Enabled") || displayName.equals(String.valueOf(ChatColor.RED) + "Logging Disabled")) {
                if (player.hasPermission("invsee.admin.logging")) {
                    this.toggleLogging(player);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to toggle logging!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals(String.valueOf(ChatColor.GREEN) + "Notifications Enabled") || displayName.equals(String.valueOf(ChatColor.RED) + "Notifications Disabled")) {
                if (player.hasPermission("invsee.admin.notifications")) {
                    this.toggleNotifications(player);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to toggle notifications!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals(String.valueOf(ChatColor.RED) + "Clear Logs")) {
                if (player.hasPermission("invsee.admin.clearlogs")) {
                    this.openClearLogConfirmGui(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to clear logs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals(String.valueOf(ChatColor.YELLOW) + "View Logs")) {
                if (player.hasPermission("invsee.admin.viewlogs")) {
                    this.openLogViewer(player, 0);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to view logs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            }
        }
        if (title.equals("Confirm Clear Logs")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(String.valueOf(ChatColor.GREEN) + "[ACCEPT]")) {
                if (player.hasPermission("invsee.admin.clearlogs")) {
                    this.clearLogFile(player);
                    this.clearLogConfirmations.remove(player.getUniqueId());
                    player.closeInventory();
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to clear logs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals(String.valueOf(ChatColor.RED) + "[CANCEL]")) {
                this.clearLogConfirmations.remove(player.getUniqueId());
                player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Log file clear cancelled.");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                this.logAction("Clear Log Cancel", player.getName());
                player.closeInventory();
                this.openAdminMenu(player);
            }
        }
        if (title.startsWith("Log Viewer (")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(String.valueOf(ChatColor.GREEN) + "Next Page") || displayName.equals(String.valueOf(ChatColor.RED) + "Previous Page")) {
                try {
                    int currentPage = Integer.parseInt(title.split("\\(")[1].split("/")[0].replace("Page ", "")) - 1;
                    int newPage = displayName.equals(String.valueOf(ChatColor.GREEN) + "Next Page") ? currentPage + 1 : currentPage - 1;
                    this.openLogViewer(player, newPage);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                }
                catch (NumberFormatException e) {
                    this.plugin.getLogger().warning("Failed to parse page number from title: " + title);
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Error navigating pages. Please try again.");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv;
        Player player = (Player)event.getPlayer();
        String title = event.getView().getTitle();
        if (title.startsWith("Inventory: ") && (inv = this.activeInventories.get(player.getUniqueId())) != null && inv.equals((Object)event.getInventory())) {
            String targetName = title.replace("Inventory: ", "");
            Player target = Bukkit.getPlayer((String)targetName);
            if (target != null) {
                this.syncInventoryToTarget(inv, target);
                long openTime = this.inventoryOpenTimes.getOrDefault(player.getUniqueId(), 0L);
                long duration = System.currentTimeMillis() - openTime;
                this.logAction("Inventory Closed", String.format("%s closed inventory of %s after %d ms", player.getName(), targetName, duration));
            }
            this.activeInventories.remove(player.getUniqueId());
            this.inventoryOpenTimes.remove(player.getUniqueId());
        }
    }
}

