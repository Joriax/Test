/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FixCommand
implements CommandExecutor {
    private final SpigotAdminSystem plugin;
    private final Map<Player, Long> cooldowns = new HashMap<Player, Long>();

    public FixCommand(SpigotAdminSystem plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("spigot.fix.use")) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        long cooldownTime = this.getCooldownTime(player);
        long lastUsed = this.cooldowns.getOrDefault(player, 0L);
        if (System.currentTimeMillis() - lastUsed < cooldownTime) {
            long timeLeft = (cooldownTime - (System.currentTimeMillis() - lastUsed)) / 1000L;
            player.sendMessage(SpigotAdminSystem.getPrefix() + "You must wait " + timeLeft + " seconds before you can use this command again.");
            return true;
        }
        this.cooldowns.put(player, System.currentTimeMillis());
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + "You are not holding an item in your hand.");
            return true;
        }
        item.setDurability((short)0);
        player.sendMessage(SpigotAdminSystem.getPrefix() + "Your item has been fixed.");
        return true;
    }

    private long getCooldownTime(Player player) {
        if (player.hasPermission("fix.premium")) {
            return 120000L;
        }
        if (player.hasPermission("fix.vip")) {
            return 180000L;
        }
        if (player.hasPermission("fix.admin")) {
            return 0L;
        }
        return 300000L;
    }
}

