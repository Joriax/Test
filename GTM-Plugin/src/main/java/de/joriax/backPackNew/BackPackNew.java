/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerSwapHandItemsEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.backPackNew;

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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BackPackNew
extends JavaPlugin
implements Listener,
CommandExecutor {
    private final Map<UUID, BackpackData> backpackCache = new ConcurrentHashMap<UUID, BackpackData>();
    private final Map<UUID, Inventory> activeInventories = new ConcurrentHashMap<UUID, Inventory>();
    private File configFile;
    private YamlConfiguration backpackConfig;
    private final Map<UUID, String> deleteConfirmations = new HashMap<UUID, String>();
    private final Map<UUID, Boolean> clearLogConfirmations = new HashMap<UUID, Boolean>();
    private final Map<UUID, Long> backpackAccessTimes = new HashMap<UUID, Long>();
    private boolean announcementsEnabled = true;
    private boolean loggingEnabled = true;
    private String prefix = "\u00a78[\u00a76BackPack\u00a78] \u00a7r";
    private File logDir;
    private File logFile;
    private Logger logger;
    private FileHandler logHandler;

    public void onEnable() {
        File mainConfigFile;
        this.setupLogging();
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        if (!(mainConfigFile = new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveResource("config.yml", false);
        }
        YamlConfiguration mainConfig = YamlConfiguration.loadConfiguration((File)mainConfigFile);
        this.prefix = org.bukkit.ChatColor.translateAlternateColorCodes((char)'&', (String)mainConfig.getString("prefix", "&8[&6BackPack&8] &r"));
        this.announcementsEnabled = mainConfig.getBoolean("enable-announcements", true);
        this.loggingEnabled = mainConfig.getBoolean("enable-logging", true);
        this.configFile = new File(this.getDataFolder(), "backpacks.yml");
        if (!this.configFile.exists()) {
            this.saveResource("backpacks.yml", false);
        }
        this.backpackConfig = YamlConfiguration.loadConfiguration((File)this.configFile);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        try {
            Objects.requireNonNull(this.getCommand("backpack")).setExecutor((CommandExecutor)this);
            Objects.requireNonNull(this.getCommand("viewbackpack")).setExecutor((CommandExecutor)this);
            Objects.requireNonNull(this.getCommand("backpackdelete")).setExecutor((CommandExecutor)this);
            Objects.requireNonNull(this.getCommand("backpackadmin")).setExecutor((CommandExecutor)this);
            Objects.requireNonNull(this.getCommand("backpackclearlog")).setExecutor((CommandExecutor)this);
        }
        catch (NullPointerException e) {
            this.getLogger().severe("Failed to register commands - check plugin.yml");
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, this::saveAllBackpacks, 6000L, 6000L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, this::clearExpiredConfirmations, 2400L, 2400L);
        this.getLogger().info("BackpackNew plugin enabled successfully!");
        if (this.announcementsEnabled) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasPermission("backpack.admin.announcements")) continue;
                    p.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.GREEN) + "Plugin enabled!");
                }
            }, 20L);
        }
    }

    private void setupLogging() {
        this.logger = Logger.getLogger("BackpackLogger");
        try {
            this.logDir = new File(this.getDataFolder(), "logs");
            if (!this.logDir.exists()) {
                this.logDir.mkdirs();
            }
            this.logFile = new File(this.logDir, "backpack.log");
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
            this.getLogger().severe("Failed to setup logging: " + e.getMessage());
        }
    }

    private void logAction(String action, String details) {
        if (!this.loggingEnabled) {
            return;
        }
        String logMessage = String.format("%s: %s", action, details);
        this.logger.info(logMessage);
        if (this.announcementsEnabled) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("backpack.admin.announcements")) continue;
                p.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.GRAY) + "[LOG] " + logMessage);
            }
        }
    }

    private void clearLogFile(Player player) {
        block6: {
            try {
                File[] logFiles;
                if (this.logHandler != null) {
                    this.logHandler.close();
                    this.logger.removeHandler(this.logHandler);
                }
                if (this.logDir.exists() && (logFiles = this.logDir.listFiles((dir, name) -> name.startsWith("backpack.log"))) != null) {
                    for (File file : logFiles) {
                        if (!file.exists() || file.delete()) continue;
                        this.getLogger().warning("Failed to delete log file: " + file.getName());
                    }
                }
                this.logFile = new File(this.logDir, "backpack.log");
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
                if (player != null) {
                    player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.GREEN) + "Log file cleared.");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
                this.logAction("Clear Log", (player != null ? player.getName() : "Console") + " cleared the log file");
            }
            catch (IOException e) {
                this.getLogger().severe("Failed to clear log file: " + e.getMessage());
                if (player == null) break block6;
                player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.RED) + "Failed to clear log file: " + e.getMessage());
            }
        }
    }

    private boolean isBlacklistedItem(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return false;
        }
        String displayName = org.bukkit.ChatColor.stripColor((String)item.getItemMeta().getDisplayName());
        List blacklist = this.getConfig().getStringList("item-blacklist");
        return blacklist.stream().anyMatch(blacklisted -> org.bukkit.ChatColor.stripColor((String)org.bukkit.ChatColor.translateAlternateColorCodes((char)'&', (String)blacklisted)).equals(displayName));
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

    public void onDisable() {
        this.saveAllBackpacks();
        if (this.logHandler != null) {
            this.logHandler.close();
            this.logger.removeHandler(this.logHandler);
        }
        this.getLogger().info("BackpackNew plugin disabled.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("backpack")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this command!");
                return true;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("backpack.use")) {
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to use this command!");
                return true;
            }
            this.backpackAccessTimes.put(player.getUniqueId(), System.currentTimeMillis());
            this.openBackpack(player, player, 0);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("backpackadmin")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "This command can only be used by players!");
                return true;
            }
            Player player = (Player)sender;
            if (!player.hasPermission("backpack.admin.gui")) {
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            this.openAdminGui(player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("viewbackpack")) {
            if (!sender.hasPermission("backpack.view")) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Usage: /viewbackpack <player>");
                return true;
            }
            Player target = Bukkit.getPlayer((String)args[0]);
            if (target == null) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Player not found!");
                return true;
            }
            if (sender instanceof Player) {
                this.openBackpack((Player)sender, target, 0);
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Viewing " + target.getName() + "'s backpack");
            } else {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + target.getName() + "'s backpack contents:");
                BackpackData data = this.getBackpackData(target.getUniqueId());
                for (int i = 0; i < data.pages.size(); ++i) {
                    sender.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "Page " + (i + 1) + ":");
                    for (ItemStack item : data.pages.get(i)) {
                        if (item == null || item.getType() == Material.AIR) continue;
                        sender.sendMessage(" - " + String.valueOf(item.getType()) + " x" + item.getAmount());
                    }
                }
            }
            this.logAction("View Backpack", sender.getName() + " viewed " + target.getName());
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("backpackdelete")) {
            if (!sender.hasPermission("backpack.delete")) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Usage: /backpackdelete <player>");
                return true;
            }
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer((String)targetName);
            if (!target.hasPlayedBefore()) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Player not found or has never played before!");
                return true;
            }
            if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
                if (!(sender instanceof Player)) {
                    this.deletePlayerBackpack(target.getUniqueId());
                    sender.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Deleted " + targetName + "'s backpack.");
                    this.logAction("Backpack Delete", sender.getName() + " deleted " + targetName);
                    return true;
                }
                Player player = (Player)sender;
                String confirmation = this.deleteConfirmations.get(player.getUniqueId());
                if (confirmation != null && confirmation.equals(targetName)) {
                    this.deletePlayerBackpack(target.getUniqueId());
                    this.deleteConfirmations.remove(player.getUniqueId());
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Deleted " + targetName + "'s backpack.");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    this.logAction("Backpack Delete", sender.getName() + " deleted " + targetName);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Confirmation expired. Please run the command again.");
                }
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player)sender;
                this.deleteConfirmations.put(player.getUniqueId(), targetName);
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Are you sure you want to delete " + targetName + "'s backpack?");
                TextComponent message = new TextComponent("  ");
                TextComponent acceptButton = new TextComponent("[ACCEPT]");
                acceptButton.setColor(ChatColor.GREEN);
                acceptButton.setBold(Boolean.valueOf(true));
                acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backpackdelete " + targetName + " confirm"));
                TextComponent cancelButton = new TextComponent("[CANCEL]");
                cancelButton.setColor(ChatColor.RED);
                cancelButton.setBold(Boolean.valueOf(true));
                cancelButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backpackdelete cancel"));
                message.addExtra((BaseComponent)acceptButton);
                message.addExtra("   ");
                message.addExtra((BaseComponent)cancelButton);
                player.spigot().sendMessage((BaseComponent)message);
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "This confirmation will expire in 2 minutes.");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
            } else {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "To confirm deletion, type: '/backpackdelete " + targetName + " confirm'");
            }
            this.logAction("Backpack Delete Attempt", sender.getName() + " initiated deletion of " + targetName);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("backpackdelete") && (args.length == 0 || args.length > 0 && args[0].equalsIgnoreCase("cancel"))) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (this.deleteConfirmations.remove(player.getUniqueId()) != null) {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "Backpack deletion cancelled.");
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    this.logAction("Backpack Delete Cancel", player.getName());
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "No pending backpack deletion.");
                }
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("backpackclearlog")) {
            if (!sender.hasPermission("backpack.admin.clearlog")) {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission for this command!");
                return true;
            }
            if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
                if (!(sender instanceof Player)) {
                    this.clearLogFile(null);
                    sender.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Log file cleared.");
                    this.logAction("Clear Log", sender.getName() + " cleared the log file");
                    return true;
                }
                Player player = (Player)sender;
                Boolean confirmation = this.clearLogConfirmations.get(player.getUniqueId());
                if (confirmation != null && confirmation.booleanValue()) {
                    this.clearLogFile(player);
                    this.clearLogConfirmations.remove(player.getUniqueId());
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Confirmation expired. Please run the command again.");
                }
                return true;
            }
            if (args.length >= 1 && args[0].equalsIgnoreCase("cancel")) {
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (this.clearLogConfirmations.remove(player.getUniqueId()) != null) {
                        player.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "Log file clear cancelled.");
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                        this.logAction("Clear Log Cancel", player.getName());
                    } else {
                        player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "No pending log file clear.");
                    }
                }
                return true;
            }
            if (sender instanceof Player) {
                Player player = (Player)sender;
                this.clearLogConfirmations.put(player.getUniqueId(), true);
                this.openClearLogConfirmGui(player);
            } else {
                sender.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "To confirm log file clear, type: '/backpackclearlog confirm'");
            }
            this.logAction("Clear Log Attempt", sender.getName() + " initiated log file clear");
            return true;
        }
        return false;
    }

    private void openAdminGui(Player player) {
        Inventory gui = Bukkit.createInventory((InventoryHolder)player, (int)27, (String)"Backpack Admin");
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < 27; ++i) {
            gui.setItem(i, filler);
        }
        ItemStack loggingItem = new ItemStack(this.loggingEnabled ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta loggingMeta = loggingItem.getItemMeta();
        loggingMeta.setDisplayName(this.loggingEnabled ? String.valueOf(org.bukkit.ChatColor.GREEN) + "Logging Enabled" : String.valueOf(org.bukkit.ChatColor.RED) + "Logging Disabled");
        loggingMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to toggle logging"));
        loggingItem.setItemMeta(loggingMeta);
        gui.setItem(10, loggingItem);
        ItemStack statsItem = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.YELLOW) + "View Statistics");
        statsMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to view backpack statistics"));
        statsItem.setItemMeta(statsMeta);
        gui.setItem(12, statsItem);
        ItemStack announcementsItem = new ItemStack(this.announcementsEnabled ? Material.BELL : Material.BARRIER);
        ItemMeta announcementsMeta = announcementsItem.getItemMeta();
        announcementsMeta.setDisplayName(this.announcementsEnabled ? String.valueOf(org.bukkit.ChatColor.GREEN) + "Notifications Enabled" : String.valueOf(org.bukkit.ChatColor.RED) + "Notifications Disabled");
        announcementsMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to toggle admin notifications"));
        announcementsItem.setItemMeta(announcementsMeta);
        gui.setItem(14, announcementsItem);
        ItemStack clearLogsItem = new ItemStack(Material.REDSTONE);
        ItemMeta clearLogsMeta = clearLogsItem.getItemMeta();
        clearLogsMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.RED) + "Clear Logs");
        clearLogsMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to clear all log files"));
        clearLogsItem.setItemMeta(clearLogsMeta);
        gui.setItem(16, clearLogsItem);
        ItemStack viewLogsItem = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta viewLogsMeta = viewLogsItem.getItemMeta();
        viewLogsMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.YELLOW) + "View Logs");
        viewLogsMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to view recent actions"));
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
        confirmMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.GREEN) + "[ACCEPT]");
        confirmMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to confirm clearing logs"));
        confirm.setItemMeta(confirmMeta);
        gui.setItem(12, confirm);
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.RED) + "[CANCEL]");
        cancelMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "Click to cancel"));
        cancel.setItemMeta(cancelMeta);
        gui.setItem(14, cancel);
        this.clearLogConfirmations.put(player.getUniqueId(), true);
        player.openInventory(gui);
        player.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "This confirmation will expire in 2 minutes.");
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
                paperMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.YELLOW) + "Action #" + (i + 1));
                ArrayList<CallSite> lore = new ArrayList<CallSite>();
                for (String part : parts = entry.split("(?<=\\G.{40})")) {
                    lore.add((CallSite)((Object)(String.valueOf(org.bukkit.ChatColor.GRAY) + part.trim())));
                }
                paperMeta.setLore(lore);
                paper.setItemMeta(paperMeta);
                gui.setItem(i - startIndex, paper);
            }
            if (page > 0) {
                ItemStack prev = new ItemStack(Material.PAPER);
                ItemMeta prevMeta = prev.getItemMeta();
                prevMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.RED) + "Previous Page");
                prev.setItemMeta(prevMeta);
                gui.setItem(45, prev);
            }
            if (page < totalPages - 1) {
                ItemStack next = new ItemStack(Material.PAPER);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page");
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
            this.getLogger().warning("Failed to read log file: " + e.getMessage());
        }
        return entries;
    }

    private void showStats(Player player) {
        int totalBackpacks = this.backpackConfig.getKeys(false).size();
        int onlineBackpacks = this.backpackCache.size();
        HashMap<BackpackSize, Integer> sizeDistribution = new HashMap<BackpackSize, Integer>();
        for (BackpackSize size : BackpackSize.values()) {
            sizeDistribution.put(size, 0);
        }
        for (String string : this.backpackConfig.getKeys(false)) {
            BackpackData data;
            Player p = Bukkit.getPlayer((UUID)UUID.fromString(string));
            if (p == null || (data = this.backpackCache.get(p.getUniqueId())) == null || data.currentSize == null) continue;
            sizeDistribution.merge(data.currentSize, 1, Integer::sum);
        }
        player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.DARK_AQUA) + "\u2550\u2550\u2550\u2550\u2550 Backpack Statistics \u2550\u2550\u2550\u2550\u2550");
        player.sendMessage(String.valueOf(org.bukkit.ChatColor.AQUA) + "\u00bb Total Backpacks: " + String.valueOf(org.bukkit.ChatColor.WHITE) + totalBackpacks);
        player.sendMessage(String.valueOf(org.bukkit.ChatColor.AQUA) + "\u00bb Loaded Backpacks: " + String.valueOf(org.bukkit.ChatColor.WHITE) + onlineBackpacks);
        player.sendMessage(String.valueOf(org.bukkit.ChatColor.AQUA) + "\u00bb Size Distribution:");
        for (Map.Entry entry2 : sizeDistribution.entrySet()) {
            player.sendMessage(String.valueOf(org.bukkit.ChatColor.GOLD) + "  - " + ((BackpackSize)((Object)entry2.getKey())).name() + ": " + String.valueOf(org.bukkit.ChatColor.WHITE) + String.valueOf(entry2.getValue()));
        }
        if (!this.backpackAccessTimes.isEmpty()) {
            player.sendMessage(String.valueOf(org.bukkit.ChatColor.AQUA) + "\u00bb Most Recent Users:");
            this.backpackAccessTimes.entrySet().stream().sorted(Map.Entry.comparingByValue().reversed()).limit(5L).forEach(entry -> {
                String username = Bukkit.getOfflinePlayer((UUID)((UUID)entry.getKey())).getName();
                if (username != null) {
                    long timeDiff = System.currentTimeMillis() - (Long)entry.getValue();
                    String timeAgo = this.formatTimeDifference(timeDiff);
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.GOLD) + "  - " + username + String.valueOf(org.bukkit.ChatColor.GRAY) + " (" + timeAgo + " ago)");
                }
            });
        }
        player.sendMessage(String.valueOf(org.bukkit.ChatColor.DARK_AQUA) + "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
        this.logAction("Stats Viewed", player.getName());
    }

    private void clearExpiredConfirmations() {
        this.deleteConfirmations.clear();
        this.clearLogConfirmations.clear();
    }

    private void deletePlayerBackpack(UUID uuid) {
        this.backpackCache.remove(uuid);
        this.activeInventories.remove(uuid);
        this.backpackConfig.set(uuid.toString(), null);
        try {
            this.backpackConfig.save(this.configFile);
        }
        catch (IOException e) {
            this.getLogger().severe("Failed to save backpacks.yml after deletion: " + e.getMessage());
        }
        Player player = Bukkit.getPlayer((UUID)uuid);
        if (player != null && player.getOpenInventory() != null && player.getOpenInventory().getTitle().startsWith("Backpack (")) {
            player.closeInventory();
            player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Your backpack has been deleted by an admin.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.giveBackpackItem(player);
        if (!this.backpackConfig.contains(player.getUniqueId().toString())) {
            this.backpackConfig.createSection(player.getUniqueId().toString());
        }
        this.updateBackpackSize(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BackpackData data = this.backpackCache.get(uuid);
        if (data != null) {
            this.saveBackpack(uuid, data, true);
        }
        this.backpackCache.remove(uuid);
        this.activeInventories.remove(uuid);
        this.deleteConfirmations.remove(uuid);
        this.clearLogConfirmations.remove(uuid);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.giveBackpackItem(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().removeIf(this::isBackpackItem);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.giveBackpackItem(player), 1L);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.isBackpackItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (this.isBackpackItem(item)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            if (player.hasPermission("backpack.use")) {
                this.openBackpack(player, player, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5f, 1.2f);
            } else {
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to use the backpack!");
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        if (message.startsWith("/lp ") || message.startsWith("/luckperms ")) {
            Player player = event.getPlayer();
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.updateBackpackSize(player), 20L);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        String displayName;
        ItemStack clickedItem;
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();
            if (this.isBackpackItem(currentItem) || this.isBackpackItem(cursorItem)) {
                event.setCancelled(true);
                this.giveBackpackItem(player);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
        }
        if (title.startsWith("Backpack (") || title.contains("'s Backpack (")) {
            if (!player.hasPermission("backpack.view")) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.RED) + "No permission to view backpack!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            Player owner = player;
            UUID ownerUUID = player.getUniqueId();
            if (title.contains("'s Backpack (")) {
                String ownerName = title.split("'s")[0].trim();
                Player target = Bukkit.getPlayerExact((String)ownerName);
                if (target != null) {
                    owner = target;
                    ownerUUID = target.getUniqueId();
                } else {
                    OfflinePlayer offlineTarget = Arrays.stream(Bukkit.getOfflinePlayers()).filter(op -> ownerName.equalsIgnoreCase(op.getName())).findFirst().orElse(null);
                    if (offlineTarget != null) {
                        ownerUUID = offlineTarget.getUniqueId();
                    } else {
                        player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.RED) + "Backpack owner not found!");
                        event.setCancelled(true);
                        player.closeInventory();
                        return;
                    }
                }
            }
            if (!player.equals((Object)owner) && !player.hasPermission("backpack.edit")) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.RED) + "No permission to edit backpack!");
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
            if (this.isBackpackItem(currentItem) || this.isBackpackItem(cursorItem)) {
                event.setCancelled(true);
                return;
            }
            if (this.isBlacklistedItem(currentItem) || this.isBlacklistedItem(cursorItem)) {
                event.setCancelled(true);
                player.sendMessage(this.prefix + String.valueOf(org.bukkit.ChatColor.RED) + "This item is blacklisted and cannot be modified!");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 1.0f);
                return;
            }
            ItemStack clickedItem2 = event.getCurrentItem();
            if (clickedItem2 != null && (clickedItem2.getType() == Material.BLACK_STAINED_GLASS_PANE || clickedItem2.getType() == Material.PAPER && clickedItem2.hasItemMeta() && (clickedItem2.getItemMeta().getDisplayName().equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page") || clickedItem2.getItemMeta().getDisplayName().equals(String.valueOf(org.bukkit.ChatColor.RED) + "Previous Page")))) {
                event.setCancelled(true);
                if (clickedItem2.getType() == Material.PAPER) {
                    String displayName2 = clickedItem2.getItemMeta().getDisplayName();
                    int currentPage = Integer.parseInt(title.split("\\(")[1].split("/")[0]) - 1;
                    if (displayName2.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page")) {
                        this.openBackpack(player, owner, currentPage + 1);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                    } else if (displayName2.equals(String.valueOf(org.bukkit.ChatColor.RED) + "Previous Page")) {
                        this.openBackpack(player, owner, currentPage - 1);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
                    }
                }
                return;
            }
            BackpackData data = this.getBackpackData(ownerUUID);
            BackpackSize size = data.currentSize;
            if (event.getSlot() >= 45 || event.getSlot() >= size.slots && event.getSlot() < 45) {
                event.setCancelled(true);
                return;
            }
            if (event.getAction() != InventoryAction.NOTHING) {
                Inventory inv = event.getInventory();
                ItemStack[] beforeInv = (ItemStack[])inv.getContents().clone();
                ItemStack[] beforePlayerInv = (ItemStack[])player.getInventory().getContents().clone();
                int page = Integer.parseInt(title.split("\\(")[1].split("/")[0]) - 1;
                Player finalOwner = owner;
                UUID finalOwnerUUID = ownerUUID;
                Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                    ItemStack[] afterInv = inv.getContents();
                    ItemStack[] afterPlayerInv = player.getInventory().getContents();
                    if (!player.equals((Object)finalOwner) && player.hasPermission("backpack.view")) {
                        int amountDiff;
                        ItemStack after;
                        ItemStack before;
                        int i;
                        for (i = 0; i < beforeInv.length; ++i) {
                            before = beforeInv[i];
                            after = afterInv[i];
                            if (before == null && after != null && after.getType() != Material.AIR) {
                                this.logAction("Item Placed", String.format("%s placed %dx%s in %s's backpack (page %d, slot %d)", player.getName(), after.getAmount(), this.getItemDisplayName(after), finalOwner.getName(), page + 1, i));
                                continue;
                            }
                            if (before != null && before.getType() != Material.AIR && after == null) {
                                this.logAction("Item Taken", String.format("%s took %dx%s from %s's backpack (page %d, slot %d)", player.getName(), before.getAmount(), this.getItemDisplayName(before), finalOwner.getName(), page + 1, i));
                                continue;
                            }
                            if (before != null && after != null && !before.isSimilar(after)) {
                                this.logAction("Item Taken", String.format("%s took %dx%s from %s's backpack (page %d, slot %d)", player.getName(), before.getAmount(), this.getItemDisplayName(before), finalOwner.getName(), page + 1, i));
                                this.logAction("Item Placed", String.format("%s placed %dx%s in %s's backpack (page %d, slot %d)", player.getName(), after.getAmount(), this.getItemDisplayName(after), finalOwner.getName(), page + 1, i));
                                continue;
                            }
                            if (before == null || after == null || before.getAmount() == after.getAmount()) continue;
                            amountDiff = after.getAmount() - before.getAmount();
                            if (amountDiff > 0) {
                                this.logAction("Item Placed", String.format("%s placed %dx%s in %s's backpack (page %d, slot %d)", player.getName(), amountDiff, this.getItemDisplayName(after), finalOwner.getName(), page + 1, i));
                                continue;
                            }
                            this.logAction("Item Taken", String.format("%s took %dx%s from %s's backpack (page %d, slot %d)", player.getName(), -amountDiff, this.getItemDisplayName(before), finalOwner.getName(), page + 1, i));
                        }
                        for (i = 0; i < beforePlayerInv.length; ++i) {
                            before = beforePlayerInv[i];
                            after = afterPlayerInv[i];
                            if (before != null && before.getType() != Material.AIR && after == null) {
                                this.logAction("Item Placed", String.format("%s placed %dx%s in %s's backpack (shift-click, page %d)", player.getName(), before.getAmount(), this.getItemDisplayName(before), finalOwner.getName(), page + 1));
                                continue;
                            }
                            if (before == null && after != null && after.getType() != Material.AIR) {
                                this.logAction("Item Taken", String.format("%s took %dx%s from %s's backpack (shift-click, page %d)", player.getName(), after.getAmount(), this.getItemDisplayName(after), finalOwner.getName(), page + 1));
                                continue;
                            }
                            if (before != null && after != null && before.getAmount() > after.getAmount()) {
                                amountDiff = before.getAmount() - after.getAmount();
                                this.logAction("Item Placed", String.format("%s placed %dx%s in %s's backpack (shift-click, page %d)", player.getName(), amountDiff, this.getItemDisplayName(before), finalOwner.getName(), page + 1));
                                continue;
                            }
                            if (before == null || after == null || before.getAmount() >= after.getAmount()) continue;
                            amountDiff = after.getAmount() - before.getAmount();
                            this.logAction("Item Taken", String.format("%s took %dx%s from %s's backpack (shift-click, page %d)", player.getName(), amountDiff, this.getItemDisplayName(after), finalOwner.getName(), page + 1));
                        }
                    }
                    this.saveCurrentPage(finalOwner, inv, page);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.2f);
                }, 1L);
            }
            return;
        }
        if (title.equals("Backpack Admin")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Logging Enabled") || displayName.equals(String.valueOf(org.bukkit.ChatColor.RED) + "Logging Disabled")) {
                if (player.hasPermission("backpack.admin.logging")) {
                    this.toggleLogging(player);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to toggle logging!");
                }
            } else if (displayName.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Notifications Enabled") || displayName.equals(String.valueOf(org.bukkit.ChatColor.RED) + "Notifications Disabled")) {
                if (player.hasPermission("backpack.admin.announcements")) {
                    this.toggleAnnouncements(player);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to toggle announcements!");
                }
            } else if (displayName.equals(String.valueOf(org.bukkit.ChatColor.YELLOW) + "View Statistics")) {
                if (player.hasPermission("backpack.admin.stats")) {
                    player.closeInventory();
                    this.showStats(player);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to view statistics!");
                }
            } else if (displayName.equals(String.valueOf(org.bukkit.ChatColor.RED) + "Clear Logs")) {
                if (player.hasPermission("backpack.admin.clearlog")) {
                    this.openClearLogConfirmGui(player);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to clear logs!");
                }
            } else if (displayName.equals(String.valueOf(org.bukkit.ChatColor.YELLOW) + "View Logs")) {
                if (player.hasPermission("backpack.admin.stats")) {
                    this.openLogViewGui(player, 0);
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to view logs!");
                }
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
        }
        if (title.equals("Confirm Clear Logs")) {
            event.setCancelled(true);
            clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            displayName = clickedItem.getItemMeta().getDisplayName();
            if (displayName.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "[ACCEPT]")) {
                if (player.hasPermission("backpack.admin.clearlog")) {
                    Boolean confirmation = this.clearLogConfirmations.get(player.getUniqueId());
                    if (confirmation != null && confirmation.booleanValue()) {
                        this.clearLogFile(player);
                        this.clearLogConfirmations.remove(player.getUniqueId());
                        player.closeInventory();
                    } else {
                        player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Confirmation expired. Please try again.");
                        player.closeInventory();
                    }
                } else {
                    player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You don't have permission to clear logs!");
                }
            } else if (displayName.equals(String.valueOf(org.bukkit.ChatColor.RED) + "[CANCEL]")) {
                this.clearLogConfirmations.remove(player.getUniqueId());
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.YELLOW) + "Log file clear cancelled.");
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
            if (displayName.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page") || displayName.equals(String.valueOf(org.bukkit.ChatColor.RED) + "Previous Page")) {
                int currentPage = Integer.parseInt(title.split("\\(")[1].split("/")[0].replace("Page ", "")) - 1;
                if (displayName.equals(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page")) {
                    this.openLogViewGui(player, currentPage + 1);
                } else {
                    this.openLogViewGui(player, currentPage - 1);
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.2f);
            }
        }
    }

    private void toggleLogging(Player player) {
        this.loggingEnabled = !this.loggingEnabled;
        File mainConfigFile = new File(this.getDataFolder(), "config.yml");
        YamlConfiguration mainConfig = YamlConfiguration.loadConfiguration((File)mainConfigFile);
        mainConfig.set("enable-logging", (Object)this.loggingEnabled);
        try {
            mainConfig.save(mainConfigFile);
        }
        catch (IOException e) {
            this.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
        player.sendMessage(this.prefix + (this.loggingEnabled ? String.valueOf(org.bukkit.ChatColor.GREEN) + "Logging enabled!" : String.valueOf(org.bukkit.ChatColor.RED) + "Logging disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Logging Toggle", player.getName() + " set to " + (this.loggingEnabled ? "on" : "off"));
        this.openAdminGui(player);
    }

    private void toggleAnnouncements(Player player) {
        this.announcementsEnabled = !this.announcementsEnabled;
        File mainConfigFile = new File(this.getDataFolder(), "config.yml");
        YamlConfiguration mainConfig = YamlConfiguration.loadConfiguration((File)mainConfigFile);
        mainConfig.set("enable-announcements", (Object)this.announcementsEnabled);
        try {
            mainConfig.save(mainConfigFile);
        }
        catch (IOException e) {
            this.getLogger().severe("Failed to save config.yml: " + e.getMessage());
        }
        player.sendMessage(this.prefix + (this.announcementsEnabled ? String.valueOf(org.bukkit.ChatColor.GREEN) + "Notifications enabled!" : String.valueOf(org.bukkit.ChatColor.RED) + "Notifications disabled!"));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        this.logAction("Notifications Toggle", player.getName() + " set to " + (this.announcementsEnabled ? "on" : "off"));
        this.openAdminGui(player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith("Backpack (") || title.contains("'s Backpack (")) {
            Object ownerName;
            Player player;
            Player owner = player = (Player)event.getWhoClicked();
            UUID ownerUUID = player.getUniqueId();
            if (title.contains("'s Backpack (")) {
                ownerName = title.split("'s")[0].trim();
                Player target = Bukkit.getPlayerExact((String)ownerName);
                if (target != null) {
                    owner = target;
                    ownerUUID = target.getUniqueId();
                } else {
                    OfflinePlayer offlineTarget = Arrays.stream(Bukkit.getOfflinePlayers()).filter(arg_0 -> BackPackNew.lambda$onInventoryDrag$9((String)ownerName, arg_0)).findFirst().orElse(null);
                    if (offlineTarget != null) {
                        ownerUUID = offlineTarget.getUniqueId();
                    }
                }
            }
            ownerName = event.getRawSlots().iterator();
            while (ownerName.hasNext()) {
                int slot = (Integer)ownerName.next();
                if (slot < 45 && (slot >= 45 || slot < this.getBackpackData((UUID)ownerUUID).currentSize.slots)) continue;
                event.setCancelled(true);
                return;
            }
            if (!event.isCancelled()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 1.2f);
                int page = Integer.parseInt(title.split("\\(")[1].split("/")[0]) - 1;
                this.saveCurrentPage(owner, event.getInventory(), page);
                if (!player.equals((Object)owner) && player.hasPermission("backpack.view")) {
                    this.logAction("Admin Backpack Action", String.format("%s modified %s's backpack (page %d)", player.getName(), owner.getName(), page + 1));
                }
            }
        } else if (title.equals("Backpack Admin") || title.equals("Confirm Clear Logs") || title.startsWith("Log Viewer (")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        String title = event.getView().getTitle();
        if (title.startsWith("Backpack (") || title.contains("'s Backpack (")) {
            Inventory inv;
            int page = Integer.parseInt(title.split("\\(")[1].split("/")[0]) - 1;
            Player owner = player;
            UUID ownerUUID = player.getUniqueId();
            if (title.contains("'s Backpack (")) {
                String ownerName = title.split("'s")[0].trim();
                Player target = Bukkit.getPlayerExact((String)ownerName);
                if (target != null) {
                    owner = target;
                    ownerUUID = target.getUniqueId();
                } else {
                    OfflinePlayer offlineTarget = Arrays.stream(Bukkit.getOfflinePlayers()).filter(op -> ownerName.equalsIgnoreCase(op.getName())).findFirst().orElse(null);
                    if (offlineTarget != null) {
                        ownerUUID = offlineTarget.getUniqueId();
                    }
                }
            }
            if ((inv = this.activeInventories.get(player.getUniqueId())) != null && inv.equals((Object)event.getInventory())) {
                this.saveCurrentPage(owner, event.getInventory(), page);
                BackpackData data = this.backpackCache.get(ownerUUID);
                if (data != null) {
                    this.saveBackpack(ownerUUID, data, true);
                    long openTime = this.backpackAccessTimes.getOrDefault(player.getUniqueId(), 0L);
                    long duration = System.currentTimeMillis() - openTime;
                    this.logAction("Backpack Closed", String.format("%s closed %s's backpack after %d ms", player.getName(), owner.getName(), duration));
                }
                this.activeInventories.remove(player.getUniqueId());
            }
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.2f);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (this.isBackpackItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (this.isBackpackItem(event.getMainHandItem()) || this.isBackpackItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    private boolean isBackpackItem(ItemStack item) {
        return item != null && item.getType() == Material.CHEST && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals("\u00a76Backpack");
    }

    private void giveBackpackItem(Player player) {
        ItemStack backpack = new ItemStack(Material.CHEST);
        ItemMeta meta = backpack.getItemMeta();
        meta.setDisplayName("\u00a76Backpack");
        meta.setUnbreakable(true);
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
        backpack.setItemMeta(meta);
        ItemStack current = player.getInventory().getItem(8);
        if (current == null || !this.isBackpackItem(current)) {
            player.getInventory().setItem(8, backpack);
        }
    }

    private void updateBackpackSize(Player player) {
        BackpackSize newSize;
        UUID uuid = player.getUniqueId();
        BackpackData data = this.backpackCache.get(uuid);
        if (data == null) {
            data = new BackpackData();
            this.backpackCache.put(uuid, data);
        }
        if (data.currentSize != (newSize = this.getBackpackSize(player))) {
            data.currentSize = newSize;
            ArrayList<ItemStack[]> newPages = new ArrayList<ItemStack[]>();
            int maxItems = newSize.slots * newSize.pages;
            int currentItems = 0;
            for (ItemStack[] itemStackArray : data.pages) {
                for (ItemStack item : itemStackArray) {
                    if (item == null || item.getType() == Material.AIR) continue;
                    ++currentItems;
                }
            }
            if (currentItems > maxItems) {
                int itemsToMove = currentItems - maxItems;
                for (ItemStack[] page : data.pages) {
                    for (int i = 0; i < page.length && itemsToMove > 0; ++i) {
                        if (page[i] == null || page[i].getType() == Material.AIR) continue;
                        player.getInventory().addItem(new ItemStack[]{page[i]});
                        page[i] = null;
                        --itemsToMove;
                    }
                }
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Some items were moved to your inventory due to reduced backpack size!");
            }
            for (int i = 0; i < newSize.pages; ++i) {
                ItemStack[] itemStackArray = new ItemStack[newSize.slots];
                if (i < data.pages.size()) {
                    ItemStack[] oldPage = data.pages.get(i);
                    for (int j = 0; j < Math.min(newSize.slots, oldPage.length); ++j) {
                        itemStackArray[j] = oldPage[j];
                    }
                }
                newPages.add(itemStackArray);
            }
            data.pages = newPages;
            data.touch();
            this.saveBackpack(uuid, data, true);
            if (player.getOpenInventory().getTitle().startsWith("Backpack (") || player.getOpenInventory().getTitle().contains("'s Backpack (")) {
                player.closeInventory();
                this.openBackpack(player, player, 0);
                player.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Your backpack size has been updated!");
            }
        }
    }

    private void openBackpack(Player viewer, Player owner, int page) {
        int i;
        this.updateBackpackSize(owner);
        BackpackData data = this.getBackpackData(owner.getUniqueId());
        BackpackSize size = data.currentSize;
        while (data.pages.size() < size.pages) {
            data.pages.add(new ItemStack[size.slots]);
        }
        String title = viewer.equals((Object)owner) ? "Backpack (" + (page + 1) + "/" + size.pages + ")" : owner.getName() + "'s Backpack (" + (page + 1) + "/" + size.pages + ")";
        Inventory inv = Bukkit.createInventory((InventoryHolder)viewer, (int)54, (String)title);
        ItemStack lockedPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = lockedPane.getItemMeta();
        paneMeta.setDisplayName(String.valueOf(org.bukkit.ChatColor.RED) + "Locked");
        paneMeta.setLore(Arrays.asList(String.valueOf(org.bukkit.ChatColor.GRAY) + "You don't have access", String.valueOf(org.bukkit.ChatColor.GRAY) + "to this slot"));
        lockedPane.setItemMeta(paneMeta);
        for (int i2 = 45; i2 < 54; ++i2) {
            inv.setItem(i2, lockedPane);
        }
        ItemStack[] contents = data.pages.get(page);
        for (i = 0; i < Math.min(size.slots, contents.length); ++i) {
            inv.setItem(i, contents[i] != null ? contents[i] : new ItemStack(Material.AIR));
        }
        for (i = size.slots; i < 45; ++i) {
            inv.setItem(i, lockedPane);
        }
        if (size.pages > 1) {
            ItemMeta meta;
            if (page < size.pages - 1) {
                ItemStack next = new ItemStack(Material.PAPER);
                meta = next.getItemMeta();
                meta.setDisplayName(String.valueOf(org.bukkit.ChatColor.GREEN) + "Next Page");
                next.setItemMeta(meta);
                inv.setItem(53, next);
            } else {
                inv.setItem(53, lockedPane);
            }
            if (page > 0) {
                ItemStack prev = new ItemStack(Material.PAPER);
                meta = prev.getItemMeta();
                meta.setDisplayName(String.valueOf(org.bukkit.ChatColor.RED) + "Previous Page");
                prev.setItemMeta(meta);
                inv.setItem(45, prev);
            } else {
                inv.setItem(45, lockedPane);
            }
            for (int i3 = 46; i3 < 53; ++i3) {
                inv.setItem(i3, lockedPane);
            }
        }
        this.activeInventories.put(viewer.getUniqueId(), inv);
        this.backpackAccessTimes.put(viewer.getUniqueId(), System.currentTimeMillis());
        viewer.openInventory(inv);
    }

    private void saveCurrentPage(Player owner, Inventory inv, int page) {
        BackpackData data = this.backpackCache.get(owner.getUniqueId());
        if (data == null) {
            return;
        }
        BackpackSize size = data.currentSize;
        ItemStack[] contents = new ItemStack[size.slots];
        for (int i = 0; i < size.slots && i < inv.getSize(); ++i) {
            ItemStack item = inv.getItem(i);
            contents[i] = item != null && item.getType() != Material.BLACK_STAINED_GLASS_PANE ? item : null;
        }
        while (data.pages.size() <= page) {
            data.pages.add(new ItemStack[size.slots]);
        }
        data.pages.set(page, contents);
        data.touch();
    }

    private void saveAllBackpacks() {
        for (Map.Entry<UUID, BackpackData> entry : this.backpackCache.entrySet()) {
            this.saveBackpack(entry.getKey(), entry.getValue(), true);
        }
        try {
            this.backpackConfig.save(this.configFile);
        }
        catch (IOException e) {
            this.getLogger().severe("Failed to save backpacks.yml: " + e.getMessage());
        }
    }

    private BackpackData getBackpackData(UUID uuid) {
        return this.backpackCache.computeIfAbsent(uuid, k -> {
            BackpackData data = new BackpackData();
            Player player = Bukkit.getPlayer((UUID)uuid);
            BackpackSize backpackSize = data.currentSize = player != null ? this.getBackpackSize(player) : BackpackSize.DEFAULT;
            if (this.backpackConfig.contains(uuid.toString())) {
                ConfigurationSection section = this.backpackConfig.getConfigurationSection(uuid.toString());
                for (String key : section.getKeys(false)) {
                    if (!key.startsWith("page")) continue;
                    int page = Integer.parseInt(key.substring(4));
                    List itemList = section.getList(key);
                    if (itemList == null) continue;
                    ItemStack[] items = (ItemStack[])itemList.stream().map(obj -> obj instanceof ItemStack ? (ItemStack)obj : null).toArray(ItemStack[]::new);
                    while (data.pages.size() <= page) {
                        data.pages.add(new ItemStack[data.currentSize.slots]);
                    }
                    data.pages.set(page, items);
                }
            }
            return data;
        });
    }

    private void saveBackpack(UUID uuid, BackpackData data, boolean immediate) {
        ConfigurationSection section = this.backpackConfig.createSection(uuid.toString());
        for (int i = 0; i < data.pages.size(); ++i) {
            ArrayList<ItemStack> toSave = new ArrayList<ItemStack>();
            ItemStack[] pageItems = data.pages.get(i);
            if (pageItems != null) {
                for (ItemStack item : pageItems) {
                    if (item != null && item.getType() != Material.AIR) {
                        toSave.add(item);
                        continue;
                    }
                    toSave.add(null);
                }
            }
            section.set("page" + i, toSave.isEmpty() ? null : toSave);
        }
        if (immediate) {
            try {
                this.backpackConfig.save(this.configFile);
                this.getLogger().info("Saved backpack for UUID " + String.valueOf(uuid));
            }
            catch (IOException e) {
                this.getLogger().severe("Failed to save backpack for " + String.valueOf(uuid) + ": " + e.getMessage());
            }
        }
    }

    private BackpackSize getBackpackSize(Player player) {
        if (player == null) {
            return BackpackSize.DEFAULT;
        }
        if (player.hasPermission("backpack.admin")) {
            return BackpackSize.ADMIN;
        }
        if (player.hasPermission("backpack.supremium")) {
            return BackpackSize.SUPREMIUM;
        }
        if (player.hasPermission("backpack.sponsor")) {
            return BackpackSize.SPONSOR;
        }
        if (player.hasPermission("backpack.premium")) {
            return BackpackSize.PREMIUM;
        }
        if (player.hasPermission("backpack.elite")) {
            return BackpackSize.ELITE;
        }
        if (player.hasPermission("backpack.vip")) {
            return BackpackSize.VIP;
        }
        return BackpackSize.DEFAULT;
    }

    private String formatTimeDifference(long timeDiffMs) {
        long seconds = timeDiffMs / 1000L;
        if (seconds < 60L) {
            return seconds + " seconds";
        }
        long minutes = seconds / 60L;
        if (minutes < 60L) {
            return minutes + " minutes";
        }
        long hours = minutes / 60L;
        if (hours < 24L) {
            return hours + " hours";
        }
        long days = hours / 24L;
        return days + " days";
    }

    private static /* synthetic */ boolean lambda$onInventoryDrag$9(String ownerName, OfflinePlayer op) {
        return ownerName.equalsIgnoreCase(op.getName());
    }

    private static class BackpackData {
        List<ItemStack[]> pages = new ArrayList<ItemStack[]>();
        long lastAccessed = System.currentTimeMillis();
        BackpackSize currentSize;

        private BackpackData() {
        }

        void touch() {
            this.lastAccessed = System.currentTimeMillis();
        }
    }

    private static enum BackpackSize {
        DEFAULT(18, 1),
        VIP(36, 1),
        ELITE(45, 2),
        PREMIUM(54, 1),
        SPONSOR(54, 3),
        SUPREMIUM(54, 4),
        ADMIN(54, 11);

        final int slots;
        final int pages;

        private BackpackSize(int slots, int pages) {
            this.slots = slots;
            this.pages = pages;
        }
    }
}

