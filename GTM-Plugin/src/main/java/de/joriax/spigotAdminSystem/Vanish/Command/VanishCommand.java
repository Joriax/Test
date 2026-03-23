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
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.Vector
 */
package de.joriax.spigotAdminSystem.Vanish.Command;

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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class VanishCommand
implements CommandExecutor,
Listener {
    private final org.bukkit.plugin.java.JavaPlugin plugin;
    private File configFile;
    private YamlConfiguration config;
    private boolean loggingEnabled = true;
    private boolean announcementsEnabled = true;
    private File logDir;
    private File logFile;
    private Logger logger;
    private FileHandler logHandler;
    private final Map<UUID, Boolean> clearLogConfirmations = new HashMap<UUID, Boolean>();
    private final Map<UUID, Boolean> vanishedPlayers = new HashMap<UUID, Boolean>();
    private final String prefix = ChatColor.translateAlternateColorCodes((char)'&', (String)"&8[&6Vanish&8] &r");

    public VanishCommand(org.bukkit.plugin.java.JavaPlugin plugin) {
        this.plugin = plugin;
        this.setupConfig();
        this.setupLogging();
    }

    private void setupConfig() {
        this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }
        if (!this.configFile.exists()) {
            this.plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)this.configFile);
        this.loggingEnabled = this.config.getBoolean("vanish.enable-logging", true);
        this.announcementsEnabled = this.config.getBoolean("vanish.enable-announcements", true);
    }

    private void setupLogging() {
        this.logger = Logger.getLogger("VanishLogger");
        try {
            this.logDir = new File(this.plugin.getDataFolder(), "vanishlog");
            if (!this.logDir.exists()) {
                this.logDir.mkdirs();
            }
            this.logFile = new File(this.logDir, "vanish.log");
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
            this.plugin.getLogger().severe("Failed to setup Vanish logging: " + e.getMessage());
        }
    }

    public void logAction(String action, String details) {
        if (!this.loggingEnabled) {
            return;
        }
        String logMessage = String.format("%s: %s", action, details);
        this.logger.info(logMessage);
        if (this.announcementsEnabled) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("spigot.vanish.admin.announcements")) continue;
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
            this.logDir = new File(this.plugin.getDataFolder(), "vanishlog");
            if (!this.logDir.exists()) {
                this.logDir.mkdirs();
            }
            this.logFile = new File(this.logDir, "vanish.log");
            if (this.logFile.exists()) {
                if (!this.logFile.delete()) {
                    this.plugin.getLogger().warning("Failed to delete Vanish log file: " + this.logFile.getName());
                } else {
                    this.plugin.getLogger().info("Successfully deleted Vanish log file: " + this.logFile.getName());
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
            player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "Vanish log file cleared.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            this.logAction("Clear Log", player.getName() + " cleared the Vanish log file");
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Failed to clear Vanish log file: " + e.getMessage());
            player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Failed to clear Vanish log file: " + e.getMessage());
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("vanish")) {
            if (!player.hasPermission("spigot.vanish.use")) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                return true;
            }
            if (this.isVanished(player)) {
                this.unvanishPlayer(player);
                player.setAllowFlight(false);
                player.setInvulnerable(false);
                player.setHealth(20.0);
                player.setSaturation(20.0f);
                player.setFoodLevel(20);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "You are no longer vanished.");
            } else {
                this.vanishPlayer(player);
                player.setAllowFlight(true);
                player.setInvulnerable(true);
                player.setHealth(20.0);
                player.setSaturation(20.0f);
                player.setFoodLevel(20);
                player.setVelocity(new Vector(0, 0, 0));
                player.setNoDamageTicks(0);
                player.sendMessage(this.prefix + String.valueOf(ChatColor.GREEN) + "You are now vanished.");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("vanishadmin")) {
            if (!player.hasPermission("spigot.vanish.admin.gui")) {
                player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            this.openAdminGui(player);
            return true;
        }
        return false;
    }

    private void vanishPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("spigot.vanish.see")) {
                p.hidePlayer((Plugin)this.plugin, player);
            }
            if (!p.hasPermission("spigot.vanish.notify")) continue;
            p.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + player.getName() + " is now in Vanish.");
        }
        this.vanishedPlayers.put(player.getUniqueId(), true);
        this.logAction("Vanish Enable", player.getName() + " entered Vanish mode");
    }

    private void unvanishPlayer(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer((Plugin)this.plugin, player);
            if (!p.hasPermission("spigot.vanish.notify")) continue;
            p.sendMessage(this.prefix + String.valueOf(ChatColor.GRAY) + player.getName() + " is no longer in Vanish.");
        }
        this.vanishedPlayers.remove(player.getUniqueId());
        this.logAction("Vanish Disable", player.getName() + " exited Vanish mode");
    }

    private boolean isVanished(Player player) {
        return this.vanishedPlayers.getOrDefault(player.getUniqueId(), false);
    }

    private void openAdminGui(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"Vanish Admin");
        ItemStack logs = new ItemStack(this.loggingEnabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta logsMeta = logs.getItemMeta();
        logsMeta.setDisplayName(this.loggingEnabled ? "\u00a7aLogging Enabled" : "\u00a7cLogging Disabled");
        logsMeta.setLore(Arrays.asList("\u00a77Click to toggle logging"));
        logs.setItemMeta(logsMeta);
        gui.setItem(10, logs);
        ItemStack stats = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName("\u00a7eView Statistics");
        statsMeta.setLore(Arrays.asList("\u00a77Click to view vanish statistics"));
        stats.setItemMeta(statsMeta);
        gui.setItem(12, stats);
        ItemStack announcements = new ItemStack(this.announcementsEnabled ? Material.BELL : Material.BARRIER);
        ItemMeta announcementsMeta = announcements.getItemMeta();
        announcementsMeta.setDisplayName(this.announcementsEnabled ? "\u00a7aAnnouncements Enabled" : "\u00a7cAnnouncements Disabled");
        announcementsMeta.setLore(Arrays.asList("\u00a77Click to toggle admin notifications"));
        announcements.setItemMeta(announcementsMeta);
        gui.setItem(14, announcements);
        ItemStack clearLogs = new ItemStack(Material.REDSTONE);
        ItemMeta clearLogsMeta = clearLogs.getItemMeta();
        clearLogsMeta.setDisplayName("\u00a7cClear Logs");
        clearLogsMeta.setLore(Arrays.asList("\u00a77Click to clear all log files"));
        clearLogs.setItemMeta(clearLogsMeta);
        gui.setItem(16, clearLogs);
        ItemStack viewLogs = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta viewLogsMeta = viewLogs.getItemMeta();
        viewLogsMeta.setDisplayName("\u00a7eView Logs");
        viewLogsMeta.setLore(Arrays.asList("\u00a77Click to view recent actions"));
        viewLogs.setItemMeta(viewLogsMeta);
        gui.setItem(18, viewLogs);
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; ++i) {
            if (i == 10 || i == 12 || i == 14 || i == 16 || i == 18) continue;
            gui.setItem(i, filler);
        }
        player.openInventory(gui);
    }

    private void openClearLogConfirmGui(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"Confirm Clear Logs");
        ItemStack accept = new ItemStack(Material.GREEN_WOOL);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName("\u00a7a[ACCEPT]");
        acceptMeta.setLore(Arrays.asList("\u00a77Click to confirm clearing logs"));
        accept.setItemMeta(acceptMeta);
        gui.setItem(12, accept);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("\u00a7c[CANCEL]");
        cancelMeta.setLore(Arrays.asList("\u00a77Click to cancel"));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(14, cancel);
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; ++i) {
            if (i == 12 || i == 14) continue;
            gui.setItem(i, filler);
        }
        this.clearLogConfirmations.put(player.getUniqueId(), true);
        player.openInventory(gui);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private void openLogViewGui(Player player, int page) {
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
                ItemStack paper = new ItemStack(Material.PAPER);
                ItemMeta paperMeta = paper.getItemMeta();
                paperMeta.setDisplayName("\u00a7eAction #" + (i + 1));
                ArrayList<CallSite> lore = new ArrayList<CallSite>();
                for (String part : parts = entry.split("(?<=\\G.{40})")) {
                    lore.add((CallSite)((Object)("\u00a77" + part.trim())));
                }
                paperMeta.setLore(lore);
                paper.setItemMeta(paperMeta);
                gui.setItem(i - startIndex, paper);
            }
            if (page > 0) {
                ItemStack prev = new ItemStack(Material.PAPER);
                ItemMeta prevMeta = prev.getItemMeta();
                prevMeta.setDisplayName("\u00a7cPrevious Page");
                prev.setItemMeta(prevMeta);
                gui.setItem(45, prev);
            }
            if (page < totalPages - 1 && logEntries.size() > (page + 1) * entriesPerPage) {
                ItemStack next = new ItemStack(Material.PAPER);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName("\u00a7aNext Page");
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
            this.plugin.getLogger().warning("Failed to read Vanish log file: " + e.getMessage());
        }
        return entries;
    }

    private void showStats(Player player) {
        int vanishedPlayersCount = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!this.isVanished(p)) continue;
            ++vanishedPlayersCount;
        }
        player.sendMessage(this.prefix + String.valueOf(ChatColor.DARK_AQUA) + "\u2550\u2550\u2550\u2550\u2550 Vanish Statistics \u2550\u2550\u2550\u2550\u2550");
        player.sendMessage(this.prefix + String.valueOf(ChatColor.AQUA) + "\u00bb Total Online Players: " + String.valueOf(ChatColor.WHITE) + Bukkit.getOnlinePlayers().size());
        player.sendMessage(this.prefix + String.valueOf(ChatColor.AQUA) + "\u00bb Vanished Players: " + String.valueOf(ChatColor.WHITE) + vanishedPlayersCount);
        player.sendMessage(this.prefix + String.valueOf(ChatColor.DARK_AQUA) + "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        this.logAction("Stats Viewed", player.getName());
    }

    private void toggleLogging(Player player) {
        this.loggingEnabled = !this.loggingEnabled;
        this.config.set("vanish.enable-logging", (Object)this.loggingEnabled);
        try {
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
        player.sendMessage(this.prefix + (this.loggingEnabled ? String.valueOf(ChatColor.GREEN) + "Logging enabled!" : String.valueOf(ChatColor.RED) + "Logging disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Logging Toggle", player.getName() + " set to " + (this.loggingEnabled ? "on" : "off"));
        this.openAdminGui(player);
    }

    private void toggleAnnouncements(Player player) {
        this.announcementsEnabled = !this.announcementsEnabled;
        this.config.set("vanish.enable-announcements", (Object)this.announcementsEnabled);
        try {
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            this.plugin.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
        player.sendMessage(this.prefix + (this.announcementsEnabled ? String.valueOf(ChatColor.GREEN) + "Announcements enabled!" : String.valueOf(ChatColor.RED) + "Announcements disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Announcements Toggle", player.getName() + " set to " + (this.announcementsEnabled ? "on" : "off"));
        this.openAdminGui(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String displayName;
        ItemStack clickedItem;
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title.equals("Vanish Admin")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals("\u00a7aLogging Enabled") || displayName.equals("\u00a7cLogging Disabled")) {
                if (player.hasPermission("spigot.vanish.admin.logging")) {
                    this.toggleLogging(player);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to toggle logging!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals("\u00a7aAnnouncements Enabled") || displayName.equals("\u00a7cAnnouncements Disabled")) {
                if (player.hasPermission("spigot.vanish.admin.announcements")) {
                    this.toggleAnnouncements(player);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to toggle announcements!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals("\u00a7eView Statistics")) {
                if (player.hasPermission("spigot.vanish.admin.stats")) {
                    player.closeInventory();
                    this.showStats(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to view statistics!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals("\u00a7cClear Logs")) {
                if (player.hasPermission("spigot.vanish.admin.clearlog")) {
                    this.openClearLogConfirmGui(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to clear logs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals("\u00a7eView Logs")) {
                if (player.hasPermission("spigot.vanish.admin.stats")) {
                    this.openLogViewGui(player, 0);
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
            if (displayName.equals("\u00a7a[ACCEPT]")) {
                if (player.hasPermission("spigot.vanish.admin.clearlog")) {
                    this.clearLogFile(player);
                    this.clearLogConfirmations.remove(player.getUniqueId());
                    player.closeInventory();
                } else {
                    player.sendMessage(this.prefix + String.valueOf(ChatColor.RED) + "You don't have permission to clear logs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                }
            } else if (displayName.equals("\u00a7c[CANCEL]")) {
                this.clearLogConfirmations.remove(player.getUniqueId());
                player.sendMessage(this.prefix + String.valueOf(ChatColor.YELLOW) + "Log file clear cancelled.");
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                this.logAction("Clear Log Cancel", player.getName());
                player.closeInventory();
                this.openAdminGui(player);
            }
        }
        if (title.startsWith("Log Viewer (")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals("\u00a7aNext Page") || displayName.equals("\u00a7cPrevious Page")) {
                try {
                    int currentPage = Integer.parseInt(title.split("\\(")[1].split("/")[0].replace("Page ", "")) - 1;
                    int newPage = displayName.equals("\u00a7aNext Page") ? currentPage + 1 : currentPage - 1;
                    this.openLogViewGui(player, newPage);
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
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.equals("Vanish Admin") || title.equals("Confirm Clear Logs") || title.startsWith("Log Viewer (")) {
            event.setCancelled(true);
        }
    }
}

