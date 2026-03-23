/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.levelSystem.LevelSystem
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 *  org.bukkit.scoreboard.Team
 */
package de.joriax.spigotAdminSystem.Utils.Scoreboard.Visual;

import de.joriax.levelSystem.LevelSystem;
import de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager.EconomyManager;
import de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    private final LevelManager levelManager;

    public ScoreboardHandler(JavaPlugin plugin, EconomyManager economyManager, LevelManager levelManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.levelManager = levelManager;
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("stats", "dummy", String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.BOLD) + "GTM Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team levelTeam = scoreboard.registerNewTeam("level");
        Team xpTeam = scoreboard.registerNewTeam("xp");
        Team moneyTeam = scoreboard.registerNewTeam("money");
        Team onlineTeam = scoreboard.registerNewTeam("online");
        levelTeam.addEntry(String.valueOf(ChatColor.BLUE) + String.valueOf(ChatColor.RESET));
        xpTeam.addEntry(String.valueOf(ChatColor.LIGHT_PURPLE) + String.valueOf(ChatColor.RESET));
        moneyTeam.addEntry(String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.RESET));
        onlineTeam.addEntry(String.valueOf(ChatColor.WHITE) + String.valueOf(ChatColor.RESET));
        objective.getScore(String.valueOf(ChatColor.GRAY) + "Level: ").setScore(6);
        objective.getScore(String.valueOf(ChatColor.BLUE) + String.valueOf(ChatColor.RESET)).setScore(5);
        objective.getScore(String.valueOf(ChatColor.GRAY) + "XP: ").setScore(4);
        objective.getScore(String.valueOf(ChatColor.LIGHT_PURPLE) + String.valueOf(ChatColor.RESET)).setScore(3);
        objective.getScore(String.valueOf(ChatColor.GRAY) + "Money: ").setScore(2);
        objective.getScore(String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.RESET)).setScore(1);
        objective.getScore(String.valueOf(ChatColor.GRAY) + "Online: ").setScore(0);
        objective.getScore(String.valueOf(ChatColor.WHITE) + String.valueOf(ChatColor.RESET)).setScore(-1);
        objective.getScore(String.valueOf(ChatColor.GRAY) + "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500").setScore(-2);
        player.setScoreboard(scoreboard);
        this.updateScoreboard(player);
    }

    public void updateScoreboard(final Player player) {
        new BukkitRunnable(this){
            final /* synthetic */ ScoreboardHandler this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                Scoreboard scoreboard = player.getScoreboard();
                if (scoreboard == null) {
                    this.this$0.createScoreboard(player);
                    return;
                }
                Team levelTeam = scoreboard.getTeam("level");
                Team xpTeam = scoreboard.getTeam("xp");
                Team moneyTeam = scoreboard.getTeam("money");
                Team onlineTeam = scoreboard.getTeam("online");
                if (levelTeam == null || xpTeam == null || moneyTeam == null || onlineTeam == null) {
                    this.this$0.createScoreboard(player);
                    return;
                }
                LevelSystem levelSystem = LevelSystem.getInstance();
                if (levelSystem != null) {
                    int level = levelSystem.getPlayerLevel(player);
                    float xpProgress = levelSystem.getPlayerXpProgress(player);
                    levelTeam.setSuffix(String.valueOf(ChatColor.AQUA) + "Lv. " + level);
                    xpTeam.setSuffix(String.valueOf(ChatColor.DARK_PURPLE) + Math.round(xpProgress * 100.0f) + "%");
                } else {
                    levelTeam.setSuffix(String.valueOf(ChatColor.RED) + "N/A");
                    xpTeam.setSuffix(String.valueOf(ChatColor.RED) + "N/A");
                }
                if (this.this$0.economyManager != null) {
                    double balance = this.this$0.economyManager.getBalance(player);
                    moneyTeam.setSuffix(String.valueOf(ChatColor.GREEN) + String.format("$%.2f", balance));
                } else {
                    moneyTeam.setSuffix(String.valueOf(ChatColor.RED) + "$0.00");
                }
                int onlinePlayers = Bukkit.getOnlinePlayers().size();
                onlineTeam.setSuffix(String.valueOf(ChatColor.WHITE) + onlinePlayers);
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L);
    }
}

