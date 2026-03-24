/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package Food;

import Food.Food;
import Food.FoodManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FoodCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (args.length == 2) {
                int amount;
                String foodName = args[0];
                try {
                    amount = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException e) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Ung\u00fcltige Menge! Bitte gib eine Zahl an.");
                    return false;
                }
                Food food = FoodManager.getFoodByName(foodName);
                if (food != null) {
                    ItemStack foodItem = food.getFoodItem();
                    foodItem.setAmount(amount);
                    player.getInventory().addItem(new ItemStack[]{foodItem});
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "Du hast " + amount + "x " + food.getName() + " erhalten!");
                } else {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Unbekanntes Essen: " + foodName);
                }
            } else {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Verwendung: /givefood <foodName> <amount>");
            }
        } else {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Nur Spieler k\u00f6nnen diesen Befehl verwenden!");
        }
        return true;
    }
}

