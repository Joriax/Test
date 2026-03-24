/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package Dealer;

import Dealer.Ware;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarenManager {
    private final Map<String, Ware> warenList = new HashMap<String, Ware>();

    public void addWare(Ware ware) {
        this.warenList.put(ware.getName(), ware);
    }

    public Ware getWare(String name) {
        return this.warenList.get(name);
    }

    public void giveWareToPlayer(Player player, String wareName) {
        Ware ware = this.getWare(wareName);
        if (ware != null) {
            ItemStack item = ware.createItemForPlayer();
            int amount = ware.getAmount();
            item.setAmount(amount);
            player.getInventory().addItem(new ItemStack[]{item});
            player.sendMessage("Du hast " + amount + "x '" + ware.getDisplayName() + "' erhalten!");
        } else {
            player.sendMessage("Diese Ware existiert nicht!");
        }
    }

    public void registerWaren() {
        ArrayList<String> weaponBoxGUILore2h = new ArrayList<String>();
        weaponBoxGUILore2h.add(String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.BOLD) + "Price: " + String.valueOf(ChatColor.GRAY) + "2 500 $");
        weaponBoxGUILore2h.add(String.valueOf(ChatColor.GRAY) + "Hmm... those goods seem to be in great shape!");
        weaponBoxGUILore2h.add(String.valueOf(ChatColor.GRAY) + "I could try sell them somewhere..");
        weaponBoxGUILore2h.add(String.valueOf(ChatColor.RED) + "Cooldown: 2 Stunden");
        ArrayList<String> weaponBoxItemLore = new ArrayList<String>();
        weaponBoxItemLore.add(String.valueOf(ChatColor.GRAY) + "Hmm... those goods seem to be in great shape!");
        weaponBoxItemLore.add(String.valueOf(ChatColor.GRAY) + "I could try sell them somewhere..");
        Ware weaponBox2h = new Ware("weaponBox2h", Material.CHEST, String.valueOf(ChatColor.GOLD) + " " + String.valueOf(ChatColor.BOLD) + "Weapon Box x1", weaponBoxGUILore2h, weaponBoxItemLore, 1, 1);
        this.addWare(weaponBox2h);
        ArrayList<String> weaponBoxGUILore8h = new ArrayList<String>();
        weaponBoxGUILore8h.add(String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.BOLD) + "Price: " + String.valueOf(ChatColor.GRAY) + "18 000 $");
        weaponBoxGUILore8h.add(String.valueOf(ChatColor.GRAY) + "Hmm... those goods seem to be in great shape!");
        weaponBoxGUILore8h.add(String.valueOf(ChatColor.GRAY) + "I could try sell them somewhere..");
        weaponBoxGUILore8h.add(String.valueOf(ChatColor.RED) + "Cooldown: 8 Stunden");
        Ware weaponBox8h = new Ware("weaponBox8h", Material.CHEST, String.valueOf(ChatColor.GOLD) + " " + String.valueOf(ChatColor.BOLD) + "Weapon Box x5", weaponBoxGUILore8h, weaponBoxItemLore, 2, 5);
        this.addWare(weaponBox8h);
        ArrayList<String> weaponBoxGUILore24h = new ArrayList<String>();
        weaponBoxGUILore24h.add(String.valueOf(ChatColor.GOLD) + String.valueOf(ChatColor.BOLD) + "Price: " + String.valueOf(ChatColor.GRAY) + "36 450 $");
        weaponBoxGUILore24h.add(String.valueOf(ChatColor.GRAY) + "Hmm... those goods seem to be in great shape!");
        weaponBoxGUILore24h.add(String.valueOf(ChatColor.GRAY) + "I could try sell them somewhere..");
        weaponBoxGUILore24h.add(String.valueOf(ChatColor.RED) + "Cooldown: 12 Stunden");
        Ware weaponBox24h = new Ware("weaponBox24h", Material.CHEST, String.valueOf(ChatColor.GOLD) + " " + String.valueOf(ChatColor.BOLD) + "Weapon Box x18", weaponBoxGUILore24h, weaponBoxItemLore, 3, 18);
        this.addWare(weaponBox24h);
    }
}

