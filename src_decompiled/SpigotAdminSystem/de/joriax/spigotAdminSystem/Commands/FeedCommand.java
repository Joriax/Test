/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Commands;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand
implements CommandExecutor {
    private final SpigotAdminSystem plugin;
    private final Map<Player, Long> cooldowns = new HashMap<Player, Long>();
    private final Map<String, Integer> permissionCooldowns = new HashMap<String, Integer>();

    public FeedCommand(SpigotAdminSystem plugin) {
        this.plugin = plugin;
        this.permissionCooldowns.put("spigot.feed.vip", 180000);
        this.permissionCooldowns.put("spigot.feed.premium", 120000);
        this.permissionCooldowns.put("spigot.feed.default", 60000);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        long lastFeedTime;
        if (!(sender instanceof Player)) {
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.hasPermission("spigot.feed.use")) {
            player.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            return true;
        }
        long currentTime = System.currentTimeMillis();
        long cooldownTime = this.getCooldownTime(player);
        if (this.cooldowns.containsKey(player) && currentTime - (lastFeedTime = this.cooldowns.get(player).longValue()) < cooldownTime) {
            long remainingTime = (cooldownTime - (currentTime - lastFeedTime)) / 1000L;
            player.sendMessage(SpigotAdminSystem.getPrefix() + "You must wait " + remainingTime + " seconds before you can use the feed command again.");
            return true;
        }
        player.setFoodLevel(20);
        player.setSaturation(10.0f);
        player.sendMessage(SpigotAdminSystem.getPrefix() + "You fed yourself.");
        this.cooldowns.put(player, currentTime);
        return true;
    }

    private long getCooldownTime(Player player) {
        for (Map.Entry<String, Integer> entry : this.permissionCooldowns.entrySet()) {
            if (!player.hasPermission(entry.getKey())) continue;
            return entry.getValue().intValue();
        }
        return this.permissionCooldowns.get("spigot.feed.default").intValue();
    }
}

