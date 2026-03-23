package de.joriax.gtm.weapons.guns.commands;

import de.joriax.gtm.weapons.guns.AmmoType;
import de.joriax.gtm.weapons.guns.WeaponManager;
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

import java.util.Arrays;

public class AmmoCommand implements CommandExecutor {

    private final WeaponManager weaponManager;
    private static final String GUI_TITLE = ChatColor.DARK_RED + "Ammo Inventory";

    public AmmoCommand(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("gtm.weapons.ammo")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        openAmmoGUI(player);
        return true;
    }

    private void openAmmoGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        AmmoType[] types = AmmoType.values();
        Material[] materials = {Material.IRON_NUGGET, Material.GOLD_NUGGET, Material.EMERALD, Material.DIAMOND};

        for (int i = 0; i < types.length; i++) {
            AmmoType type = types[i];
            int amount = weaponManager.getReserveAmmo(player.getUniqueId(), type);

            ItemStack item = new ItemStack(materials[i % materials.length]);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + type.getDisplayName());
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Reserve Ammo: " + ChatColor.WHITE + amount,
                        ChatColor.DARK_GRAY + "Type: " + type.name()
                ));
                item.setItemMeta(meta);
            }
            inv.setItem(i * 2 + 1, item);
        }

        player.openInventory(inv);
    }
}
