/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Guns;

import Guns.AmmoType;
import Guns.WeaponManager;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AmmoCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl ausf\u00fchren!");
            return false;
        }
        Player player = (Player)sender;
        AmmoCommand.openAmmoGUI(player);
        return true;
    }

    public static void openAmmoGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)(String.valueOf(ChatColor.DARK_GRAY) + "Dein Munitionsbeutel"));
        ItemStack pistolItem = AmmoCommand.createDisplayItem(player, Material.IRON_NUGGET, String.valueOf(ChatColor.GRAY) + "Pistol Ammo", AmmoType.PISTOL);
        ItemStack smgItem = AmmoCommand.createDisplayItem(player, Material.GOLD_NUGGET, String.valueOf(ChatColor.YELLOW) + "SMG Ammo", AmmoType.SMG);
        ItemStack arItem = AmmoCommand.createDisplayItem(player, Material.COPPER_INGOT, String.valueOf(ChatColor.RED) + "AR Ammo", AmmoType.AR);
        ItemStack sniperItem = AmmoCommand.createDisplayItem(player, Material.DIAMOND, String.valueOf(ChatColor.BLUE) + "Sniper Ammo", AmmoType.SNIPER);
        gui.setItem(10, pistolItem);
        gui.setItem(12, smgItem);
        gui.setItem(14, arItem);
        gui.setItem(16, sniperItem);
        player.openInventory(gui);
    }

    private static ItemStack createDisplayItem(Player player, Material material, String displayName, AmmoType ammoType) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int ammoAmount = WeaponManager.getAmmo(player, ammoType);
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Deine Munition: " + ammoAmount, String.valueOf(ChatColor.GRAY) + "Klicke, um 64 Munition zu droppen!"));
            item.setItemMeta(meta);
        }
        return item;
    }
}

