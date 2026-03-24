/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Chest
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 */
package LootCrate;

import Armour.Armour;
import Armour.ArmourManager;
import Food.Food;
import Food.FoodManager;
import Guns.Weapon;
import Guns.WeaponManager;
import Melee.MeleeWeapon;
import Melee.MeleeWeaponManager;
import Throw.GrenadeItem;
import Throw.Moloitem;
import Throw.Smoke;
import Throw.TearItem;
import at.Poriax.gTM.GTM;
import de.joriax.economy.EconomyAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LootCratePlugin
implements Listener {
    private final Random random = new Random();
    private final EconomyAPI economyAPI;
    private final Map<Chest, ChestTimerTask> chestTimers = new ConcurrentHashMap<Chest, ChestTimerTask>();
    private static final int GRENADE_PROBABILITY = 20;
    private static final int MOLOTOV_PROBABILITY = 15;
    private static final int TEAR_GAS_PROBABILITY = 15;
    private static final int SMOKE_GRENADE_PROBABILITY = 10;

    public LootCratePlugin(EconomyAPI economyAPI) {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)GTM.getInstance());
        this.economyAPI = economyAPI;
    }

    public void onDisable() {
        for (ChestTimerTask task : this.chestTimers.values()) {
            task.cleanup();
        }
        this.chestTimers.clear();
        Bukkit.getLogger().info("LootCratePlugin disabled! All chest timers and armor stands have been cleaned up.");
    }

    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
            Player player = event.getPlayer();
            Chest chest = (Chest)event.getClickedBlock().getState();
            ChestTimerTask existingTask = this.chestTimers.get(chest);
            if (existingTask == null || existingTask.isTimerExpired()) {
                if (existingTask != null && existingTask.isTimerExpired()) {
                    int amount = 100 + this.random.nextInt(401);
                    this.economyAPI.addBalance(player, (double)amount);
                    player.sendMessage("Du hast " + amount + " erhalten!");
                    existingTask.cleanup();
                }
                ArmorStand titleStand = this.createTitleArmorStand(chest);
                ArmorStand timerStand = this.createTimerArmorStand(chest);
                int delay = 10;
                ChestTimerTask newTask = new ChestTimerTask(this, titleStand, timerStand, delay, chest);
                this.chestTimers.put(chest, newTask);
                newTask.runTaskTimer((Plugin)GTM.getInstance(), 0L, 20L);
                this.fillChestWithRandomItems(chest.getInventory());
            }
        }
    }

    private ArmorStand createTitleArmorStand(Chest chest) {
        Location chestLocation = chest.getLocation().add(0.5, -0.7, 0.5);
        ArmorStand titleStand = (ArmorStand)chest.getWorld().spawn(chestLocation, ArmorStand.class);
        titleStand.setCustomName(String.valueOf(ChatColor.YELLOW) + String.valueOf(ChatColor.BOLD) + "Lootcrate");
        titleStand.setCustomNameVisible(true);
        titleStand.setGravity(false);
        titleStand.setInvisible(true);
        return titleStand;
    }

    private ArmorStand createTimerArmorStand(Chest chest) {
        Location chestLocation = chest.getLocation().add(0.5, -1.0, 0.5);
        ArmorStand timerStand = (ArmorStand)chest.getWorld().spawn(chestLocation, ArmorStand.class);
        timerStand.setCustomNameVisible(true);
        timerStand.setGravity(false);
        timerStand.setInvisible(true);
        return timerStand;
    }

    private void fillChestWithRandomItems(Inventory inventory) {
        inventory.clear();
        int itemsToFill = this.random.nextInt(5) + 3;
        boolean[] filledSlots = new boolean[inventory.getSize()];
        int filledCount = 0;
        ArrayList<ItemStack> addedItems = new ArrayList<ItemStack>();
        while (filledCount < itemsToFill) {
            ItemStack randomItem;
            int randomIndex = this.random.nextInt(inventory.getSize());
            if (filledSlots[randomIndex] || (randomItem = this.getRandomItem(addedItems)) == null || randomItem.getType() == Material.AIR || this.isItemInList(addedItems, randomItem)) continue;
            inventory.setItem(randomIndex, randomItem);
            filledSlots[randomIndex] = true;
            addedItems.add(randomItem.clone());
            ++filledCount;
        }
        if (filledCount == 0) {
            for (int i = 0; i < 2; ++i) {
                ItemStack defaultItem = this.getRandomDefaultItem();
                if (defaultItem == null || defaultItem.getType() == Material.AIR || this.isItemInList(addedItems, defaultItem)) continue;
                inventory.addItem(new ItemStack[]{defaultItem});
                addedItems.add(defaultItem.clone());
            }
        }
    }

    private boolean isItemInList(List<ItemStack> list, ItemStack item) {
        for (ItemStack existing : list) {
            if (!existing.isSimilar(item)) continue;
            return true;
        }
        return false;
    }

    private ItemStack getRandomItem(List<ItemStack> addedItems) {
        int randChoice = this.random.nextInt(5);
        switch (randChoice) {
            case 0: {
                return this.getRandomWeapon(addedItems);
            }
            case 1: {
                return this.getRandomMeleeWeapon(addedItems);
            }
            case 2: {
                return this.getRandomFood(addedItems);
            }
            case 3: {
                return this.getRandomArmour(addedItems);
            }
            case 4: {
                return this.getRandomThrowable(addedItems);
            }
        }
        return this.getRandomDefaultItem();
    }

    private ItemStack getRandomThrowable(List<ItemStack> addedItems) {
        int totalProbability = 60;
        int randomValue = this.random.nextInt(totalProbability);
        if (randomValue < 20) {
            return this.getThrowableWithRandomAmount(GrenadeItem.createThrowableItem(), addedItems);
        }
        if (randomValue < 35) {
            return this.getThrowableWithRandomAmount(Moloitem.createMolotovCocktail(), addedItems);
        }
        if (randomValue < 50) {
            return this.getThrowableWithRandomAmount(TearItem.createTearGas(), addedItems);
        }
        return this.getThrowableWithRandomAmount(Smoke.createSmokeGrenade(), addedItems);
    }

    private ItemStack getThrowableWithRandomAmount(ItemStack throwable, List<ItemStack> addedItems) {
        int amount = this.random.nextInt(3) + 1;
        ItemStack result = throwable.clone();
        result.setAmount(amount);
        if (!this.isItemInList(addedItems, result)) {
            return result;
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getRandomDefaultItem() {
        ArrayList<ItemStack> allItems = new ArrayList<ItemStack>();
        for (Weapon weapon : WeaponManager.getWeapons().values()) {
            allItems.add(weapon.getWeaponItem());
        }
        for (MeleeWeapon meleeWeapon : MeleeWeaponManager.getMeleeWeapons().values()) {
            allItems.add(meleeWeapon.getMeleeWeaponItem());
        }
        for (Armour armour : ArmourManager.getArmours().values()) {
            allItems.add(armour.getArmourItem());
        }
        for (Food food : FoodManager.getFoods().values()) {
            allItems.add(food.getFoodItem());
        }
        allItems.add(GrenadeItem.createThrowableItem());
        allItems.add(Moloitem.createMolotovCocktail());
        allItems.add(TearItem.createTearGas());
        allItems.add(Smoke.createSmokeGrenade());
        if (!allItems.isEmpty()) {
            return ((ItemStack)allItems.get(this.random.nextInt(allItems.size()))).clone();
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getRandomArmour(List<ItemStack> addedItems) {
        ItemStack armourItem;
        Armour randomArmour;
        ArrayList<Armour> armours = new ArrayList<Armour>(ArmourManager.getArmours().values());
        if (!armours.isEmpty() && (randomArmour = this.getRandomArmourWithProbability(armours)) != null && !this.isItemInList(addedItems, armourItem = randomArmour.getArmourItem())) {
            return armourItem.clone();
        }
        return new ItemStack(Material.AIR);
    }

    private Armour getRandomArmourWithProbability(List<Armour> armours) {
        int totalWeight = 0;
        for (Armour armour : armours) {
            totalWeight += armour.getProbability();
        }
        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int currentWeight = 0;
        for (Armour armour : armours) {
            if (randomValue >= (currentWeight += armour.getProbability())) continue;
            return armour;
        }
        return null;
    }

    private ItemStack getRandomWeapon(List<ItemStack> addedItems) {
        ItemStack weaponItem;
        Weapon randomWeapon;
        ArrayList<Weapon> weapons = new ArrayList<Weapon>(WeaponManager.getWeapons().values());
        if (!weapons.isEmpty() && (randomWeapon = this.getRandomWeaponWithProbability(weapons)) != null && !this.isItemInList(addedItems, weaponItem = randomWeapon.getWeaponItem())) {
            return weaponItem.clone();
        }
        return new ItemStack(Material.AIR);
    }

    private Weapon getRandomWeaponWithProbability(List<Weapon> weapons) {
        int totalWeight = 0;
        for (Weapon weapon : weapons) {
            totalWeight += weapon.getProbability();
        }
        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int currentWeight = 0;
        for (Weapon weapon : weapons) {
            if (randomValue >= (currentWeight += weapon.getProbability())) continue;
            return weapon;
        }
        return null;
    }

    private ItemStack getRandomMeleeWeapon(List<ItemStack> addedItems) {
        ItemStack meleeWeaponItem;
        MeleeWeapon randomMeleeWeapon;
        ArrayList<MeleeWeapon> meleeWeapons = new ArrayList<MeleeWeapon>(MeleeWeaponManager.getMeleeWeapons().values());
        if (!meleeWeapons.isEmpty() && (randomMeleeWeapon = this.getRandomMeleeWeaponWithProbability(meleeWeapons)) != null && !this.isItemInList(addedItems, meleeWeaponItem = randomMeleeWeapon.getMeleeWeaponItem())) {
            return meleeWeaponItem.clone();
        }
        return new ItemStack(Material.AIR);
    }

    private MeleeWeapon getRandomMeleeWeaponWithProbability(List<MeleeWeapon> meleeWeapons) {
        int totalWeight = 0;
        for (MeleeWeapon meleeWeapon : meleeWeapons) {
            totalWeight += meleeWeapon.getProbability();
        }
        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int currentWeight = 0;
        for (MeleeWeapon meleeWeapon : meleeWeapons) {
            if (randomValue >= (currentWeight += meleeWeapon.getProbability())) continue;
            return meleeWeapon;
        }
        return null;
    }

    private ItemStack getRandomFood(List<ItemStack> addedItems) {
        ItemStack foodItem;
        Food randomFood;
        ArrayList<Food> foodItems = new ArrayList<Food>(FoodManager.getFoods().values());
        if (!foodItems.isEmpty() && (randomFood = (Food)foodItems.get(this.random.nextInt(foodItems.size()))) != null && !this.isItemInList(addedItems, foodItem = randomFood.getFoodItem())) {
            return foodItem.clone();
        }
        return new ItemStack(Material.AIR);
    }

    private class ChestTimerTask
    extends BukkitRunnable {
        private final ArmorStand titleStand;
        private final ArmorStand timerStand;
        private int timeLeft;
        private final Chest chest;
        private boolean timerExpired;

        public ChestTimerTask(LootCratePlugin lootCratePlugin, ArmorStand titleStand, ArmorStand timerStand, int timeLeft, Chest chest) {
            this.titleStand = titleStand;
            this.timerStand = timerStand;
            this.timeLeft = timeLeft;
            this.chest = chest;
            this.timerExpired = false;
            this.updateTimerDisplay();
        }

        public void run() {
            if (this.timeLeft > 0) {
                this.updateTimerDisplay();
                --this.timeLeft;
            } else {
                this.timerExpired = true;
                this.timerStand.setCustomName(String.valueOf(ChatColor.GREEN) + "Restocked!");
                this.cancel();
            }
        }

        public boolean isTimerExpired() {
            return this.timerExpired;
        }

        public int getTimeLeft() {
            return this.timeLeft;
        }

        public void cleanup() {
            if (!this.isCancelled()) {
                this.cancel();
            }
            if (this.titleStand != null && !this.titleStand.isDead()) {
                this.titleStand.remove();
            }
            if (this.timerStand != null && !this.timerStand.isDead()) {
                this.timerStand.remove();
            }
        }

        private void updateTimerDisplay() {
            if (this.timerStand != null && !this.timerStand.isDead()) {
                if (this.timeLeft > 0) {
                    int minutes = this.timeLeft / 60;
                    int seconds = this.timeLeft % 60;
                    if (minutes > 0) {
                        this.timerStand.setCustomName(String.valueOf(ChatColor.YELLOW) + "Restocks in " + minutes + " minutes, " + seconds + " seconds");
                    } else {
                        this.timerStand.setCustomName(String.valueOf(ChatColor.YELLOW) + "Restocks in " + seconds + " seconds");
                    }
                } else {
                    this.timerStand.setCustomName(String.valueOf(ChatColor.GREEN) + "Restocked!");
                }
            }
        }
    }
}

