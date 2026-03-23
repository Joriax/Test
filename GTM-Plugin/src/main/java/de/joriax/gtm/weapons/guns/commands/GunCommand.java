package de.joriax.gtm.weapons.guns.commands;

import de.joriax.gtm.weapons.guns.Weapon;
import de.joriax.gtm.weapons.guns.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GunCommand implements CommandExecutor {

    private final WeaponManager weaponManager;

    public GunCommand(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gtm.admin.giveweapon")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveweapon <player> <weapon>");
            sender.sendMessage(ChatColor.GRAY + "Available weapons: " + String.join(", ",
                    weaponManager.getWeapons().stream()
                            .map(Weapon::getName)
                            .toArray(String[]::new)));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        String weaponName = args[1].toLowerCase();
        Weapon weapon = weaponManager.getWeapon(weaponName);
        if (weapon == null) {
            sender.sendMessage(ChatColor.RED + "Unknown weapon: " + weaponName);
            sender.sendMessage(ChatColor.GRAY + "Available weapons: " + String.join(", ",
                    weaponManager.getWeapons().stream()
                            .map(Weapon::getName)
                            .toArray(String[]::new)));
            return true;
        }

        // Give weapon with full magazine
        ItemStack item = weapon.createItem();
        weaponManager.setMagazineAmmo(target.getUniqueId(), weapon.getName(), weapon.getMagazineSize());

        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Gave " + weapon.getDisplayName() + " to " + target.getName() + ".");
        target.sendMessage(ChatColor.GOLD + "You received a " + ChatColor.YELLOW + weapon.getDisplayName() + ChatColor.GOLD + "!");
        return true;
    }
}
