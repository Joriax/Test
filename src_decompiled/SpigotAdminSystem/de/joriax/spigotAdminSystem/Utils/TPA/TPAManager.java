/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.ComponentBuilder
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.ChatColor
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 */
package de.joriax.spigotAdminSystem.Utils.TPA;

import de.joriax.spigotAdminSystem.SpigotAdminSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TPAManager {
    private final Map<UUID, UUID> teleportRequests = new HashMap<UUID, UUID>();
    private final Map<UUID, UUID> teleportHereRequests = new HashMap<UUID, UUID>();
    private final Set<UUID> disabledTpaPlayers = new HashSet<UUID>();

    public void requestTeleport(Player sender, Player target) {
        this.teleportRequests.put(target.getUniqueId(), sender.getUniqueId());
        sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Teleport request sent to " + target.getName() + ".");
        this.sendClickableMessage(sender, target, false);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    public void requestTeleportHere(Player sender, Player target) {
        this.teleportHereRequests.put(target.getUniqueId(), sender.getUniqueId());
        sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Teleport request (here) sent to " + target.getName() + ".");
        this.sendClickableMessage(sender, target, true);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    private void sendClickableMessage(Player sender, Player target, boolean isTpaHere) {
        TextComponent message = new TextComponent(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.YELLOW) + sender.getName() + " wants to " + (isTpaHere ? "teleport you to them" : "teleport to you") + ". ");
        TextComponent accept = new TextComponent(String.valueOf(ChatColor.GREEN) + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        TextComponent deny = new TextComponent(String.valueOf(ChatColor.RED) + " [Deny]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny").create()));
        target.spigot().sendMessage(new BaseComponent[]{message, accept, deny});
    }

    public UUID getRequestSender(Player target) {
        UUID senderId = this.teleportRequests.get(target.getUniqueId());
        if (senderId == null) {
            senderId = this.teleportHereRequests.get(target.getUniqueId());
        }
        return senderId;
    }

    public void acceptTeleport(Player sender, Player target) {
        this.teleportRequests.remove(target.getUniqueId());
        this.teleportHereRequests.remove(target.getUniqueId());
    }

    public void denyTeleport(Player sender, Player target) {
        UUID senderId = this.teleportRequests.get(target.getUniqueId());
        if (senderId == null) {
            senderId = this.teleportHereRequests.get(target.getUniqueId());
        }
        if (senderId != null) {
            this.teleportRequests.remove(target.getUniqueId());
            this.teleportHereRequests.remove(target.getUniqueId());
            sender.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.RED) + target.getName() + " denied your teleport request.");
            target.sendMessage(SpigotAdminSystem.getPrefix() + String.valueOf(ChatColor.GREEN) + "Teleport request denied.");
            target.playSound(target.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }

    public int getTeleportDelay(Player player) {
        if (player.hasPermission("spigot.tpa.vip")) {
            return 3;
        }
        if (player.hasPermission("spigot.tpa.premium")) {
            return 5;
        }
        return 10;
    }

    public boolean isTpaEnabled(Player player) {
        return !this.disabledTpaPlayers.contains(player.getUniqueId());
    }

    public void setTpaEnabled(Player player, boolean enabled) {
        if (enabled) {
            this.disabledTpaPlayers.remove(player.getUniqueId());
        } else {
            this.disabledTpaPlayers.add(player.getUniqueId());
        }
    }

    public void clearRequests(Player player) {
        this.teleportRequests.remove(player.getUniqueId());
        this.teleportHereRequests.remove(player.getUniqueId());
    }
}

