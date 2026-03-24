/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package at.Poriax.gTM;

import Armour.ArmourManager;
import Auto.Auto;
import Auto.Autos;
import Command.StaffOnly;
import Command.VoteCommand;
import Dealer.Buyer;
import Dealer.DealerCommand;
import Dealer.DealerPlugin;
import Dealer.WarenManager;
import Food.FoodCommand;
import Food.FoodManager;
import Gang.GangPlugin;
import Guns.AmmoCommand;
import Guns.AmmoGUIListener;
import Guns.GiveAmmoCommand;
import Guns.GunCommand;
import Guns.SneakListener;
import Guns.WeaponListener;
import Guns.WeaponManager;
import Guns.WeaponSwitchListener;
import Level.LevelCommand;
import Level.LevelDatabase;
import Level.LevelJoinListener;
import Level.LevelListener;
import LootCrate.LootCratePlugin;
import Melee.MeleeWeaponManager;
import Regeln.BreakUnbreak;
import Regeln.Vanish;
import Regeln.Welcome;
import Throw.GiveGrenadeCommand;
import Throw.GiveMolotovCommand;
import Throw.GiveSmokeCommand;
import Throw.GiveTearGasCommand;
import Throw.MolotovCocktail;
import Throw.SmokeGrenade;
import Throw.TearGas;
import Throw.ThrowableGrenade;
import TrashC.TrashCanPlugin;
import de.joriax.economy.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class GTM
extends JavaPlugin {
    private EconomyAPI economyAPI;
    private static GTM instance;
    private WarenManager warenManager;
    private DealerPlugin dealerPlugin;
    private Buyer buyer;
    private GangPlugin gangPlugin;
    private LootCratePlugin lootCratePlugin;
    private Vanish vanish;

    public void onEnable() {
        if (this.setupEconomy()) {
            instance = this;
            this.gangPlugin = new GangPlugin(this, this.economyAPI);
            this.gangPlugin.loadGangData();
            this.lootCratePlugin = new LootCratePlugin(this.economyAPI);
            Bukkit.getPluginManager().registerEvents((Listener)this.lootCratePlugin, (Plugin)this);
            WeaponManager.init(this.getDataFolder());
            WeaponManager.registerWeapons();
            FoodManager.registerFood();
            MeleeWeaponManager.registerMeleeWeapons();
            ArmourManager.registerArmour();
            TrashCanPlugin trashCanPlugin = new TrashCanPlugin(this.economyAPI);
            this.buyer = new Buyer(this.economyAPI);
            this.warenManager = new WarenManager();
            this.warenManager.registerWaren();
            this.dealerPlugin = new DealerPlugin(this.warenManager, this.economyAPI);
            if (this.getConfig().contains("dealerPosX") && this.getConfig().contains("dealerPosY") && this.getConfig().contains("dealerPosZ")) {
                this.dealerPlugin.spawnDealer(null);
                this.getLogger().info("Dealer wurde erfolgreich gespawnt!");
            }
            this.getCommand("vote").setExecutor((CommandExecutor)new VoteCommand());
            this.getCommand("staffOnly").setExecutor((CommandExecutor)new StaffOnly());
            this.getCommand("giveweapon").setExecutor((CommandExecutor)new GunCommand());
            this.getCommand("giveammo").setExecutor((CommandExecutor)new GiveAmmoCommand());
            this.getCommand("givefood").setExecutor((CommandExecutor)new FoodCommand());
            this.getCommand("grenade").setExecutor((CommandExecutor)new GiveGrenadeCommand());
            this.getCommand("moli").setExecutor((CommandExecutor)new GiveMolotovCommand());
            this.getCommand("tear").setExecutor((CommandExecutor)new GiveTearGasCommand());
            this.getCommand("smoke").setExecutor((CommandExecutor)new GiveSmokeCommand());
            this.getCommand("dealer").setExecutor((CommandExecutor)new DealerCommand(this.dealerPlugin));
            this.getCommand("buyer").setExecutor((CommandExecutor)new Buyer(this.economyAPI));
            this.getCommand("gang").setExecutor((CommandExecutor)this.gangPlugin);
            this.getCommand("ganginvite").setExecutor((CommandExecutor)this.gangPlugin);
            this.getCommand("gangaccept").setExecutor((CommandExecutor)this.gangPlugin);
            this.getCommand("gangdecline").setExecutor((CommandExecutor)this.gangPlugin);
            this.getCommand("gangleave").setExecutor((CommandExecutor)this.gangPlugin);
            this.getCommand("ammo").setExecutor((CommandExecutor)new AmmoCommand());
            this.getCommand("car").setExecutor((CommandExecutor)new Auto.CarCommandExecutor());
            this.getCommand("level").setExecutor((CommandExecutor)new LevelCommand());
            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents((Listener)new BreakUnbreak(), (Plugin)this);
            pluginManager.registerEvents((Listener)new Welcome(), (Plugin)this);
            pluginManager.registerEvents((Listener)new WeaponListener(), (Plugin)this);
            pluginManager.registerEvents((Listener)new SneakListener(), (Plugin)this);
            pluginManager.registerEvents((Listener)new WeaponSwitchListener(), (Plugin)this);
            pluginManager.registerEvents((Listener)new ThrowableGrenade(), (Plugin)this);
            pluginManager.registerEvents((Listener)new MolotovCocktail(this.gangPlugin), (Plugin)this);
            pluginManager.registerEvents((Listener)new TearGas(), (Plugin)this);
            pluginManager.registerEvents((Listener)new SmokeGrenade(), (Plugin)this);
            pluginManager.registerEvents((Listener)trashCanPlugin, (Plugin)this);
            pluginManager.registerEvents((Listener)this.dealerPlugin, (Plugin)this);
            pluginManager.registerEvents((Listener)this.buyer, (Plugin)this);
            pluginManager.registerEvents((Listener)this.gangPlugin, (Plugin)this);
            pluginManager.registerEvents((Listener)new AmmoGUIListener(), (Plugin)this);
            pluginManager.registerEvents((Listener)new Autos(), (Plugin)this);
            pluginManager.registerEvents((Listener)new LevelListener(), (Plugin)this);
            pluginManager.registerEvents((Listener)new LevelJoinListener(), (Plugin)this);
            this.getLogger().info("GTM wurde erfolgreich aktiviert!");
        } else {
            this.getLogger().severe("EconomyAPI nicht gefunden. Das Plugin wird deaktiviert.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
        try {
            LevelDatabase.connect("localhost", "gtm", "gtm", "gtm");
            LevelDatabase.setupTable();
            this.getLogger().info("Levels-Datenbank erfolgreich verbunden!");
        }
        catch (Exception e) {
            this.getLogger().severe("Konnte nicht mit der Levels-Datenbank verbinden.");
            e.printStackTrace();
        }
        this.saveDefaultConfig();
    }

    public static void giveMenuClock(Player player) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        meta.setDisplayName(String.valueOf(ChatColor.BOLD) + " " + String.valueOf(ChatColor.GOLD) + "Phone");
        clock.setItemMeta(meta);
        player.getInventory().setItem(8, clock);
    }

    public void onDisable() {
        if (this.gangPlugin != null) {
            this.gangPlugin.saveGangData();
        }
        if (this.lootCratePlugin != null) {
            this.lootCratePlugin.onDisable();
        }
        WeaponManager.saveAmmoData();
        this.saveConfig();
        this.getLogger().info("GTM wurde deaktiviert!");
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(EconomyAPI.class);
        if (rsp != null) {
            this.economyAPI = (EconomyAPI)rsp.getProvider();
        }
        return this.economyAPI != null;
    }

    public static GTM getInstance() {
        return instance;
    }

    public EconomyAPI getEconomyAPI() {
        return this.economyAPI;
    }

    public GangPlugin getGangPlugin() {
        return this.gangPlugin;
    }
}

