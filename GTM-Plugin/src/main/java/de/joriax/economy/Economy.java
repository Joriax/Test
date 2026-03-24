/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.ServicePriority
 *  org.bukkit.plugin.java.JavaPlugin
 */
package de.joriax.economy;

import de.joriax.economy.AddBalanceCommand;
import de.joriax.economy.BalanceCommand;
import de.joriax.economy.CrowbarsCommand;
import de.joriax.economy.DatabaseManager;
import de.joriax.economy.EconomyAPI;
import de.joriax.economy.EconomyManager;
import de.joriax.economy.PayCommand;
import de.joriax.economy.RemoveBalanceCommand;
import de.joriax.economy.SeeBalanceCommand;
import de.joriax.economy.SetBalanceCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class Economy
extends JavaPlugin {
    private EconomyManager economyManager;
    private DatabaseManager databaseManager;
    private EconomyAPI economyAPI;

    public void onEnable() {
        this.getLogger().info("Economy Plugin ON!");
        String dbUrl = "jdbc:mysql://localhost:3306/gtm";
        String dbUser = "gtm";
        String dbPassword = "gtm";
        try {
            this.databaseManager = new DatabaseManager();
            this.databaseManager.connect(dbUrl, dbUser, dbPassword);
            this.economyManager = new EconomyManager(this.databaseManager);
            this.economyManager.addCrobwarsColumn();
            this.economyAPI = new EconomyAPI(this.economyManager);
            this.getServer().getServicesManager().register(EconomyAPI.class, this.economyAPI, (Plugin)this, ServicePriority.Normal);
            this.getCommand("balance").setExecutor((CommandExecutor)new BalanceCommand(this.economyManager));
            this.getCommand("pay").setExecutor((CommandExecutor)new PayCommand(this.economyManager));
            this.getCommand("setbalance").setExecutor((CommandExecutor)new SetBalanceCommand(this.economyManager));
            this.getCommand("addbalance").setExecutor((CommandExecutor)new AddBalanceCommand(this.economyManager));
            this.getCommand("seebalance").setExecutor((CommandExecutor)new SeeBalanceCommand(this.economyManager));
            this.getCommand("removebalance").setExecutor((CommandExecutor)new RemoveBalanceCommand(this.economyAPI));
            this.getCommand("crowbars").setExecutor((CommandExecutor)new CrowbarsCommand(this.economyAPI));
            this.getCommand("seecrowbars").setExecutor((CommandExecutor)new CrowbarsCommand(this.economyAPI));
            this.getCommand("addcrowbars").setExecutor((CommandExecutor)new CrowbarsCommand(this.economyAPI));
            this.getCommand("removecrowbars").setExecutor((CommandExecutor)new CrowbarsCommand(this.economyAPI));
        }
        catch (Exception e) {
            e.printStackTrace();
            this.getLogger().severe("Error connecting to the database!");
        }
    }

    public void onDisable() {
        this.getLogger().info("Economy Plugin OFF!");
        try {
            if (this.databaseManager != null) {
                this.databaseManager.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EconomyAPI getEconomyAPI() {
        return this.economyAPI;
    }
}

