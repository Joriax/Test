/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Villager
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package Dealer;

import Dealer.WarenManager;
import at.Poriax.gTM.GTM;
import de.joriax.economy.EconomyAPI;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DealerPlugin
implements Listener {
    private Villager dealer;
    private WarenManager warenManager;
    private EconomyAPI economyAPI;
    private final Map<UUID, Long> cooldowns = new HashMap<UUID, Long>();
    private File configFile;
    private FileConfiguration config;
    private final Map<UUID, Boolean> paidPlayers = new HashMap<UUID, Boolean>();

    public DealerPlugin(WarenManager warenManager, EconomyAPI economyAPI) {
        this.warenManager = warenManager;
        this.economyAPI = economyAPI;
        this.loadConfig();
    }

    public void spawnDealer(Player player) {
        this.dealer = (Villager)player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        this.dealer.setAI(false);
        this.dealer.setSilent(true);
        this.dealer.setInvulnerable(true);
        this.dealer.setCustomName("Dealer");
        this.dealer.setCustomNameVisible(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().equals((Object)this.dealer)) {
            event.setCancelled(true);
            this.openMainGUI(event.getPlayer());
        }
    }

    private void openMainGUI(Player player) {
        if (this.paidPlayers.getOrDefault(player.getUniqueId(), false).booleanValue()) {
            this.openSecondGUI(player);
            return;
        }
        Inventory gui = Bukkit.createInventory(null, (int)9, (String)"Dealer Hauptmen\u00fc");
        ItemStack greenWool = new ItemStack(Material.GREEN_WOOL);
        ItemMeta greenMeta = greenWool.getItemMeta();
        greenMeta.setDisplayName("\u00a7aOption 1");
        greenMeta.setLore(Arrays.asList("\u00a77Klicke hier, um die zweite GUI zu \u00f6ffnen."));
        greenWool.setItemMeta(greenMeta);
        gui.setItem(2, greenWool);
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName("\u00a7eCan I trust you?");
        paperMeta.setLore(Arrays.asList("\u00a77Information: Du kannst 100 bezahlen, um fortzufahren."));
        paper.setItemMeta(paperMeta);
        gui.setItem(4, paper);
        ItemStack redWool = new ItemStack(Material.RED_WOOL);
        ItemMeta redMeta = redWool.getItemMeta();
        redMeta.setDisplayName("\u00a7cSchlie\u00dfen");
        redMeta.setLore(Arrays.asList("\u00a77Klicke hier, um das Men\u00fc zu schlie\u00dfen."));
        redWool.setItemMeta(redMeta);
        gui.setItem(6, redWool);
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = blackGlass.getItemMeta();
        glassMeta.setDisplayName("");
        blackGlass.setItemMeta(glassMeta);
        for (int i = 0; i < 9; ++i) {
            if (i == 2 || i == 4 || i == 6) continue;
            gui.setItem(i, blackGlass);
        }
        player.openInventory(gui);
    }

    private void openSecondGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)"Dealer Zweites Men\u00fc");
        ItemStack weaponBox2h = this.warenManager.getWare("weaponBox2h").createItemForGUI();
        gui.setItem(10, weaponBox2h);
        ItemStack weaponBox8h = this.warenManager.getWare("weaponBox8h").createItemForGUI();
        gui.setItem(13, weaponBox8h);
        ItemStack weaponBox24h = this.warenManager.getWare("weaponBox24h").createItemForGUI();
        gui.setItem(16, weaponBox24h);
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = blackGlass.getItemMeta();
        glassMeta.setDisplayName("");
        blackGlass.setItemMeta(glassMeta);
        for (int i = 0; i < 27; ++i) {
            if (i == 10 || i == 13 || i == 16) continue;
            gui.setItem(i, blackGlass);
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player)event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        if (title.equals("Dealer Hauptmen\u00fc")) {
            event.setCancelled(true);
            switch (clickedItem.getType()) {
                case GREEN_WOOL: {
                    if (!this.paidPlayers.getOrDefault(player.getUniqueId(), false).booleanValue()) {
                        double balance = this.economyAPI.getBalance(player);
                        if (balance >= 100.0) {
                            this.economyAPI.removeBalance(player, 100.0);
                            this.paidPlayers.put(player.getUniqueId(), true);
                            player.sendMessage("\u00a7aDu hast 100 von deinem Konto abgehoben.");
                        } else {
                            player.sendMessage("\u00a7cDu hast nicht genug Guthaben.");
                            return;
                        }
                    }
                    this.openSecondGUI(player);
                    break;
                }
                case PAPER: {
                    break;
                }
                case RED_WOOL: {
                    player.closeInventory();
                }
            }
        } else if (title.equals("Dealer Zweites Men\u00fc")) {
            event.setCancelled(true);
            switch (clickedItem.getType()) {
                case CHEST: {
                    if (clickedItem.getItemMeta() == null) break;
                    String displayName = clickedItem.getItemMeta().getDisplayName();
                    if (this.hasCooldown(player)) {
                        long remainingTime = this.getRemainingCooldown(player);
                        player.sendMessage("\u00a7cDu musst noch " + this.formatTime(remainingTime) + " warten.");
                        return;
                    }
                    long cooldownTime = 0L;
                    String wareName = "";
                    if (displayName.contains("8")) {
                        cooldownTime = 43200000L;
                        this.economyAPI.removeBalance(player, 36450.0);
                        wareName = "weaponBox24h";
                    } else if (displayName.contains("5")) {
                        cooldownTime = 28800000L;
                        this.economyAPI.removeBalance(player, 18000.0);
                        wareName = "weaponBox8h";
                    } else if (displayName.contains("1")) {
                        cooldownTime = 0x6DDD00L;
                        this.economyAPI.removeBalance(player, 2500.0);
                        wareName = "weaponBox2h";
                    }
                    this.setCooldown(player, cooldownTime);
                    this.warenManager.giveWareToPlayer(player, wareName);
                    break;
                }
                default: {
                    if (clickedItem.getType() != Material.BLACK_STAINED_GLASS_PANE) break;
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean hasCooldown(Player player) {
        return this.cooldowns.containsKey(player.getUniqueId()) && this.cooldowns.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private long getRemainingCooldown(Player player) {
        return this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
    }

    private void setCooldown(Player player, long cooldownTime) {
        UUID playerUUID = player.getUniqueId();
        long endTime = System.currentTimeMillis() + cooldownTime;
        this.cooldowns.put(playerUUID, endTime);
        this.config.set("cooldowns." + String.valueOf(playerUUID), (Object)endTime);
        this.saveConfig();
    }

    private String formatTime(long millis) {
        long hours = millis / 3600000L;
        long minutes = millis % 3600000L / 60000L;
        return hours + " Stunden und " + minutes + " Minuten";
    }

    private void loadConfig() {
        this.configFile = new File(GTM.getInstance().getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            GTM.getInstance().saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration((File)this.configFile);
        if (this.config.contains("cooldowns")) {
            for (String key : this.config.getConfigurationSection("cooldowns").getKeys(false)) {
                this.cooldowns.put(UUID.fromString(key), this.config.getLong("cooldowns." + key));
            }
        }
        if (this.config.contains("paidPlayers")) {
            for (String key : this.config.getConfigurationSection("paidPlayers").getKeys(false)) {
                this.paidPlayers.put(UUID.fromString(key), this.config.getBoolean("paidPlayers." + key));
            }
        }
    }

    private void saveConfig() {
        try {
            this.config.set("paidPlayers", this.paidPlayers);
            this.config.save(this.configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

