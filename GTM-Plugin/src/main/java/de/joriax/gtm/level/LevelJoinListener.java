package de.joriax.gtm.level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LevelJoinListener implements Listener {

    private final LevelManager levelManager;

    public LevelJoinListener(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Load or create player data
        levelManager.loadPlayer(player.getUniqueId(), player.getName());

        // Give clock item to show level if they don't have one
        if (!hasLevelClock(player)) {
            giveLevelClock(player);
        }
    }

    private boolean hasLevelClock(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.CLOCK) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.getDisplayName().contains("Level")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void giveLevelClock(Player player) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        if (meta != null) {
            int level = levelManager.getLevel(player);
            meta.setDisplayName(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + level);
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Right-click to see your level stats",
                    ChatColor.GRAY + "XP: " + levelManager.getXP(player)
            ));
            meta.setUnbreakable(true);
            clock.setItemMeta(meta);
        }
        player.getInventory().addItem(clock);
    }
}
