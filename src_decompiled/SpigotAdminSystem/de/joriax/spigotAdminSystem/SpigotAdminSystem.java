/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.spigotAdminSystem;

import de.joriax.economy.EconomyAPI;
import de.joriax.spigotAdminSystem.CommandSpy.CommandListener;
import de.joriax.spigotAdminSystem.CommandSpy.CommandSpy;
import de.joriax.spigotAdminSystem.Commands.ChatMuteCommand;
import de.joriax.spigotAdminSystem.Commands.CheckCPSCommand;
import de.joriax.spigotAdminSystem.Commands.ClearChatCommand;
import de.joriax.spigotAdminSystem.Commands.DayCommand;
import de.joriax.spigotAdminSystem.Commands.DayLightCommand;
import de.joriax.spigotAdminSystem.Commands.FeedCommand;
import de.joriax.spigotAdminSystem.Commands.FixAllCommand;
import de.joriax.spigotAdminSystem.Commands.FixCommand;
import de.joriax.spigotAdminSystem.Commands.FlySpeedCommand;
import de.joriax.spigotAdminSystem.Commands.GCCommand;
import de.joriax.spigotAdminSystem.Commands.GameModeCommand;
import de.joriax.spigotAdminSystem.Commands.GetPosCommand;
import de.joriax.spigotAdminSystem.Commands.GodCommand;
import de.joriax.spigotAdminSystem.Commands.HealCommand;
import de.joriax.spigotAdminSystem.Commands.InvSee;
import de.joriax.spigotAdminSystem.Commands.LocalBroadcastCommand;
import de.joriax.spigotAdminSystem.Commands.NightCommand;
import de.joriax.spigotAdminSystem.Commands.PingCommand;
import de.joriax.spigotAdminSystem.Commands.PlaytimeCommand;
import de.joriax.spigotAdminSystem.Commands.PrivateClearChatCommand;
import de.joriax.spigotAdminSystem.Commands.SeePingCommand;
import de.joriax.spigotAdminSystem.Commands.SeenCommand;
import de.joriax.spigotAdminSystem.Commands.TPCommand;
import de.joriax.spigotAdminSystem.Commands.TPHereCommand;
import de.joriax.spigotAdminSystem.Commands.WalkspeedCommand;
import de.joriax.spigotAdminSystem.Listener.CPSListener;
import de.joriax.spigotAdminSystem.Listener.ChatListener;
import de.joriax.spigotAdminSystem.Listener.PlayerQuitListener;
import de.joriax.spigotAdminSystem.Manager.CPSManager;
import de.joriax.spigotAdminSystem.Manager.DayLightManager;
import de.joriax.spigotAdminSystem.Manager.MySQLManager;
import de.joriax.spigotAdminSystem.Utils.BlockedCommands.BlockCommandListener;
import de.joriax.spigotAdminSystem.Utils.ClearLag.ClearLagGUI;
import de.joriax.spigotAdminSystem.Utils.ClearLag.ClearLagManager;
import de.joriax.spigotAdminSystem.Utils.Listeners.PlayerDeathListener;
import de.joriax.spigotAdminSystem.Utils.Listeners.PlayerKillListener;
import de.joriax.spigotAdminSystem.Utils.Maintenance.MaintenanceGUI;
import de.joriax.spigotAdminSystem.Utils.Maintenance.MaintenanceManager;
import de.joriax.spigotAdminSystem.Utils.Near.NearSystem;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksCommand;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksListener;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksManager;
import de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager.EconomyManager;
import de.joriax.spigotAdminSystem.Utils.Scoreboard.Manager.LevelManager;
import de.joriax.spigotAdminSystem.Utils.Scoreboard.Visual.ScoreboardHandler;
import de.joriax.spigotAdminSystem.Utils.TPA.MovementListener;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAAdminCommand;
import de.joriax.spigotAdminSystem.Utils.TPA.TPACCEPTCommand;
import de.joriax.spigotAdminSystem.Utils.TPA.TPACommand;
import de.joriax.spigotAdminSystem.Utils.TPA.TPADENYCommand;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAHERECommand;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAListener;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAManager;
import de.joriax.spigotAdminSystem.Utils.TPA.TPAToggleCommand;
import de.joriax.spigotAdminSystem.Utils.Time.TimeGUI;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.ConfirmGUI;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.GUIManager;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsCommand;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import de.joriax.spigotAdminSystem.Utils.Weather.WeatherGUI;
import de.joriax.spigotAdminSystem.Vanish.Command.VanishCommand;
import de.joriax.spigotAdminSystem.Vanish.Manager.VanishManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotAdminSystem
extends JavaPlugin
implements Listener {
    private final Map<String, Long> lastSeen = new HashMap<String, Long>();
    private MySQLManager mysqlManager;
    private HashMap<UUID, Long> sessionStartTime;
    private long startTime;
    private static SpigotAdminSystem instance;
    private EconomyManager economyManager;
    private EconomyAPI economyAPI;
    private LevelManager levelManager;
    private ScoreboardHandler scoreboardHandler;
    private ClearLagManager clearLagManager;
    private MaintenanceManager maintenanceManager;
    private PerksManager perksManager;
    private TPAManager tpaManager;
    private CPSManager cpsManager;
    private DayLightManager daylightManager;
    private static final String prefix;

    public void onEnable() {
        if (this.setupEconomy()) {
            instance = this;
            this.startTime = System.currentTimeMillis();
            InvSee invSee = new InvSee(this);
            UtilsConfig.setupConfig(this);
            this.economyManager = new EconomyManager(this, this.economyAPI);
            this.levelManager = new LevelManager(this);
            this.scoreboardHandler = new ScoreboardHandler(this, this.economyManager, this.levelManager);
            this.clearLagManager = new ClearLagManager(this, prefix);
            this.clearLagManager.startClearLagTask();
            this.maintenanceManager = new MaintenanceManager(this);
            this.perksManager = new PerksManager(this);
            this.tpaManager = new TPAManager();
            String host = "localhost:3306";
            String database = "spigotadminsys";
            String username = "spigotadminsys";
            String password = "spigotadminsys";
            this.mysqlManager = new MySQLManager(host, database, username, password);
            this.mysqlManager.connect();
            this.sessionStartTime = new HashMap();
            this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
            this.cpsManager = new CPSManager();
            this.daylightManager = new DayLightManager(this);
            VanishCommand vanishCommand = new VanishCommand(this);
            VanishManager vanishManager = new VanishManager(this, vanishCommand);
            TPAListener tpaListener = new TPAListener(this.tpaManager, this);
            this.getServer().getPluginManager().registerEvents((Listener)new CPSListener(this.cpsManager), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new TPAListener(this.tpaManager, this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new PlayerQuitListener(this.mysqlManager, this.sessionStartTime, this.lastSeen), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ChatListener(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new MovementListener(this.tpaManager), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)invSee, (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)vanishCommand, (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new CommandSpy(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new CommandListener(this), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new GUIManager(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new WeatherGUI(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new TimeGUI(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ConfirmGUI(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new ClearLagGUI(this.clearLagManager), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new BlockCommandListener(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new PlayerKillListener(this.economyAPI), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new PlayerDeathListener(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new MaintenanceGUI(), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)new PerksListener(this.economyAPI, this.perksManager), (Plugin)this);
            this.getServer().getPluginManager().registerEvents((Listener)tpaListener, (Plugin)this);
            this.getCommand("flyspeed").setExecutor((CommandExecutor)new FlySpeedCommand());
            this.getCommand("heal").setExecutor((CommandExecutor)new HealCommand());
            this.getCommand("feed").setExecutor((CommandExecutor)new FeedCommand(this));
            this.getCommand("tp").setExecutor((CommandExecutor)new TPCommand());
            this.getCommand("gm").setExecutor((CommandExecutor)new GameModeCommand());
            this.getCommand("fix").setExecutor((CommandExecutor)new FixCommand(this));
            this.getCommand("fixall").setExecutor((CommandExecutor)new FixAllCommand(this));
            this.getCommand("checkcps").setExecutor((CommandExecutor)new CheckCPSCommand(this.cpsManager));
            this.getCommand("tpa").setExecutor((CommandExecutor)new TPACommand(this.tpaManager));
            this.getCommand("tpaccept").setExecutor((CommandExecutor)new TPACCEPTCommand(this.tpaManager, new TPAListener(this.tpaManager, this)));
            this.getCommand("tpahere").setExecutor((CommandExecutor)new TPAHERECommand(this.tpaManager));
            this.getCommand("tphere").setExecutor((CommandExecutor)new TPHereCommand());
            this.getCommand("seen").setExecutor((CommandExecutor)new SeenCommand(this.lastSeen));
            this.getCommand("getpos").setExecutor((CommandExecutor)new GetPosCommand());
            this.getCommand("god").setExecutor((CommandExecutor)new GodCommand());
            this.getCommand("ping").setExecutor((CommandExecutor)new PingCommand());
            this.getCommand("seeping").setExecutor((CommandExecutor)new SeePingCommand());
            this.getCommand("lbc").setExecutor((CommandExecutor)new LocalBroadcastCommand());
            this.getCommand("playtime").setExecutor((CommandExecutor)new PlaytimeCommand(this.mysqlManager, this.sessionStartTime));
            this.getCommand("cc").setExecutor((CommandExecutor)new ClearChatCommand());
            this.getCommand("pcc").setExecutor((CommandExecutor)new PrivateClearChatCommand());
            this.getCommand("chatmute").setExecutor((CommandExecutor)new ChatMuteCommand());
            this.getCommand("day").setExecutor((CommandExecutor)new DayCommand());
            this.getCommand("night").setExecutor((CommandExecutor)new NightCommand());
            this.getCommand("gc").setExecutor((CommandExecutor)new GCCommand(this));
            this.getCommand("invsee").setExecutor((CommandExecutor)invSee);
            this.getCommand("invseeadmin").setExecutor((CommandExecutor)invSee);
            this.getCommand("daylight").setExecutor((CommandExecutor)new DayLightCommand(this.daylightManager));
            this.getCommand("vanish").setExecutor((CommandExecutor)vanishCommand);
            this.getCommand("vanishadmin").setExecutor((CommandExecutor)vanishCommand);
            this.getCommand("commandspy").setExecutor((CommandExecutor)new CommandSpy(this));
            this.getCommand("commandspyadmin").setExecutor((CommandExecutor)new CommandSpy(this));
            this.getCommand("utils").setExecutor((CommandExecutor)new UtilsCommand());
            this.getCommand("near").setExecutor((CommandExecutor)new NearSystem());
            this.getCommand("walkspeed").setExecutor((CommandExecutor)new WalkspeedCommand());
            this.getCommand("perks").setExecutor((CommandExecutor)new PerksCommand(this.economyAPI, this.perksManager));
            this.getCommand("tpa").setExecutor((CommandExecutor)new TPACommand(this.tpaManager));
            this.getCommand("tpahere").setExecutor((CommandExecutor)new TPAHERECommand(this.tpaManager));
            this.getCommand("tpaccept").setExecutor((CommandExecutor)new TPACCEPTCommand(this.tpaManager, tpaListener));
            this.getCommand("tpadeny").setExecutor((CommandExecutor)new TPADENYCommand(this.tpaManager));
            this.getCommand("tpatoggle").setExecutor((CommandExecutor)new TPAToggleCommand(this.tpaManager));
            this.getCommand("tpaadmin").setExecutor((CommandExecutor)new TPAAdminCommand(this.tpaManager));
            this.getLogger().info("SpigotAdminSystem Plugin ON!");
            this.saveDefaultConfig();
        } else {
            this.getLogger().severe("EconomyAPI not found. The plugin will be deactivated.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(EconomyAPI.class);
        if (rsp != null) {
            this.economyAPI = (EconomyAPI)rsp.getProvider();
        }
        return this.economyAPI != null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.lastSeen.put(event.getPlayer().getName(), System.currentTimeMillis());
        UUID playerId = event.getPlayer().getUniqueId();
        this.sessionStartTime.put(playerId, System.currentTimeMillis());
        Player player = event.getPlayer();
        this.scoreboardHandler.createScoreboard(player);
        this.scoreboardHandler.updateScoreboard(player);
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.lastSeen.put(event.getPlayer().getName(), System.currentTimeMillis());
        UUID playerId = event.getPlayer().getUniqueId();
        long joinTime = this.sessionStartTime.getOrDefault(playerId, System.currentTimeMillis());
        long sessionDuration = System.currentTimeMillis() - joinTime;
        this.mysqlManager.updatePlaytime(playerId.toString(), sessionDuration);
        this.sessionStartTime.remove(playerId);
        event.setQuitMessage(null);
    }

    public long getStartTime() {
        return this.startTime;
    }

    public static SpigotAdminSystem getInstance() {
        return instance;
    }

    public static String getPrefix() {
        return prefix;
    }

    public ClearLagManager getClearLagManager() {
        return this.clearLagManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return this.maintenanceManager;
    }

    public PerksManager getPerksManager() {
        return this.perksManager;
    }

    public void onDisable() {
        this.mysqlManager.disconnect();
        this.getLogger().info("SpigotAdminSystem Plugin OFF!");
    }

    static {
        prefix = String.valueOf(ChatColor.DARK_GRAY) + "[" + String.valueOf(ChatColor.GRAY) + "GTM" + String.valueOf(ChatColor.DARK_GRAY) + "]" + String.valueOf(ChatColor.GRAY) + " x ";
    }
}

