/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package Food;

import Food.Food;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FoodManager {
    private static Map<String, Food> foods = new ConcurrentHashMap<String, Food>();

    public static void registerFood() {
        Food burger = new Food("Burger", Material.COOKED_BEEF, 50);
        FoodManager.addFood(burger);
        Food fries = new Food("Fries", Material.CARROT, 30);
        FoodManager.addFood(fries);
    }

    public static void addFood(Food food) {
        foods.put(food.getName().toLowerCase(), food);
    }

    public static Food getFoodByName(String name) {
        return foods.get(name.toLowerCase());
    }

    public static Map<String, Food> getFoods() {
        return foods;
    }

    public static List<ItemStack> getRandomFoodsWithProbability() {
        ArrayList<Food> foodList = new ArrayList<Food>(foods.values());
        int totalWeight = 0;
        for (Food food : foodList) {
            totalWeight += food.getProbability();
        }
        ArrayList<ItemStack> selectedFoods = new ArrayList<ItemStack>();
        block1: for (int i = 0; i < 5; ++i) {
            int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
            int currentWeight = 0;
            for (Food food : foodList) {
                if (randomValue >= (currentWeight += food.getProbability())) continue;
                selectedFoods.add(food.getFoodItem());
                continue block1;
            }
        }
        return selectedFoods;
    }
}

