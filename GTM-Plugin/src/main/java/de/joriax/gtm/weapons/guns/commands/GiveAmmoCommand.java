package de.joriax.gtm.weapons.guns.commands;

import de.joriax.gtm.weapons.guns.AmmoType;
import de.joriax.gtm.weapons.guns.WeaponManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveAmmoCommand implements CommandExecutor {

    private final WeaponManager weaponManager;

    public GiveAmmoCommand(WeaponManager weaponManager) {
        this.weaponManager = weaponManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gtm.admin.giveammo")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveammo <player> <type> <amount>");
            sender.sendMessage(ChatColor.GRAY + "Ammo types: PISTOL, SMG, AR, SNIPER");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + args[0] + "' is not online.");
            return true;
        }

        AmmoType ammoType = AmmoType.fromString(args[1].toUpperCase());
        if (ammoType == null) {
            sender.sendMessage(ChatColor.RED + "Unknown ammo type: " + args[1]);
            sender.sendMessage(ChatColor.GRAY + "Ammo types: PISTOL, SMG, AR, SNIPER");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount. Must be a positive number.");
            return true;
        }

        weaponManager.addReserveAmmo(target.getUniqueId(), ammoType, amount);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + "x " + ammoType.getDisplayName() + " to " + target.getName() + ".");
        target.sendMessage(ChatColor.GOLD + "You received " + ChatColor.YELLOW + amount + "x " + ammoType.getDisplayName() + ChatColor.GOLD + "!");
        return true;
    }
}
