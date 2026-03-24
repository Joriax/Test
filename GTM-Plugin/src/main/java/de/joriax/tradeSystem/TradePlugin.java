/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.tradeSystem;

import de.joriax.economy.EconomyAPI;
import de.joriax.tradeSystem.TradeCommand;
import de.joriax.tradeSystem.TradeListener;
import de.joriax.tradeSystem.TradeManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TradePlugin
extends JavaPlugin {
    private TradeManager tradeManager;
    private EconomyAPI economyAPI;

    public void onEnable() {
        if (this.setupEconomy()) {
            this.getLogger().info("TradePlugin aktiviert!");
            this.tradeManager = new TradeManager();
            this.getCommand("trade").setExecutor((CommandExecutor)new TradeCommand(this.tradeManager, this.economyAPI));
            this.getServer().getPluginManager().registerEvents((Listener)new TradeListener(this.tradeManager), (Plugin)this);
        } else {
            this.getLogger().severe("EconomyAPI not found. The plugin will be deactivated.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }

    public void onDisable() {
        this.getLogger().info("TradePlugin deaktiviert!");
    }

    public TradeManager getTradeManager() {
        return this.tradeManager;
    }

    public EconomyAPI getEconomyAPI() {
        return this.economyAPI;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(EconomyAPI.class);
        if (rsp != null) {
            this.economyAPI = (EconomyAPI)rsp.getProvider();
        }
        return this.economyAPI != null;
    }
}

