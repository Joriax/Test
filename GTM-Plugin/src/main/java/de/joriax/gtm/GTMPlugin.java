package de.joriax.gtm;

import de.joriax.gtm.backpack.BackpackManager;
import de.joriax.gtm.economy.DatabaseManager;
import de.joriax.gtm.economy.EconomyAPI;
import de.joriax.gtm.economy.EconomyManager;
import de.joriax.gtm.economy.commands.*;
import de.joriax.gtm.level.LevelDatabase;
import de.joriax.gtm.level.LevelJoinListener;
import de.joriax.gtm.level.LevelListener;
import de.joriax.gtm.level.LevelManager;
import de.joriax.gtm.level.commands.GiveXPCommand;
import de.joriax.gtm.level.commands.LevelCommand;
import de.joriax.gtm.level.commands.SetLevelCommand;
import de.joriax.gtm.weapons.guns.*;
import de.joriax.gtm.weapons.guns.commands.*;
import de.joriax.gtm.weapons.melee.MeleeWeaponManager;
import de.joriax.gtm.weapons.throwables.*;

import de.joriax.spigotAdminSystem.CommandSpy.CommandListener;
import de.joriax.spigotAdminSystem.Commands.*;
import de.joriax.spigotAdminSystem.Listener.*;
import de.joriax.spigotAdminSystem.Manager.CPSManager;
import de.joriax.spigotAdminSystem.Manager.DayLightManager;
import de.joriax.spigotAdminSystem.Manager.MySQLManager;
import de.joriax.spigotAdminSystem.Utils.BlockedCommands.BlockCommandListener;
import de.joriax.spigotAdminSystem.Utils.ClearLag.ClearLagManager;
import de.joriax.spigotAdminSystem.Utils.Maintenance.MaintenanceManager;
import de.joriax.spigotAdminSystem.Utils.Near.NearSystem;
import de.joriax.spigotAdminSystem.Utils.Perks.PerksManager;
import de.joriax.spigotAdminSystem.Utils.TPA.*;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsCommand;
import de.joriax.spigotAdminSystem.Utils.UtilsMain.UtilsConfig;
import de.joriax.spigotAdminSystem.Utils.Listeners.PlayerDeathListener;
import de.joriax.spigotAdminSystem.Vanish.Command.VanishCommand;
import de.joriax.spigotAdminSystem.Vanish.Manager.VanishManager;

import de.joriax.spigotWatchlist.WatchlistCommand;
import de.joriax.spigotWatchlist.WatchlistManager;
import de.joriax.spigotWatchlist.WatchlistMessageListener;

import de.joriax.tradeSystem.TradeCommand;
import de.joriax.tradeSystem.TradeListener;
import de.joriax.tradeSystem.TradeManager;

import de.joriax.jetPackSystem.JetPackSystem;
import de.joriax.wingSystem.WingsuitPlugin;

