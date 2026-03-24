package de.joriax.gtm.backpack;

import de.joriax.gtm.GTMPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BackpackManager implements CommandExecutor, Listener {

    private final GTMPlugin plugin;

    // Current open backpack sessions: player UUID -> {page, ownerUUID}
    private final Map<UUID, UUID> openBackpacks = new HashMap<>();   // viewer -> owner
    private final Map<UUID, Integer> openPages = new HashMap<>();     // viewer -> page

    // Backpack type config
    private static final String[] TYPES = {"basic", "vip", "elite", "premium", "sponsor", "supremium", "admin"};
    private static final int[] SIZES = {27, 36, 45, 54, 54, 54, 54};
    private static final int[] PAGES = {1, 1, 2, 1, 3, 4, 11};

    // Navigation item slots based on size
    private static final int PREV_SLOT_27 = 18;  // row 3 start
    private static final int NEXT_SLOT_27 = 26;

    public BackpackManager(GTMPlugin plugin) {
        this.plugin = plugin;
        // Auto-save task every 5 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimerAsynchronously(plugin, 20L * 300, 20L * 300);
    }

    // ========== COMMAND HANDLING ==========

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label.toLowerCase()) {
            case "backpack":
                return cmdBackpack(sender, args);
            case "viewbackpack":
                return cmdViewBackpack(sender, args);
            case "backpackdelete":
                return cmdBackpackDelete(sender, args);
            default:
                return false;
        }
    }

    private boolean cmdBackpack(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("backpack.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use a backpack.");
            return true;
        }
        openBackpack(player, player.getUniqueId(), 0);
        return true;
    }

    private boolean cmdViewBackpack(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gtm.admin.viewbackpack")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to view backpacks.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /viewbackpack <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view backpacks.");
            return true;
        }
        openBackpack((Player) sender, target.getUniqueId(), 0);
        return true;
    }

    private boolean cmdBackpackDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gtm.admin.backpackdelete")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to delete backpacks.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /backpackdelete <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            // Try offline
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' must be online to delete their backpack.");
            return true;
        }
        File bpFile = getBackpackFile(target.getUniqueId());
        if (bpFile.exists()) {
            bpFile.delete();
            sender.sendMessage(ChatColor.GREEN + "Deleted " + target.getName() + "'s backpack.");
            target.sendMessage(ChatColor.RED + "Your backpack has been deleted by an admin.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + " has no backpack data to delete.");
        }
        return true;
    }

    // ========== BACKPACK LOGIC ==========

    public String getBackpackType(Player player) {
        for (int i = TYPES.length - 1; i >= 0; i--) {
            if (player.hasPermission("backpack." + TYPES[i])) {
                return TYPES[i];
            }
        }
        return "basic";
    }

    public int getMaxPages(Player player) {
        for (int i = TYPES.length - 1; i >= 0; i--) {
            if (player.hasPermission("backpack." + TYPES[i])) {
                return PAGES[i];
            }
        }
        return 1;
    }

    public int getSize(Player player) {
        for (int i = TYPES.length - 1; i >= 0; i--) {
            if (player.hasPermission("backpack." + TYPES[i])) {
                return SIZES[i];
            }
        }
        return 27;
    }

    public int getMaxPagesForOwner(UUID ownerUUID) {
        Player owner = Bukkit.getPlayer(ownerUUID);
        if (owner != null) return getMaxPages(owner);
        return 1;
    }

    public int getSizeForOwner(UUID ownerUUID) {
        Player owner = Bukkit.getPlayer(ownerUUID);
        if (owner != null) return getSize(owner);
        return 27;
    }

    public void openBackpack(Player viewer, UUID ownerUUID, int page) {
        int maxPages = getMaxPagesForOwner(ownerUUID);
        int size = getSizeForOwner(ownerUUID);

        if (page >= maxPages) {
            viewer.sendMessage(ChatColor.RED + "This backpack only has " + maxPages + " page(s).");
            return;
        }

        String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
        if (ownerName == null) ownerName = ownerUUID.toString().substring(0, 8);

        String title;
        if (viewer.getUniqueId().equals(ownerUUID)) {
            title = ChatColor.DARK_GRAY + "Backpack - Page " + (page + 1) + "/" + maxPages;
        } else {
            title = ChatColor.DARK_GRAY + ownerName + "'s Backpack - P" + (page + 1);
        }

        // Adjust size for navigation if multi-page
        int invSize = size;
        if (maxPages > 1) {
            invSize = Math.max(size, 27);
        }

        Inventory inv = Bukkit.createInventory(null, invSize, title);

        // Load saved items for this page
        ItemStack[] savedItems = loadPage(ownerUUID, page, invSize);
        // Only fill content slots (not navigation row)
        int contentSlots = maxPages > 1 ? invSize - 9 : invSize;
        for (int i = 0; i < contentSlots && i < savedItems.length; i++) {
            if (savedItems[i] != null) {
                inv.setItem(i, savedItems[i]);
            }
        }

        // Add navigation buttons if multi-page
        if (maxPages > 1) {
            int lastRow = invSize - 9;
            // Previous button
            if (page > 0) {
                inv.setItem(lastRow, createNavItem(Material.ARROW, ChatColor.GREEN + "Previous Page"));
            } else {
                inv.setItem(lastRow, createNavItem(Material.BARRIER, ChatColor.RED + "No Previous Page"));
            }
            // Page info
            inv.setItem(lastRow + 4, createNavItem(Material.BOOK,
                    ChatColor.YELLOW + "Page " + (page + 1) + " of " + maxPages));
            // Next button
            if (page < maxPages - 1) {
                inv.setItem(lastRow + 8, createNavItem(Material.ARROW, ChatColor.GREEN + "Next Page"));
            } else {
                inv.setItem(lastRow + 8, createNavItem(Material.BARRIER, ChatColor.RED + "No Next Page"));
            }
        }

        openBackpacks.put(viewer.getUniqueId(), ownerUUID);
        openPages.put(viewer.getUniqueId(), page);
        viewer.openInventory(inv);
    }

    private ItemStack createNavItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    // ========== EVENT HANDLING ==========

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player viewer = (Player) event.getWhoClicked();

        if (!openBackpacks.containsKey(viewer.getUniqueId())) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(viewer.getOpenInventory().getTopInventory())) return;

        UUID ownerUUID = openBackpacks.get(viewer.getUniqueId());
        int page = openPages.get(viewer.getUniqueId());
        int maxPages = getMaxPagesForOwner(ownerUUID);
        int size = event.getClickedInventory().getSize();
        int slot = event.getSlot();

        // Check navigation row
        if (maxPages > 1) {
            int lastRow = size - 9;
            if (slot >= lastRow) {
                event.setCancelled(true);
                if (slot == lastRow && page > 0) {
                    // Previous page - save current first
                    saveCurrentPage(viewer, ownerUUID, page);
                    viewer.closeInventory();
                    openBackpack(viewer, ownerUUID, page - 1);
                } else if (slot == lastRow + 8 && page < maxPages - 1) {
                    // Next page - save current first
                    saveCurrentPage(viewer, ownerUUID, page);
                    viewer.closeInventory();
                    openBackpack(viewer, ownerUUID, page + 1);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player viewer = (Player) event.getPlayer();

        if (!openBackpacks.containsKey(viewer.getUniqueId())) return;

        UUID ownerUUID = openBackpacks.get(viewer.getUniqueId());
        int page = openPages.get(viewer.getUniqueId());

        saveCurrentPage(viewer, ownerUUID, page);

        openBackpacks.remove(viewer.getUniqueId());
        openPages.remove(viewer.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (openBackpacks.containsKey(player.getUniqueId())) {
            UUID ownerUUID = openBackpacks.get(player.getUniqueId());
            int page = openPages.getOrDefault(player.getUniqueId(), 0);
            saveCurrentPage(player, ownerUUID, page);
            openBackpacks.remove(player.getUniqueId());
            openPages.remove(player.getUniqueId());
        }
    }

    // ========== SAVE/LOAD ==========

    private void saveCurrentPage(Player viewer, UUID ownerUUID, int page) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();
        int size = inv.getSize();
        int maxPages = getMaxPagesForOwner(ownerUUID);
        int contentSlots = maxPages > 1 ? size - 9 : size;

        ItemStack[] pageItems = new ItemStack[contentSlots];
        for (int i = 0; i < contentSlots; i++) {
            pageItems[i] = inv.getItem(i);
        }
        savePage(ownerUUID, page, pageItems);
    }

    private File getBackpackFile(UUID uuid) {
        File dir = new File(plugin.getDataFolder(), "backpacks");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, uuid.toString() + ".yml");
    }

    private void savePage(UUID ownerUUID, int page, ItemStack[] items) {
        File file = getBackpackFile(ownerUUID);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String pageKey = "page." + page;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                config.set(pageKey + "." + i, items[i]);
            } else {
                config.set(pageKey + "." + i, null);
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("[Backpack] Failed to save backpack for " + ownerUUID + ": " + e.getMessage());
        }
    }

    private ItemStack[] loadPage(UUID ownerUUID, int page, int size) {
        File file = getBackpackFile(ownerUUID);
        ItemStack[] items = new ItemStack[size];

        if (!file.exists()) return items;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String pageKey = "page." + page;

        if (!config.contains(pageKey)) return items;

        int contentSlots = size;
        for (int i = 0; i < contentSlots; i++) {
            if (config.contains(pageKey + "." + i)) {
                ItemStack item = config.getItemStack(pageKey + "." + i);
                items[i] = item;
            }
        }
        return items;
    }

    public void saveAll() {
        for (Map.Entry<UUID, UUID> entry : openBackpacks.entrySet()) {
            Player viewer = Bukkit.getPlayer(entry.getKey());
            if (viewer != null && viewer.isOnline()) {
                UUID ownerUUID = entry.getValue();
                int page = openPages.getOrDefault(entry.getKey(), 0);
                saveCurrentPage(viewer, ownerUUID, page);
            }
        }
    }
}
