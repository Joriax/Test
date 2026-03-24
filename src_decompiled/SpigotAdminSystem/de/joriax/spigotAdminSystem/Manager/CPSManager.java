/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Manager;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class CPSManager {
    private final Map<Player, Integer> clickCounts = new HashMap<Player, Integer>();
    private final Map<Player, Long> combatStartTimes = new HashMap<Player, Long>();
    private final Map<Player, Player> monitoredPlayers = new HashMap<Player, Player>();

    public void click(Player player) {
        this.clickCounts.put(player, this.clickCounts.getOrDefault(player, 0) + 1);
    }

    public boolean isInCombat(Player player) {
        return this.combatStartTimes.containsKey(player);
    }

    public void startCombat(Player player) {
        this.combatStartTimes.put(player, System.currentTimeMillis());
        this.scheduleCPSDisplay(player);
    }

    private void scheduleCPSDisplay(Player player) {
        new Thread(() -> {
            try {
                while (this.combatStartTimes.containsKey(player)) {
                    Thread.sleep(1000L);
                    int clicks = this.clickCounts.getOrDefault(player, 0);
                    Player monitor = this.monitoredPlayers.get(player);
                    if (monitor != null) {
                        monitor.sendMessage(SpigotAdminSystem.getPrefix() + "CPS from " + player.getName() + ": \u00a7e" + clicks);
                    }
                    this.clickCounts.put(player, 0);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void monitorPlayer(Player monitor, Player target) {
        this.monitoredPlayers.put(target, monitor);
    }

    public void stopMonitoring(Player monitor, Player target) {
        this.monitoredPlayers.remove(target);
        this.combatStartTimes.remove(target);
        this.clickCounts.remove(target);
    }
}

