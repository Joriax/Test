package de.joriax.gtm;

import de.joriax.gtm.admin.clearlag.ClearLagManager;
import de.joriax.gtm.admin.commandspy.CommandSpyListener;
import de.joriax.gtm.admin.commandspy.CommandSpyManager;
import de.joriax.gtm.admin.commands.*;
import de.joriax.gtm.admin.database.MySQLManager;
import de.joriax.gtm.admin.listeners.*;
import de.joriax.gtm.admin.maintenance.MaintenanceManager;
import de.joriax.gtm.admin.perks.PerksManager;
import de.joriax.gtm.admin.scoreboard.ScoreboardHandler;
import de.joriax.gtm.admin.tpa.*;
import de.joriax.gtm.admin.utils.*;
import de.joriax.gtm.admin.vanish.VanishCommand;
import de.joriax.gtm.admin.vanish.VanishManager;
import de.joriax.gtm.armour.ArmourManager;
import de.joriax.gtm.backpack.BackpackManager;
import de.joriax.gtm.dealer.DealerManager;
import de.joriax.gtm.dealer.WareManager;
import de.joriax.gtm.dealer.commands.BuyerCommand;
import de.joriax.gtm.dealer.commands.DealerCommand;
import de.joriax.gtm.economy.DatabaseManager;
import de.joriax.gtm.economy.EconomyAPI;
import de.joriax.gtm.economy.EconomyManager;
import de.joriax.gtm.economy.commands.*;
import de.joriax.gtm.food.FoodManager;
import de.joriax.gtm.food.commands.FoodCommand;
import de.joriax.gtm.gang.GangManager;
import de.joriax.gtm.jetpack.JetpackManager;
import de.joriax.gtm.level.LevelDatabase;
import de.joriax.gtm.level.LevelJoinListener;
import de.joriax.gtm.level.LevelListener;
import de.joriax.gtm.level.LevelManager;
import de.joriax.gtm.level.commands.GiveXPCommand;
import de.joriax.gtm.level.commands.LevelCommand;
import de.joriax.gtm.level.commands.SetLevelCommand;
import de.joriax.gtm.lootcrate.LootCrateManager;
import de.joriax.gtm.rules.BreakUnbreakListener;
import de.joriax.gtm.rules.WelcomeListener;
import de.joriax.gtm.trade.TradeListener;
import de.joriax.gtm.trade.TradeManager;
import de.joriax.gtm.trade.commands.TradeCommand;
import de.joriax.gtm.trash.TrashCanManager;
import de.joriax.gtm.vehicle.AutoProtectionListener;
import de.joriax.gtm.vehicle.Autos;
import de.joriax.gtm.vehicle.VehicleManager;
import de.joriax.gtm.vehicle.commands.CarCommandExecutor;
import de.joriax.gtm.watchlist.WatchlistCommand;
import de.joriax.gtm.watchlist.WatchlistManager;
import de.joriax.gtm.watchlist.WatchlistMessageListener;
import de.joriax.gtm.weapons.guns.*;
import de.joriax.gtm.weapons.guns.commands.AmmoCommand;
import de.joriax.gtm.weapons.guns.commands.GiveAmmoCommand;
import de.joriax.gtm.weapons.guns.commands.GunCommand;
import de.joriax.gtm.weapons.melee.MeleeWeaponManager;
import de.joriax.gtm.weapons.throwables.*;
import de.joriax.gtm.weapons.throwables.commands.*;
import de.joriax.gtm.wingsuit.WingsuitManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class GTMPlugin extends JavaPlugin {

    private static GTMPlugin instance;

    // Core systems
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private EconomyAPI economyAPI;

    // Level system
    private LevelDatabase levelDatabase;
    private LevelManager levelManager;

    // Admin systems
    private MySQLManager mySQLManager;
    private VanishManager vanishManager;
    private CommandSpyManager commandSpyManager;
    private PerksManager perksManager;
    private ScoreboardHandler scoreboardHandler;
    private ClearLagManager clearLagManager;
    private MaintenanceManager maintenanceManager;
    private CPSManager cpsManager;
    private DayLightManager dayLightManager;
    private UtilsConfig utilsConfig;

    // TPA system
    private TPAManager tpaManager;

    // Gameplay systems
    private BackpackManager backpackManager;
    private WeaponManager weaponManager;
    private MeleeWeaponManager meleeWeaponManager;
    private VehicleManager vehicleManager;
    private GangManager gangManager;
    private WareManager wareManager;
    private DealerManager dealerManager;
    private FoodManager foodManager;
    private ArmourManager armourManager;
    private TrashCanManager trashCanManager;
    private LootCrateManager lootCrateManager;
    private JetpackManager jetpackManager;
    private WingsuitManager wingsuitManager;
    private TradeManager tradeManager;
    private WatchlistManager watchlistManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Initialize economy database
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();
        databaseManager.createTables();

        economyManager = new EconomyManager(databaseManager);
        economyAPI = new EconomyAPI(economyManager);

        // Register EconomyAPI as Bukkit service for compatibility
        getServer().getServicesManager().register(EconomyAPI.class, economyAPI, this, ServicePriority.Normal);

        // Initialize level system
        levelDatabase = new LevelDatabase(this);
        levelDatabase.connect();
        levelDatabase.createTables();
        levelManager = new LevelManager(levelDatabase);

        // Initialize admin MySQL
        mySQLManager = new MySQLManager(this);
        mySQLManager.connect();
        mySQLManager.createTables();

        // Initialize admin systems
        vanishManager = new VanishManager();
        commandSpyManager = new CommandSpyManager();
        perksManager = new PerksManager(this);
        cpsManager = new CPSManager();
        clearLagManager = new ClearLagManager(this);
        maintenanceManager = new MaintenanceManager(this);
        utilsConfig = new UtilsConfig(this);
        dayLightManager = new DayLightManager(this);

        // TPA
        tpaManager = new TPAManager();

        // Backpack
        backpackManager = new BackpackManager(this);

        // Weapons
        weaponManager = new WeaponManager(this);
        meleeWeaponManager = new MeleeWeaponManager(this);

        // Vehicle
        vehicleManager = new VehicleManager(this);

        // Gang
        gangManager = new GangManager(this);

        // Dealer
        wareManager = new WareManager(this);
        dealerManager = new DealerManager(this, wareManager);

        // Food
        foodManager = new FoodManager(this);

        // Armour
        armourManager = new ArmourManager(this);

        // Trash
        trashCanManager = new TrashCanManager(this);

        // LootCrate
        lootCrateManager = new LootCrateManager(this);

        // Jetpack
        jetpackManager = new JetpackManager(this);

        // Wingsuit
        wingsuitManager = new WingsuitManager(this);

        // Trade
        tradeManager = new TradeManager(this);

        // Watchlist
        watchlistManager = new WatchlistManager(this);

        // Scoreboard (initialized after economy and level)
        scoreboardHandler = new ScoreboardHandler(this);

        // Register commands and listeners
        registerEconomyCommands();
        registerLevelCommands();
        registerLevelListeners();
        registerBackpackCommands();
        registerWeaponCommands();
        registerVehicleCommands();
        registerDealerCommands();
        registerFoodCommands();
        registerTradeCommands();
        registerWatchlistCommands();
        registerAdminCommands();
        registerAdminListeners();
        registerGameplayListeners();
        registerRulesListeners();

        getLogger().info("GTMPlugin has been enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        if (backpackManager != null) {
            backpackManager.saveAll();
        }
        if (gangManager != null) {
            gangManager.saveAll();
        }
        if (watchlistManager != null) {
            watchlistManager.saveAll();
        }
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        if (levelDatabase != null) {
            levelDatabase.disconnect();
        }
        if (mySQLManager != null) {
            mySQLManager.disconnect();
        }
        if (clearLagManager != null) {
            clearLagManager.stopTask();
        }
        if (scoreboardHandler != null) {
            scoreboardHandler.stopTask();
        }
        if (dayLightManager != null) {
            dayLightManager.stopTask();
        }
        getLogger().info("GTMPlugin has been disabled.");
    }

    private void registerEconomyCommands() {
        getCommand("balance").setExecutor(new BalanceCommand(economyAPI));
        getCommand("pay").setExecutor(new PayCommand(economyAPI));
        getCommand("setbalance").setExecutor(new SetBalanceCommand(economyAPI));
        getCommand("addbalance").setExecutor(new AddBalanceCommand(economyAPI));
        getCommand("seebalance").setExecutor(new SeeBalanceCommand(economyAPI));
        getCommand("removebalance").setExecutor(new RemoveBalanceCommand(economyAPI));
        getCommand("crowbars").setExecutor(new CrowbarsCommand(economyAPI));
    }

    private void registerLevelCommands() {
        LevelCommand levelCmd = new LevelCommand(levelManager);
        getCommand("level").setExecutor(levelCmd);
        getCommand("setlevel").setExecutor(new SetLevelCommand(levelManager));
        getCommand("givexp").setExecutor(new GiveXPCommand(levelManager));
    }

    private void registerLevelListeners() {
        getServer().getPluginManager().registerEvents(new LevelJoinListener(levelManager), this);
        getServer().getPluginManager().registerEvents(new LevelListener(levelManager, economyAPI), this);
    }

    private void registerBackpackCommands() {
        getCommand("backpack").setExecutor(backpackManager);
        getCommand("viewbackpack").setExecutor(backpackManager);
        getCommand("backpackdelete").setExecutor(backpackManager);
        getServer().getPluginManager().registerEvents(backpackManager, this);
    }

    private void registerWeaponCommands() {
        getCommand("giveweapon").setExecutor(new GunCommand(weaponManager));
        getCommand("giveammo").setExecutor(new GiveAmmoCommand(weaponManager));
        getCommand("ammo").setExecutor(new AmmoCommand(weaponManager));

        getServer().getPluginManager().registerEvents(new WeaponListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new WeaponSwitchListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new SneakListener(weaponManager), this);
        getServer().getPluginManager().registerEvents(new AmmoGUIListener(weaponManager), this);

        // Throwables
        getServer().getPluginManager().registerEvents(new ThrowableGrenade(), this);
        getServer().getPluginManager().registerEvents(new MolotovCocktail(), this);
        getServer().getPluginManager().registerEvents(new TearGas(), this);
        getServer().getPluginManager().registerEvents(new SmokeGrenade(), this);

        getCommand("givegrenade").setExecutor(new GiveGrenadeCommand());
        getCommand("givemolotov").setExecutor(new GiveMolotovCommand());
        getCommand("giveteargas").setExecutor(new GiveTearGasCommand());
        getCommand("givesmoke").setExecutor(new GiveSmokeCommand());
    }

    private void registerVehicleCommands() {
        CarCommandExecutor carCmd = new CarCommandExecutor(vehicleManager);
        getCommand("createcar").setExecutor(carCmd);
        getCommand("car").setExecutor(carCmd);
        getServer().getPluginManager().registerEvents(new Autos(vehicleManager), this);
        getServer().getPluginManager().registerEvents(new AutoProtectionListener(vehicleManager), this);
    }

    private void registerDealerCommands() {
        getCommand("dealer").setExecutor(new DealerCommand(dealerManager));
        getCommand("buyer").setExecutor(new BuyerCommand(dealerManager));
        getServer().getPluginManager().registerEvents(dealerManager, this);
    }

    private void registerFoodCommands() {
        getCommand("givefood").setExecutor(new FoodCommand(foodManager));
    }

    private void registerTradeCommands() {
        TradeCommand tradeCmd = new TradeCommand(tradeManager);
        getCommand("trade").setExecutor(tradeCmd);
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager, economyAPI), this);
    }

    private void registerWatchlistCommands() {
        WatchlistCommand watchlistCmd = new WatchlistCommand(watchlistManager);
        getCommand("watchlist").setExecutor(watchlistCmd);
        getServer().getPluginManager().registerEvents(watchlistCmd, this);
        getServer().getPluginManager().registerEvents(new WatchlistMessageListener(watchlistManager), this);
    }

    private void registerAdminCommands() {
        // Vanish
        VanishCommand vanishCmd = new VanishCommand(vanishManager);
        getCommand("vanish").setExecutor(vanishCmd);
        getCommand("unvanish").setExecutor(vanishCmd);

        // Basic admin commands
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("tp").setExecutor(new TeleportCommand());
        getCommand("tphere").setExecutor(new TeleportHereCommand());
        getCommand("gm").setExecutor(new GameModeCommand());
        getCommand("god").setExecutor(new GodCommand());
        getCommand("flyspeed").setExecutor(new FlySpeedCommand());
        getCommand("walkspeed").setExecutor(new WalkSpeedCommand());
        getCommand("checkcps").setExecutor(new CheckCPSCommand(cpsManager));
        getCommand("invsee").setExecutor(new InvSeeCommand());
        getCommand("seen").setExecutor(new SeenCommand(mySQLManager));
        getCommand("getpos").setExecutor(new GetPosCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("seeping").setExecutor(new SeePingCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand(mySQLManager));
        getCommand("cc").setExecutor(new ClearChatCommand());
        getCommand("pcc").setExecutor(new PrivateClearChatCommand());
        getCommand("chatmute").setExecutor(new ChatMuteCommand());
        getCommand("day").setExecutor(new DayCommand());
        getCommand("night").setExecutor(new NightCommand());
        getCommand("daylight").setExecutor(new DayLightCommand(dayLightManager));
        getCommand("gc").setExecutor(new GCCommand());
        getCommand("lbc").setExecutor(new LocalBroadcastCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("fix").setExecutor(new FixCommand());
        getCommand("fixall").setExecutor(new FixAllCommand());
        getCommand("perks").setExecutor(new PerksCommand(perksManager));
        getCommand("staffonly").setExecutor(new StaffOnlyCommand(maintenanceManager));
        getCommand("vote").setExecutor(new VoteCommand());
        getCommand("utils").setExecutor(new UtilsCommand(this));

        // TPA
        TPACommand tpaCmd = new TPACommand(tpaManager);
        TPAHereCommand tpaHereCmd = new TPAHereCommand(tpaManager);
        TPAAcceptCommand tpaAcceptCmd = new TPAAcceptCommand(tpaManager);
        TPADenyCommand tpaDenyCmd = new TPADenyCommand(tpaManager);
        TPAToggleCommand tpaToggleCmd = new TPAToggleCommand(tpaManager);
        TPAAdminCommand tpaAdminCmd = new TPAAdminCommand(tpaManager);

        getCommand("tpa").setExecutor(tpaCmd);
        getCommand("tpahere").setExecutor(tpaHereCmd);
        getCommand("tpaccept").setExecutor(tpaAcceptCmd);
        getCommand("tpadeny").setExecutor(tpaDenyCmd);
        getCommand("tpatoggle").setExecutor(tpaToggleCmd);
        getCommand("tpaadmin").setExecutor(tpaAdminCmd);

        // Gang
        getCommand("gang").setExecutor(gangManager);
        getServer().getPluginManager().registerEvents(gangManager, this);

        // Jetpack
        getCommand("jetpack").setExecutor(jetpackManager);

        // Wingsuit
        getCommand("wing").setExecutor(wingsuitManager);

        // LootCrate
        getCommand("lootcrate").setExecutor(lootCrateManager);
        getServer().getPluginManager().registerEvents(lootCrateManager, this);

        // Trash
        getCommand("trash").setExecutor(trashCanManager);
        getServer().getPluginManager().registerEvents(trashCanManager, this);
    }

    private void registerAdminListeners() {
        getServer().getPluginManager().registerEvents(new CommandSpyListener(commandSpyManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(maintenanceManager), this);
        getServer().getPluginManager().registerEvents(new InvSeeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(mySQLManager, vanishManager, scoreboardHandler), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(economyAPI, levelManager), this);
        getServer().getPluginManager().registerEvents(new CPSListener(cpsManager), this);
        getServer().getPluginManager().registerEvents(new TPAListener(tpaManager), this);
        getServer().getPluginManager().registerEvents(new MovementListener(tpaManager), this);
        getServer().getPluginManager().registerEvents(new GUIManager(this), this);
        getServer().getPluginManager().registerEvents(new BlockCommandListener(maintenanceManager), this);
        getServer().getPluginManager().registerEvents(vanishManager, this);
        getServer().getPluginManager().registerEvents(perksManager, this);
        getServer().getPluginManager().registerEvents(jetpackManager, this);
        getServer().getPluginManager().registerEvents(wingsuitManager, this);
    }

    private void registerGameplayListeners() {
        // Melee
        getServer().getPluginManager().registerEvents(meleeWeaponManager, this);
    }

    private void registerRulesListeners() {
        getServer().getPluginManager().registerEvents(new BreakUnbreakListener(), this);
        getServer().getPluginManager().registerEvents(new WelcomeListener(levelManager), this);
    }

    // Getters
    public static GTMPlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public EconomyAPI getEconomyAPI() {
        return economyAPI;
    }

    public LevelDatabase getLevelDatabase() {
        return levelDatabase;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public CommandSpyManager getCommandSpyManager() {
        return commandSpyManager;
    }

    public PerksManager getPerksManager() {
        return perksManager;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public ClearLagManager getClearLagManager() {
        return clearLagManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }

    public CPSManager getCpsManager() {
        return cpsManager;
    }

    public TPAManager getTpaManager() {
        return tpaManager;
    }

    public BackpackManager getBackpackManager() {
        return backpackManager;
    }

    public WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public MeleeWeaponManager getMeleeWeaponManager() {
        return meleeWeaponManager;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public GangManager getGangManager() {
        return gangManager;
    }

    public WareManager getWareManager() {
        return wareManager;
    }

    public DealerManager getDealerManager() {
        return dealerManager;
    }

    public FoodManager getFoodManager() {
        return foodManager;
    }

    public ArmourManager getArmourManager() {
        return armourManager;
    }

    public TrashCanManager getTrashCanManager() {
        return trashCanManager;
    }

    public LootCrateManager getLootCrateManager() {
        return lootCrateManager;
    }

    public JetpackManager getJetpackManager() {
        return jetpackManager;
    }

    public WingsuitManager getWingsuitManager() {
        return wingsuitManager;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public WatchlistManager getWatchlistManager() {
        return watchlistManager;
    }

    public DayLightManager getDayLightManager() {
        return dayLightManager;
    }

    public UtilsConfig getUtilsConfig() {
        return utilsConfig;
    }
}
