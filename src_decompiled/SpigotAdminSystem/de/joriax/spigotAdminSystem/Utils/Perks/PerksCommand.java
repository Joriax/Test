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
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package de.joriax.spigotAdminSystem.Utils.Perks;

import de.joriax.economy.EconomyAPI;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksManager;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PerksCommand
implements CommandExecutor {
    private final EconomyAPI economyAPI;
    private final PerksManager perksManager;

    public PerksCommand(EconomyAPI economyAPI, PerksManager perksManager) {
        this.economyAPI = economyAPI;
        this.perksManager = perksManager;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("\u00a7cOnly players can use this command.");
            return false;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("perks.use")) {
            player.sendMessage("\u00a7cYou don't have permission to open the perks menu.");
            return false;
        }
        this.openMainMenu(player);
        return true;
    }

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"\u00a76Perks Menu");
        ItemStack border = this.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        if (player.hasPermission("perks.buy.speed")) {
            this.addPerk(gui, 10, Material.FEATHER, "\u00a7aSpeed Perk", "\u00a77Increases your speed.", 100.0, player.getUniqueId());
        }
        if (player.hasPermission("perks.buy.jump")) {
            this.addPerk(gui, 12, Material.RABBIT_FOOT, "\u00a7aJump Boost", "\u00a77Increases your jump height.", 150.0, player.getUniqueId());
        }
        if (player.hasPermission("perks.view.strength")) {
            this.addCategory(gui, 14, Material.IRON_SWORD, "\u00a7aStrength Perk", "\u00a77Increases your attack damage.");
        }
        if (player.hasPermission("perks.view.invisibility")) {
            this.addCategory(gui, 16, Material.GLASS, "\u00a7aInvisibility Perk", "\u00a77Makes you invisible.");
        }
        if (player.hasPermission("perks.admin")) {
            gui.setItem(22, this.createItem(Material.COMMAND_BLOCK, "\u00a7cAdmin Panel", Arrays.asList("\u00a77Open the admin panel")));
        }
        player.openInventory(gui);
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

    private void addCategory(Inventory gui, int slot, Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(description));
        item.setItemMeta(meta);
        gui.setItem(slot, item);
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
}

