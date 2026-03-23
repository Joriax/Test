/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Sound
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package de.joriax.levelSystem;

import de.joriax.levelSystem.PlayerData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LevelSystem
extends JavaPlugin
implements Listener {
    private HashMap<UUID, PlayerData> playerDataMap = new HashMap();
    private Connection connection;
    private String dbUrl = "jdbc:mysql://localhost:3306/levelsystem_db";
    private String dbUser = "levelsystem_db";
    private String dbPassword = "levelsystem_db";

    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getCommand("level").setExecutor((CommandExecutor)this);
        this.connectDatabase();
    }

    public static LevelSystem getInstance() {
        return (LevelSystem)Bukkit.getPluginManager().getPlugin("LevelSystem");
    }

    public int getPlayerLevel(Player player) {
        PlayerData data = this.getPlayerData(player.getUniqueId());
        return data != null ? data.getLevel() : 1;
    }

    public float getPlayerXpProgress(Player player) {
        PlayerData data = this.getPlayerData(player.getUniqueId());
        return data != null ? (float)data.getXp() / (float)data.getXpToNextLevel() : 0.0f;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("level")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                PlayerData data = this.getPlayerData(player.getUniqueId());
                if (data != null) {
                    player.sendMessage("Level: " + data.getLevel() + " | XP: " + data.getXp() + "/" + data.getXpToNextLevel());
                    this.updateXpBar(player);
                } else {
                    player.sendMessage("Deine Daten konnten nicht geladen werden.");
                }
            } else {
                sender.sendMessage("Dieser Befehl kann nur von einem Spieler verwendet werden.");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setlevel")) {
            if (sender.hasPermission("levelsystem.setlevel")) {
                if (args.length == 2) {
                    int newLevel;
                    Player target = Bukkit.getPlayer((String)args[0]);
                    try {
                        newLevel = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Bitte gib eine g\u00fcltige Zahl f\u00fcr das Level ein.");
                        return true;
                    }
                    if (target != null) {
                        PlayerData data = this.getPlayerData(target.getUniqueId());
                        data.setLevel(newLevel);
                        data.setXp(0);
                        this.updateXpBar(target);
                        sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast " + target.getName() + " auf Level " + newLevel + " gesetzt.");
                        target.sendMessage(String.valueOf(ChatColor.GREEN) + "Dein Level wurde auf " + newLevel + " gesetzt!");
                    } else {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Spieler nicht gefunden.");
                    }
                } else {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "Verwendung: /setlevel <Spieler> <Level>");
                }
                return true;
            }
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Du hast keine Berechtigung, diesen Befehl auszuf\u00fchren.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("givexp")) {
            if (sender.hasPermission("levelsystem.givexp")) {
                if (args.length == 2) {
                    int amount;
                    Player target = Bukkit.getPlayer((String)args[0]);
                    try {
                        amount = Integer.parseInt(args[1]);
                    }
                    catch (NumberFormatException e) {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Bitte gib eine g\u00fcltige Zahl f\u00fcr die XP ein.");
                        return true;
                    }
                    if (target != null) {
                        PlayerData data = this.getPlayerData(target.getUniqueId());
                        data.addXp(amount);
                        this.updateXpBar(target);
                        sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast " + amount + " XP an " + target.getName() + " gegeben.");
                        target.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast " + amount + " XP erhalten!");
                    } else {
                        sender.sendMessage(String.valueOf(ChatColor.RED) + "Spieler nicht gefunden.");
                    }
                } else {
                    sender.sendMessage(String.valueOf(ChatColor.RED) + "Verwendung: /givexp <Spieler> <Menge>");
                }
                return true;
            }
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Du hast keine Berechtigung, diesen Befehl auszuf\u00fchren.");
            return true;
        }
        return false;
    }

    public void onDisable() {
        for (UUID uuid : this.playerDataMap.keySet()) {
            PlayerData data = this.playerDataMap.get(uuid);
            if (data == null) continue;
            data.saveToDatabase(uuid, this.connection);
        }
        this.playerDataMap.clear();
        this.disconnectDatabase();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerDataMap.get(uuid);
    }

    private void connectDatabase() {
        try {
            this.connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
            this.getLogger().info("Datenbankverbindung erfolgreich!");
        }
        catch (SQLException e) {
            this.getLogger().severe("Datenbankverbindung fehlgeschlagen: " + e.getMessage());
        }
    }

    private void disconnectDatabase() {
        if (this.connection != null) {
            try {
                this.connection.close();
                this.getLogger().info("Datenbankverbindung geschlossen.");
            }
            catch (SQLException e) {
                this.getLogger().severe("Fehler beim Schlie\u00dfen der Datenbankverbindung: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = new PlayerData();
        data.loadFromDatabase(player.getUniqueId(), this.connection);
        this.playerDataMap.put(player.getUniqueId(), data);
        this.startXpGain(player);
        this.updateXpBar(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = this.playerDataMap.get(player.getUniqueId());
        if (data != null) {
            data.saveToDatabase(player.getUniqueId(), this.connection);
            this.playerDataMap.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        PlayerData data;
        Player killer = event.getEntity().getKiller();
        if (killer != null && (data = this.playerDataMap.get(killer.getUniqueId())) != null) {
            data.addXp(50);
            this.updateXpBar(killer);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setKeepLevel(true);
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.updateXpBar(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Player player;
        PlayerData data;
        if (event.getDamager() instanceof Player && event.getEntity().getName().equalsIgnoreCase(String.valueOf(ChatColor.RED) + "Cop") && (data = this.playerDataMap.get((player = (Player)event.getDamager()).getUniqueId())) != null) {
            data.addXp(100);
            this.updateXpBar(player);
        }
    }

    private void startXpGain(final Player player) {
        new BukkitRunnable(this){
            final /* synthetic */ LevelSystem this$0;
            {
                this.this$0 = this$0;
            }

            public void run() {
                PlayerData data = this.this$0.playerDataMap.get(player.getUniqueId());
                if (data != null) {
                    data.addXp(10);
                    this.this$0.updateXpBar(player);
                }
            }
        }.runTaskTimer((Plugin)this, 0L, 1200L);
    }

    private void updateXpBar(Player player) {
        PlayerData data = this.getPlayerData(player.getUniqueId());
        if (data != null) {
            player.setLevel(data.getLevel());
            float progress = (float)data.getXp() / (float)data.getXpToNextLevel();
            player.setExp(progress);
            if (data.hasLeveledUp()) {
                player.sendMessage("\u00a7aHerzlichen Gl\u00fcckwunsch! Du hast Level " + data.getLevel() + " erreicht!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                Bukkit.getLogger().info(player.getName() + " hat Level " + data.getLevel() + " erreicht!");
                data.resetLeveledUp();
            }
        }
    }
}

