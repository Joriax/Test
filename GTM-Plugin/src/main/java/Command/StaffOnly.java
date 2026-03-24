/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package Command;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffOnly
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler k\u00f6nnen diesen Befehl nutzen!");
            return true;
        }
        Player player = (Player)sender;
        TextComponent textComponent = new TextComponent(String.valueOf(ChatColor.GREEN) + "Change your gamemode: ");
        TextComponent survivalButton = new TextComponent(String.valueOf(ChatColor.GRAY) + "[Survival] ");
        survivalButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gamemode survival"));
        survivalButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((String)"Click to change to survival")));
        TextComponent creativeButton = new TextComponent(String.valueOf(ChatColor.GRAY) + " [Creative]");
        creativeButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gamemode creative"));
        creativeButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((String)"Click to change to creative")));
        textComponent.addExtra((BaseComponent)survivalButton);
        textComponent.addExtra((BaseComponent)creativeButton);
        player.spigot().sendMessage((BaseComponent)textComponent);
        return true;
    }
}

