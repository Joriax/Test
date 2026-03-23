package de.joriax.gtm.level;

import de.joriax.gtm.economy.EconomyAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LevelListener implements Listener {

    private final LevelManager levelManager;
    private final EconomyAPI economyAPI;

    private static final int XP_PER_KILL = 25;
    private static final double MONEY_PER_KILL = 250.0;

    public LevelListener(LevelManager levelManager, EconomyAPI economyAPI) {
        this.levelManager = levelManager;
        this.economyAPI = economyAPI;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.equals(victim)) {
            // Award XP to killer
            levelManager.addXP(killer, XP_PER_KILL);
            killer.sendMessage(ChatColor.GREEN + "+" + XP_PER_KILL + " XP for killing " + victim.getName() + "!");

            // Award money to killer
            economyAPI.addBalance(killer, MONEY_PER_KILL);
            killer.sendMessage(ChatColor.GREEN + "+$" + String.format("%.0f", MONEY_PER_KILL) + " for the kill!");

            // Update level clock for killer
            updateLevelClock(killer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        levelManager.unloadPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.CLOCK) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (!meta.getDisplayName().contains("Level")) return;

        // Show level stats on right click
        org.bukkit.event.block.Action action = event.getAction();
        if (action == org.bukkit.event.block.Action.RIGHT_CLICK_AIR ||
                action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            int level = levelManager.getLevel(player);
            int xp = levelManager.getXP(player);
            int xpNeeded = levelManager.getXPRequiredForNextLevel(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "=== Your Level Stats ===");
            player.sendMessage(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + level);
            player.sendMessage(ChatColor.GOLD + "XP: " + ChatColor.YELLOW + xp + ChatColor.GRAY + " / " + xpNeeded);

            // Update the clock display
            updateLevelClock(player);
        }
    }

    private void updateLevelClock(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.CLOCK) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains("Level")) {
                    int level = levelManager.getLevel(player);
                    int xp = levelManager.getXP(player);
                    int xpNeeded = levelManager.getXPRequiredForNextLevel(player.getUniqueId());
                    meta.setDisplayName(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + level);
                    meta.setLore(Arrays.asList(
                            ChatColor.GRAY + "Right-click to see your level stats",
                            ChatColor.GRAY + "XP: " + xp + " / " + xpNeeded
                    ));
                    item.setItemMeta(meta);
                    break;
                }
            }
        }
    }
}