import Regeln.BreakUnbreak;
import Regeln.Welcome;
import Command.StaffOnly;
import Command.VoteCommand;
import Food.FoodCommand;
import Food.FoodManager;
import Armour.ArmourManager;
import Throw.GiveGrenadeCommand;
import Throw.GiveMolotovCommand;
import Throw.GiveTearGasCommand;
import Throw.GiveSmokeCommand;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GTMPlugin extends JavaPlugin {

    private static GTMPlugin instance;
    private final long startTime = System.currentTimeMillis();

    // Core economy
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private EconomyAPI economyAPI;

    // Level system
    private LevelDatabase levelDatabase;
    private LevelManager levelManager;

    // Admin MySQL + tracking maps
    private MySQLManager mySQLManager;
    private HashMap<UUID, Long> sessionStartTime;
    private Map<String, Long> lastSeen;

    // Admin managers
    private CPSManager cpsManager;
    private DayLightManager dayLightManager;
    private MaintenanceManager maintenanceManager;
    private PerksManager perksManager;
    private ClearLagManager clearLagManager;
    private VanishCommand vanishCommand;
    private VanishManager vanishManager;
    private TPAManager tpaManager;
    private TPAListener tpaListener;

    // Gameplay
    private BackpackManager backpackManager;
    private WeaponManager weaponManager;
    private MeleeWeaponManager meleeWeaponManager;
    private JetPackSystem jetPackSystem;
    private WingsuitPlugin wingsuitPlugin;
    private TradeManager tradeManager;
    private WatchlistManager watchlistManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Economy
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        economyManager = new EconomyManager(databaseManager);
        economyAPI = new EconomyAPI(economyManager);
        getServer().getServicesManager().register(EconomyAPI.class, economyAPI, this, ServicePriority.Normal);

        // Level
        levelDatabase = new LevelDatabase(this);
        levelDatabase.connect();
        levelDatabase.createTables();
        levelManager = new LevelManager(levelDatabase);

        // UtilsConfig (static setup)
        UtilsConfig.setupConfig(this);

        // Admin MySQL
        mySQLManager = new MySQLManager("localhost:3306", "adminspigot", "adminspigot", "adminspigot");
        mySQLManager.connect();
        sessionStartTime = new HashMap<>();
        lastSeen = new HashMap<>();

        // Admin managers
        cpsManager = new CPSManager();
        dayLightManager = new DayLightManager(this);
        maintenanceManager = new MaintenanceManager(this);
        perksManager = new PerksManager(this);
        clearLagManager = new ClearLagManager(this, "[GTM]");
        vanishCommand = new VanishCommand(this);
        vanishManager = new VanishManager(this, vanishCommand);
        tpaManager = new TPAManager();
        tpaListener = new TPAListener(tpaManager, this);

        // Gameplay
        backpackManager = new BackpackManager(this);
        weaponManager = new WeaponManager(this);
        meleeWeaponManager = new MeleeWeaponManager(this);
        jetPackSystem = new JetPackSystem(this);
        wingsuitPlugin = new WingsuitPlugin(this);

        // Trade
        tradeManager = new TradeManager();

        // Watchlist
        watchlistManager = new WatchlistManager();

        // Static inits
        ArmourManager.registerArmour();
        FoodManager.registerFood();

        registerCommands();
        registerListeners();

        getLogger().info("GTMPlugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
        if (levelDatabase != null) levelDatabase.disconnect();
        if (mySQLManager != null) mySQLManager.disconnect();
        if (backpackManager != null) backpackManager.saveAll();
        getLogger().info("GTMPlugin disabled.");
    }

    private void registerCommands() {
        // Economy commands
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(this));
        getCommand("addbalance").setExecutor(new AddBalanceCommand(this));
        getCommand("seebalance").setExecutor(new SeeBalanceCommand(this));
        getCommand("removebalance").setExecutor(new RemoveBalanceCommand(this));
        getCommand("crowbars").setExecutor(new CrowbarsCommand(economyAPI));

        // Level commands
        getCommand("level").setExecutor(new LevelCommand(levelManager));
        getCommand("setlevel").setExecutor(new SetLevelCommand(levelManager));
        getCommand("givexp").setExecutor(new GiveXPCommand(levelManager));

        // Backpack commands
        getCommand("backpack").setExecutor(backpackManager);
        getCommand("viewbackpack").setExecutor(backpackManager);
        getCommand("backpackdelete").setExecutor(backpackManager);

        // Weapon commands
        getCommand("giveweapon").setExecutor(new GunCommand(weaponManager));
        getCommand("giveammo").setExecutor(new GiveAmmoCommand(weaponManager));
        getCommand("ammo").setExecutor(new AmmoCommand(weaponManager));

        // Throwable commands
        getCommand("grenade").setExecutor(new GiveGrenadeCommand());
        getCommand("moli").setExecutor(new GiveMolotovCommand());
        getCommand("tear").setExecutor(new GiveTearGasCommand());
        getCommand("smoke").setExecutor(new GiveSmokeCommand());

        // JetPack / Wingsuit
        getCommand("jetpack").setExecutor(jetPackSystem);
        getCommand("wing").setExecutor(wingsuitPlugin);

        // Trade
        getCommand("trade").setExecutor(new TradeCommand(tradeManager, economyAPI));

        // Watchlist
        WatchlistCommand watchlistCmd = new WatchlistCommand(watchlistManager);
        getCommand("watchlist").setExecutor(watchlistCmd);
        getServer().getPluginManager().registerEvents(watchlistCmd, this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new WatchlistMessageListener(watchlistCmd));

        // Food
        getCommand("givefood").setExecutor(new FoodCommand());

        // Admin commands
        getCommand("vanish").setExecutor(vanishCommand);
        getCommand("unvanish").setExecutor(vanishCommand);
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new FeedCommand(this));
        getCommand("tp").setExecutor(new TPCommand());
        getCommand("tphere").setExecutor(new TPHereCommand());
        getCommand("gm").setExecutor(new GameModeCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("flyspeed").setExecutor(new FlySpeedCommand());
        getCommand("walkspeed").setExecutor(new WalkspeedCommand());
        getCommand("checkcps").setExecutor(new CheckCPSCommand(cpsManager));
        getCommand("invsee").setExecutor(new InvSee(this));
        getCommand("seen").setExecutor(new SeenCommand(lastSeen));
        getCommand("getpos").setExecutor(new GetPosCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("seeping").setExecutor(new SeePingCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand(mySQLManager, sessionStartTime));
        getCommand("cc").setExecutor(new ClearChatCommand());
        getCommand("pcc").setExecutor(new PrivateClearChatCommand());
        getCommand("chatmute").setExecutor(new ChatMuteCommand());
        getCommand("day").setExecutor(new DayCommand());
        getCommand("night").setExecutor(new NightCommand());
        getCommand("daylight").setExecutor(new DayLightCommand(dayLightManager));
        getCommand("gc").setExecutor(new GCCommand(startTime));
        getCommand("lbc").setExecutor(new LocalBroadcastCommand());
        getCommand("near").setExecutor(new NearSystem());
        getCommand("fix").setExecutor(new FixCommand(this));
        getCommand("fixall").setExecutor(new FixAllCommand(this));
        getCommand("staffonly").setExecutor(new StaffOnly());
        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("utils").setExecutor(new UtilsCommand());

        // TPA commands
        TPACCEPTCommand tpaAcceptCmd = new TPACCEPTCommand(tpaManager, tpaListener);
        getCommand("tpa").setExecutor(new TPACommand(tpaManager));
        getCommand("tpahere").setExecutor(new TPAHERECommand(tpaManager));
        getCommand("tpaccept").setExecutor(tpaAcceptCmd);
        getCommand("tpadeny").setExecutor(new TPADENYCommand(tpaManager));
        getCommand("tpatoggle").setExecutor(new TPAToggleCommand(tpaManager));
        getCommand("tpaadmin").setExecutor(new TPAAdminCommand(tpaManager));
    }

    private void registerListeners() {
        // Level
        getServer().getPluginManager().registerEvents(new LevelJoinListener(levelManager), this);
        getServer().getPluginManager().registerEvents(new LevelListener(levelManager, economyAPI), this);

        // Backpack
        getServer().getPluginManager().registerEvents(backpackManager, this);

        // Weapons
        getServer().getPluginManager().registerEvents(new WeaponListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new WeaponSwitchListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new SneakListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new AmmoGUIListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new ThrowableGrenade(), this);
        getServer().getPluginManager().registerEvents(new MolotovCocktail(), this);
        getServer().getPluginManager().registerEvents(new TearGas(), this);
        getServer().getPluginManager().registerEvents(new SmokeGrenade(), this);

        // Melee
        getServer().getPluginManager().registerEvents(meleeWeaponManager, this);

        // JetPack / Wingsuit
        getServer().getPluginManager().registerEvents(jetPackSystem, this);
        getServer().getPluginManager().registerEvents(wingsuitPlugin, this);

        // Trade
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);

        // Regeln
        getServer().getPluginManager().registerEvents(new BreakUnbreak(), this);
        getServer().getPluginManager().registerEvents(new Welcome(), this);

        // Admin
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InvSeeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(mySQLManager, sessionStartTime, lastSeen), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new CPSListener(cpsManager), this);
        getServer().getPluginManager().registerEvents(tpaListener, this);
        getServer().getPluginManager().registerEvents(new MovementListener(tpaManager), this);
        getServer().getPluginManager().registerEvents(new BlockCommandListener(), this);
        getServer().getPluginManager().registerEvents(vanishCommand, this);
        getServer().getPluginManager().registerEvents(perksManager, this);
    }

    public static GTMPlugin getInstance() {
        return instance;
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }
}
