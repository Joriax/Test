/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.joriax.spigotAdminSystem.Utils.Perks;

import de.joriax.economy.EconomyAPI;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksCommand;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksManager;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PerksListener
implements Listener {
    private final EconomyAPI economyAPI;
    private final PerksManager perksManager;

    public PerksListener(EconomyAPI economyAPI, PerksManager perksManager) {
        this.economyAPI = economyAPI;
        this.perksManager = perksManager;
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith("\u00a76Perks") || title.startsWith("\u00a76Admin: Manage")) {
            event.setCancelled(true);
            System.out.println("[PerksListener] Cancelled drag in inventory: " + title);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getWhoClicked();
        String title = event.getView().getTitle();
        if (title.startsWith("\u00a76Perks") || title.startsWith("\u00a76Admin: Manage")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            System.out.println("[PerksListener] Click cancelled in inventory: " + title + ", ClickType: " + String.valueOf(event.getClick()));
            if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }
            String displayName = clickedItem.getItemMeta().getDisplayName();
            System.out.println("[PerksListener] Clicked item: " + displayName + " in menu: " + title);
            switch (title) {
                case "\u00a76Perks Menu": {
                    this.handleMainMenuClick(player, clickedItem, event.getClickedInventory());
                    break;
                }
                case "\u00a76Perks Admin": {
                    this.handleAdminMenuClick(player, clickedItem);
                    break;
                }
                case "\u00a76Strength Perk": 
                case "\u00a76Invisibility Perk": {
                    this.handleSubcategoryClick(player, title, clickedItem, event.getClickedInventory());
                    break;
                }
                case "\u00a76Perks Admin: Player List": {
                    this.handlePlayerListClick(player, clickedItem);
                    break;
                }
                case "\u00a76Perks Admin: Confirm": {
                    this.handleResetConfirmationClick(player, clickedItem);
                    break;
                }
                default: {
                    if (!title.startsWith("\u00a76Admin: Manage")) break;
                    this.handlePlayerManagementClick(player, clickedItem, title);
                }
            }
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem, Inventory inventory) {
        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equals("\u00a7cAdmin Panel") && player.hasPermission("perks.admin")) {
            this.openAdminMenu(player);
            return;
        }
        switch (displayName) {
            case "\u00a7aSpeed Perk": {
                if (player.hasPermission("perks.buy.speed")) {
                    this.handlePurchase(player, "Speed Perk", 100.0, inventory);
                    break;
                }
                player.sendMessage("\u00a7cYou don't have permission to purchase this perk.");
                break;
            }
            case "\u00a7aJump Boost": {
                if (player.hasPermission("perks.buy.jump")) {
                    this.handlePurchase(player, "Jump Boost", 150.0, inventory);
                    break;
                }
                player.sendMessage("\u00a7cYou don't have permission to purchase this perk.");
                break;
            }
            case "\u00a7aStrength Perk": {
                if (player.hasPermission("perks.view.strength")) {
                    this.openStrengthMenu(player);
                    break;
                }
                player.sendMessage("\u00a7cYou don't have permission to open this category.");
                break;
            }
            case "\u00a7aInvisibility Perk": {
                if (player.hasPermission("perks.view.invisibility")) {
                    this.openInvisibilityMenu(player);
                    break;
                }
                player.sendMessage("\u00a7cYou don't have permission to open this category.");
            }
        }
    }

    private void handleAdminMenuClick(Player player, ItemStack clickedItem) {
        String displayName;
        if (!player.hasPermission("perks.admin")) {
            player.sendMessage("\u00a7cYou don't have permission for admin functions.");
            return;
        }
        switch (displayName = clickedItem.getItemMeta().getDisplayName()) {
            case "\u00a7cBack": {
                this.openMainMenu(player);
                break;
            }
            case "\u00a7aShow All Players": {
                this.openPlayerListMenu(player);
                break;
            }
            case "\u00a7aReset Perks": {
                this.openResetConfirmationMenu(player);
            }
        }
    }

    private void handlePlayerListClick(Player player, ItemStack clickedItem) {
        if (!player.hasPermission("perks.admin")) {
            player.sendMessage("\u00a7cYou don't have permission for admin functions.");
            return;
        }
        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equals("\u00a7cBack")) {
            this.openAdminMenu(player);
            return;
        }
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            String targetPlayerName = displayName.replace("\u00a7e", "");
            Player targetPlayer = Bukkit.getPlayer((String)targetPlayerName);
            if (targetPlayer != null) {
                this.openPlayerManagementMenu(player, targetPlayer);
            } else {
                player.sendMessage("\u00a7cPlayer is not online.");
                this.openPlayerListMenu(player);
            }
        }
    }

    private void handleResetConfirmationClick(Player player, ItemStack clickedItem) {
        if (!player.hasPermission("perks.admin")) {
            player.sendMessage("\u00a7cYou don't have permission for admin functions.");
            return;
        }
        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equals("\u00a7aConfirm")) {
            this.perksManager.resetAllPerks();
            player.sendMessage("\u00a7aAll perks have been reset.");
            Bukkit.getOnlinePlayers().forEach(this::executeRemoveAllPermissions);
            this.openAdminMenu(player);
        } else if (displayName.equals("\u00a7cCancel")) {
            this.openAdminMenu(player);
        }
    }

    private void handlePlayerManagementClick(Player player, ItemStack clickedItem, String title) {
        if (!player.hasPermission("perks.admin")) {
            player.sendMessage("\u00a7cYou don't have permission for admin functions.");
            return;
        }
        String displayName = clickedItem.getItemMeta().getDisplayName();
        String targetPlayerName = title.substring("\u00a76Admin: Manage ".length());
        Player targetPlayer = Bukkit.getPlayer((String)targetPlayerName);
        if (displayName.equals("\u00a7cBack")) {
            this.openPlayerListMenu(player);
            return;
        }
        if (targetPlayer == null) {
            player.sendMessage("\u00a7cThe player is no longer online.");
            this.openPlayerListMenu(player);
            return;
        }
        if (displayName.startsWith("\u00a7cRemove: ")) {
            String perkName = displayName.substring("\u00a7cRemove: ".length());
            this.perksManager.removePerk(targetPlayer.getUniqueId(), perkName);
            player.sendMessage("\u00a7aPerk \u00a7e" + perkName + " \u00a7aremoved from \u00a7e" + targetPlayerName + "\u00a7a.");
            this.executeRemovePermissionCommand(targetPlayer, perkName);
            this.openPlayerManagementMenu(player, targetPlayer);
        } else if (displayName.startsWith("\u00a7aGrant: ")) {
            String perkName = displayName.substring("\u00a7aGrant: ".length());
            this.perksManager.setBoughtPerk(targetPlayer.getUniqueId(), perkName);
            player.sendMessage("\u00a7aPerk \u00a7e" + perkName + " \u00a7agranted to \u00a7e" + targetPlayerName + "\u00a7a.");
            this.executeCommandAfterPurchase(targetPlayer, "\u00a7a" + perkName);
            this.openPlayerManagementMenu(player, targetPlayer);
        }
    }

    private void handleSubcategoryClick(Player player, String title, ItemStack clickedItem, Inventory inventory) {
        double price;
        String cleanPerkName;
        String perkLevel;
        String displayName = clickedItem.getItemMeta().getDisplayName();
        System.out.println("[PerksListener] Subcategory click: " + displayName + " in " + title);
        if (displayName.equals("\u00a7cBack")) {
            this.openMainMenu(player);
            return;
        }
        if (!displayName.startsWith("\u00a7a")) {
            player.sendMessage("\u00a7cThis item cannot be purchased.");
            return;
        }
        String perkType = title.equals("\u00a76Strength Perk") ? "strength" : "invisibility";
        String string = perkLevel = displayName.contains("I") && !displayName.contains("II") ? "1" : "2";
        if (!player.hasPermission("perks.buy." + perkType + "." + perkLevel) && !player.hasPermission("perks.admin")) {
            player.sendMessage("\u00a7cYou don't have permission to purchase this perk.");
            return;
        }
        switch (cleanPerkName = displayName.replace("\u00a7a", "")) {
            case "Strength I": {
                price = 200.0;
                break;
            }
            case "Strength II": {
                price = 300.0;
                break;
            }
            case "Invisibility I": {
                price = 250.0;
                break;
            }
            case "Invisibility II": {
                price = 400.0;
                break;
            }
            default: {
                player.sendMessage("\u00a7cError: Invalid perk.");
                return;
            }
        }
        this.handlePurchase(player, cleanPerkName, price, inventory);
    }

    private void handlePurchase(Player player, String perkName, double price, Inventory inventory) {
        String cleanPerkName = perkName.replace("\u00a7a", "");
        if (cleanPerkName.isEmpty()) {
            player.sendMessage("\u00a7cError: Invalid perk name.");
            return;
        }
        if (this.perksManager.hasBoughtPerk(player.getUniqueId(), cleanPerkName)) {
            player.sendMessage("\u00a7cYou have already purchased this perk.");
            return;
        }
        if (player.hasPermission("perks.free") || player.hasPermission("perks.admin")) {
            this.perksManager.setBoughtPerk(player.getUniqueId(), cleanPerkName);
            player.sendMessage("\u00a7aPerk \u00a7e" + cleanPerkName + " \u00a7areceived for free!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            this.executeCommandAfterPurchase(player, "\u00a7a" + cleanPerkName);
            this.refreshGUI(inventory, player);
            return;
        }
        if (this.economyAPI.withdrawBalance(player, price)) {
            this.perksManager.setBoughtPerk(player.getUniqueId(), cleanPerkName);
            player.sendMessage("\u00a7aPerk \u00a7e" + cleanPerkName + " \u00a7asuccessfully purchased!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            Bukkit.broadcastMessage((String)("\u00a7a" + player.getName() + " has purchased the perk \u00a7e" + cleanPerkName + "\u00a7a!"));
            this.executeCommandAfterPurchase(player, "\u00a7a" + cleanPerkName);
            this.refreshGUI(inventory, player);
        } else {
            player.sendMessage("\u00a7cNot enough money to purchase perk \u00a7e" + cleanPerkName + "\u00a7c.");
        }
    }

    private void refreshGUI(Inventory inventory, Player player) {
        String title = player.getOpenInventory().getTitle();
        if (title.equals("\u00a76Perks Menu")) {
            new PerksCommand(this.economyAPI, this.perksManager).openMainMenu(player);
        } else if (title.equals("\u00a76Strength Perk")) {
            this.openStrengthMenu(player);
        } else if (title.equals("\u00a76Invisibility Perk")) {
            this.openInvisibilityMenu(player);
        }
    }

    private void executeCommandAfterPurchase(Player player, String perkName) {
        String permission = this.getPermissionForPerk(perkName);
        if (permission != null) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)("lp user " + player.getName() + " permission set " + permission + " true"));
        }
    }

    private void executeRemovePermissionCommand(Player player, String perkName) {
        String permission = this.getPermissionForPerk("\u00a7a" + perkName);
        if (permission != null) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)("lp user " + player.getName() + " permission unset " + permission));
        }
    }

    private void executeRemoveAllPermissions(Player player) {
        String[] permissions;
        for (String perm : permissions = new String[]{"perks.effect.speed", "perks.effect.jump", "perks.effect.strength.1", "perks.effect.strength.2", "perks.effect.invisibility.1", "perks.effect.invisibility.2"}) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), (String)("lp user " + player.getName() + " permission unset " + perm));
        }
    }

    private String getPermissionForPerk(String perkName) {
        switch (perkName) {
            case "\u00a7aSpeed Perk": {
                return "perks.effect.speed";
            }
            case "\u00a7aJump Boost": {
                return "perks.effect.jump";
            }
            case "\u00a7aStrength I": {
                return "perks.effect.strength.1";
            }
            case "\u00a7aStrength II": {
                return "perks.effect.strength.2";
            }
            case "\u00a7aInvisibility I": {
                return "perks.effect.invisibility.1";
            }
            case "\u00a7aInvisibility II": {
                return "perks.effect.invisibility.2";
            }
        }
        return null;
    }

    private void openMainMenu(Player player) {
        new PerksCommand(this.economyAPI, this.perksManager).openMainMenu(player);
    }

    private void openAdminMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a76Perks Admin");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        gui.setItem(11, this.createItem(Material.PLAYER_HEAD, "\u00a7aShow All Players", Arrays.asList("\u00a77Show a list of all players", "\u00a77to manage their perks.")));
        gui.setItem(15, this.createItem(Material.BARRIER, "\u00a7aReset Perks", Arrays.asList("\u00a77Reset all perks.")));
        gui.setItem(22, this.createItem(Material.ARROW, "\u00a7cBack"));
        player.openInventory(gui);
    }

    private void openPlayerListMenu(Player player) {
        int lastRowStart;
        int playerCount = Bukkit.getOnlinePlayers().size();
        int invSize = Math.min(54, (playerCount + 8) / 9 * 9 + 9);
        Inventory gui = Bukkit.createInventory(null, (int)invSize, (String)"\u00a76Perks Admin: Player List");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = lastRowStart = invSize - 9; i < invSize; ++i) {
            gui.setItem(i, border);
        }
        int slot = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (slot >= lastRowStart) break;
            gui.setItem(slot, this.createItem(Material.PLAYER_HEAD, "\u00a7e" + onlinePlayer.getName(), Arrays.asList("\u00a77Click to manage this", "\u00a77player's perks.")));
            ++slot;
        }
        gui.setItem(invSize - 5, this.createItem(Material.ARROW, "\u00a7cBack"));
        player.openInventory(gui);
    }

    private void openPlayerManagementMenu(Player admin, Player targetPlayer) {
        Inventory gui = Bukkit.createInventory(null, (int)54, (String)("\u00a76Admin: Manage " + targetPlayer.getName()));
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; ++i) {
            gui.setItem(i, border);
            gui.setItem(45 + i, border);
        }
        gui.setItem(9, this.createItem(Material.EMERALD_BLOCK, "\u00a7aUnlocked Perks"));
        gui.setItem(27, this.createItem(Material.REDSTONE_BLOCK, "\u00a7cAvailable Perks"));
        gui.setItem(49, this.createItem(Material.ARROW, "\u00a7cBack"));
        int ownedSlot = 10;
        int availableSlot = 28;
        for (String perk : this.perksManager.getAllPerks()) {
            Material material = this.getPerkMaterial(perk);
            if (this.perksManager.hasBoughtPerk(targetPlayer.getUniqueId(), perk)) {
                if (ownedSlot >= 27 || ownedSlot % 9 == 0 || ownedSlot % 9 == 8) continue;
                gui.setItem(ownedSlot, this.createItem(material, "\u00a7cRemove: " + perk, Arrays.asList("\u00a77Click to remove this perk.")));
                if (++ownedSlot % 9 != 0) continue;
                ownedSlot += 2;
                continue;
            }
            if (availableSlot >= 45 || availableSlot % 9 == 0 || availableSlot % 9 == 8) continue;
            gui.setItem(availableSlot, this.createItem(material, "\u00a7aGrant: " + perk, Arrays.asList("\u00a77Click to grant this perk.")));
            if (++availableSlot % 9 != 0) continue;
            availableSlot += 2;
        }
        admin.openInventory(gui);
    }

    private void openResetConfirmationMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a76Perks Admin: Confirm");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        gui.setItem(11, this.createItem(Material.LIME_WOOL, "\u00a7aConfirm", Arrays.asList("\u00a77Confirm to reset all perks.")));
        gui.setItem(15, this.createItem(Material.RED_WOOL, "\u00a7cCancel"));
        player.openInventory(gui);
    }

    private void openStrengthMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a76Strength Perk");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        gui.setItem(22, this.createItem(Material.ARROW, "\u00a7cBack"));
        if (player.hasPermission("perks.buy.strength.1") || player.hasPermission("perks.admin")) {
            this.addPerk(gui, 11, Material.IRON_SWORD, "\u00a7aStrength I", "\u00a77Slightly increased attack damage.", 200.0, player.getUniqueId());
        }
        if (player.hasPermission("perks.buy.strength.2") || player.hasPermission("perks.admin")) {
            this.addPerk(gui, 15, Material.DIAMOND_SWORD, "\u00a7aStrength II", "\u00a77Greatly increased attack damage.", 300.0, player.getUniqueId());
        }
        player.openInventory(gui);
    }

    private void openInvisibilityMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a76Invisibility Perk");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        gui.setItem(22, this.createItem(Material.ARROW, "\u00a7cBack"));
        if (player.hasPermission("perks.buy.invisibility.1") || player.hasPermission("perks.admin")) {
            this.addPerk(gui, 11, Material.POTION, "\u00a7aInvisibility I", "\u00a77Makes you invisible for 1 minute.", 250.0, player.getUniqueId());
        }
        if (player.hasPermission("perks.buy.invisibility.2") || player.hasPermission("perks.admin")) {
            this.addPerk(gui, 15, Material.SPLASH_POTION, "\u00a7aInvisibility II", "\u00a77Makes you invisible for 3 minutes.", 400.0, player.getUniqueId());
        }
        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material material, String name) {
        return this.createItem(material, name, null);
    }

    private void addPerk(Inventory gui, int slot, Material material, String name, String description, double price, UUID playerId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        String cleanName = name.replace("\u00a7a", "");
        meta.setLore(Arrays.asList(description, this.perksManager.hasBoughtPerk(playerId, cleanName) ? "\u00a7aBOUGHT" : "\u00a76Price: " + price + " Coins"));
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }

    private Material getPerkMaterial(String perkName) {
        switch (perkName) {
            case "Speed Perk": {
                return Material.FEATHER;
            }
            case "Jump Boost": {
                return Material.RABBIT_FOOT;
            }
            case "Strength I": {
                return Material.IRON_SWORD;
            }
            case "Strength II": {
                return Material.DIAMOND_SWORD;
            }
            case "Invisibility I": {
                return Material.POTION;
            }
            case "Invisibility II": {
                return Material.SPLASH_POTION;
            }
        }
        return Material.BOOK;
    }
}

