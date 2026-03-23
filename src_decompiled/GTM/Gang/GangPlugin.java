/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.joriax.economy.EconomyAPI
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.plugin.java.JavaPlugin
 */
package Gang;

import de.joriax.economy.EconomyAPI;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GangPlugin
implements Listener,
CommandExecutor {
    private final JavaPlugin plugin;
    private HashMap<UUID, Set<UUID>> gangs = new HashMap();
    private HashMap<UUID, UUID> pendingInvites = new HashMap();
    private HashMap<UUID, Inventory> gangGUIs = new HashMap();
    private HashMap<UUID, Set<UUID>> coLeaders = new HashMap();
    private static final int MAX_GANG_SIZE = 7;
    private HashMap<UUID, String> gangNames = new HashMap();
    private HashMap<UUID, Integer> gangXP = new HashMap();
    private HashMap<UUID, Integer> gangLevels = new HashMap();
    private EconomyAPI economyAPI;

    public GangPlugin(JavaPlugin plugin, EconomyAPI economyAPI) {
        this.plugin = plugin;
        this.economyAPI = economyAPI;
    }

    public void saveGangData() {
        File file = new File(this.plugin.getDataFolder(), "gangs.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> entry : this.gangs.entrySet()) {
            config.set("gangs." + entry.getKey().toString(), (Object)entry.getValue().stream().map(UUID::toString).toArray());
        }
        for (Map.Entry<UUID, Set<UUID>> entry : this.coLeaders.entrySet()) {
            config.set("coLeaders." + entry.getKey().toString(), (Object)entry.getValue().stream().map(UUID::toString).toArray());
        }
        for (Map.Entry<UUID, Object> entry : this.gangNames.entrySet()) {
            config.set("gangNames." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Object> entry : this.gangXP.entrySet()) {
            config.set("gangXP." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Object> entry : this.gangLevels.entrySet()) {
            config.set("gangLevels." + entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGangData() {
        UUID leaderUUID;
        File file = new File(this.plugin.getDataFolder(), "gangs.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)file);
        if (config.contains("gangs")) {
            for (String leaderId : config.getConfigurationSection("gangs").getKeys(false)) {
                leaderUUID = UUID.fromString(leaderId);
                HashSet<UUID> members = new HashSet<UUID>();
                for (String memberId : config.getStringList("gangs." + leaderId)) {
                    members.add(UUID.fromString(memberId));
                }
                this.gangs.put(leaderUUID, members);
            }
        }
        if (config.contains("coLeaders")) {
            for (String leaderId : config.getConfigurationSection("coLeaders").getKeys(false)) {
                leaderUUID = UUID.fromString(leaderId);
                HashSet<UUID> coLeadersSet = new HashSet<UUID>();
                for (String coLeaderId : config.getStringList("coLeaders." + leaderId)) {
                    coLeadersSet.add(UUID.fromString(coLeaderId));
                }
                this.coLeaders.put(leaderUUID, coLeadersSet);
            }
        }
        if (config.contains("gangNames")) {
            for (String leaderId : config.getConfigurationSection("gangNames").getKeys(false)) {
                leaderUUID = UUID.fromString(leaderId);
                String gangName = config.getString("gangNames." + leaderId);
                this.gangNames.put(leaderUUID, gangName);
            }
        }
        if (config.contains("gangXP")) {
            for (String leaderId : config.getConfigurationSection("gangXP").getKeys(false)) {
                leaderUUID = UUID.fromString(leaderId);
                int xp = config.getInt("gangXP." + leaderId);
                this.gangXP.put(leaderUUID, xp);
            }
        }
        if (config.contains("gangLevels")) {
            for (String leaderId : config.getConfigurationSection("gangLevels").getKeys(false)) {
                leaderUUID = UUID.fromString(leaderId);
                int level = config.getInt("gangLevels." + leaderId);
                this.gangLevels.put(leaderUUID, level);
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command!");
            return true;
        }
        Player player = (Player)sender;
        if (command.getName().equalsIgnoreCase("gang")) {
            if (args.length == 0) {
                this.openGangGUI(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 2) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /gang create <gangName>");
                    return true;
                }
                String gangName = args[1];
                this.createGang(player, gangName);
                return true;
            }
            if (args[0].equalsIgnoreCase("invite")) {
                if (args.length < 2) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /gang invite <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer((String)args[1]);
                if (target == null) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
                    return true;
                }
                this.invitePlayer(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                if (args.length < 2) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /gang accept <gangName>");
                    return true;
                }
                String gangName = args[1];
                this.acceptInvite(player, gangName);
                return true;
            }
            if (args[0].equalsIgnoreCase("decline")) {
                this.declineInvite(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("promote")) {
                if (args.length < 2) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /gang promote <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer((String)args[1]);
                if (target == null) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
                    return true;
                }
                this.promotePlayer(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                this.leaveGang(player);
                return true;
            }
            if (args[0].equalsIgnoreCase("kick")) {
                if (args.length < 2) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /gang kick <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer((String)args[1]);
                if (target == null) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
                    return true;
                }
                this.kickPlayer(player, target);
                return true;
            }
            if (args[0].equalsIgnoreCase("xp")) {
                this.showGangXP(player);
                return true;
            }
        }
        return false;
    }

    private void createGang(Player player, String gangName) {
        if (this.findGangLeader(player.getUniqueId()) != null) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "You are already in a gang!");
            return;
        }
        if (this.gangNames.containsValue(gangName)) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "A gang with this name already exists!");
            return;
        }
        HashSet<UUID> newGang = new HashSet<UUID>();
        newGang.add(player.getUniqueId());
        this.gangs.put(player.getUniqueId(), newGang);
        this.gangNames.put(player.getUniqueId(), gangName);
        this.gangXP.put(player.getUniqueId(), 0);
        this.gangLevels.put(player.getUniqueId(), 0);
        Inventory gui = this.createGangGUI(player.getUniqueId());
        this.gangGUIs.put(player.getUniqueId(), gui);
        player.sendMessage(String.valueOf(ChatColor.GREEN) + "You have created a new gang named " + String.valueOf(ChatColor.YELLOW) + gangName + String.valueOf(ChatColor.GREEN) + "!");
    }

    private void openGangGUI(Player player) {
        UUID leaderId = this.findGangLeader(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "You are not in a gang!");
            return;
        }
        Inventory gui = this.createGangGUI(leaderId);
        this.gangGUIs.put(leaderId, gui);
        player.openInventory(gui);
    }

    private Inventory createGangGUI(UUID leaderId) {
        Player leader = Bukkit.getPlayer((UUID)leaderId);
        if (leader == null) {
            return null;
        }
        Inventory gui = Bukkit.createInventory(null, (int)27, (String)(String.valueOf(ChatColor.BLUE) + "Gang Members"));
        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; ++i) {
            if (i >= 9 && i < 18 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, blackGlass);
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers != null) {
            int slot = 10;
            Player leaderPlayer = Bukkit.getPlayer((UUID)leaderId);
            ItemStack leaderSkull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta leaderMeta = (SkullMeta)leaderSkull.getItemMeta();
            leaderMeta.setOwningPlayer(Bukkit.getOfflinePlayer((UUID)leaderId));
            String leaderName = Bukkit.getOfflinePlayer((UUID)leaderId).getName();
            leaderMeta.setDisplayName(String.valueOf(ChatColor.YELLOW) + leaderName);
            ArrayList<CallSite> leaderLore = new ArrayList<CallSite>();
            leaderLore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Status: " + (leaderPlayer != null ? String.valueOf(ChatColor.GREEN) + "Online" : String.valueOf(ChatColor.RED) + "Offline"))));
            leaderLore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Rank: " + String.valueOf(ChatColor.GOLD) + "Leader")));
            leaderMeta.setLore(leaderLore);
            leaderSkull.setItemMeta((ItemMeta)leaderMeta);
            gui.setItem(slot, leaderSkull);
            ++slot;
            for (UUID memberId : gangMembers) {
                if (memberId.equals(leaderId)) continue;
                Player member = Bukkit.getPlayer((UUID)memberId);
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta)skull.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer((UUID)memberId));
                String playerName = Bukkit.getOfflinePlayer((UUID)memberId).getName();
                meta.setDisplayName(String.valueOf(ChatColor.YELLOW) + playerName);
                ArrayList<CallSite> lore = new ArrayList<CallSite>();
                lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Status: " + (member != null ? String.valueOf(ChatColor.GREEN) + "Online" : String.valueOf(ChatColor.RED) + "Offline"))));
                String rank = ((Set)this.coLeaders.getOrDefault(leaderId, new HashSet())).contains(memberId) ? String.valueOf(ChatColor.YELLOW) + "Co-Leader" : String.valueOf(ChatColor.WHITE) + "Member";
                lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Rank: " + rank)));
                meta.setLore(lore);
                skull.setItemMeta((ItemMeta)meta);
                if (slot > 16) continue;
                gui.setItem(slot, skull);
                ++slot;
            }
            int currentXP = this.gangXP.getOrDefault(leaderId, 0);
            int currentLevel = this.gangLevels.getOrDefault(leaderId, 0);
            int requiredXP = this.getRequiredXPForLevel(currentLevel);
            ItemStack xpPaper = new ItemStack(Material.PAPER);
            ItemMeta paperMeta = xpPaper.getItemMeta();
            paperMeta.setDisplayName(String.valueOf(ChatColor.YELLOW) + "Gang Level: " + currentLevel);
            ArrayList<CallSite> paperLore = new ArrayList<CallSite>();
            paperLore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "XP: " + currentXP + "/" + requiredXP)));
            paperMeta.setLore(paperLore);
            xpPaper.setItemMeta(paperMeta);
            gui.setItem(22, xpPaper);
        }
        return gui;
    }

    private int getRequiredXPForLevel(int level) {
        if (level == 0) {
            return 50;
        }
        double baseXP = 50.0;
        for (int i = 0; i < level; ++i) {
            baseXP *= 1.15;
        }
        return (int)Math.round(baseXP);
    }

    private void addGangXP(UUID leaderId, int xp) {
        int currentXP = this.gangXP.getOrDefault(leaderId, 0);
        int newXP = currentXP + xp;
        int currentLevel = this.gangLevels.getOrDefault(leaderId, 0);
        int requiredXP = this.getRequiredXPForLevel(currentLevel);
        this.gangXP.put(leaderId, newXP);
        while (newXP >= requiredXP) {
            this.gangXP.put(leaderId, newXP -= requiredXP);
            this.gangLevels.put(leaderId, ++currentLevel);
            requiredXP = this.getRequiredXPForLevel(currentLevel);
            Set<UUID> members = this.gangs.get(leaderId);
            if (members == null) continue;
            for (UUID memberId : members) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null) continue;
                member.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.GREEN) + " Your gang has reached Level " + currentLevel + "!");
            }
            this.sendMilestoneReward(leaderId, currentLevel);
        }
        if (this.gangGUIs.containsKey(leaderId)) {
            Inventory gui = this.createGangGUI(leaderId);
            this.gangGUIs.put(leaderId, gui);
        }
    }

    private void sendMilestoneReward(UUID leaderId, int level) {
        Set<UUID> members;
        String milestoneMessage = null;
        int money = 0;
        switch (level) {
            case 5: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.YELLOW) + " Congratulations! Your gang has reached Level 5 - you're growing stronger!";
                money = 50000;
                break;
            }
            case 10: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.GOLD) + " Amazing! Level 10 achieved - your gang is a force to be reckoned with!";
                money = 250000;
                break;
            }
            case 15: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.RED) + " Incredible! Level 15 - your gang dominates the streets!";
                money = 500000;
                break;
            }
            case 20: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.DARK_PURPLE) + " Legendary! Level 20 - your gang is unstoppable!";
                money = 750000;
                break;
            }
            case 25: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.AQUA) + " Epic Milestone! Level 25 - you're writing gang history!";
                money = 2500000;
                break;
            }
            case 30: {
                milestoneMessage = String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG > " + String.valueOf(ChatColor.DARK_RED) + " Ultimate Power! Level 30 - your gang is a living legend!";
                money = 6000000;
            }
        }
        if (milestoneMessage != null && (members = this.gangs.get(leaderId)) != null) {
            for (UUID memberId : members) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null) continue;
                member.sendMessage(milestoneMessage);
                member.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  The gangs out there are starting to fear you, here you got some bribe money: " + String.valueOf(ChatColor.GREEN) + String.valueOf(ChatColor.BOLD) + "$" + money);
                this.economyAPI.addBalance(member, (double)money);
            }
        }
    }

    private void showGangXP(Player player) {
        UUID leaderId = this.findGangLeader(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "You are not in a gang!");
            return;
        }
        int currentXP = this.gangXP.getOrDefault(leaderId, 0);
        int currentLevel = this.gangLevels.getOrDefault(leaderId, 0);
        int requiredXP = this.getRequiredXPForLevel(currentLevel);
        player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GRAY) + "Gang Level: " + currentLevel);
        player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "          " + String.valueOf(ChatColor.GRAY) + "XP: " + currentXP + "/" + requiredXP);
    }

    public UUID findGangLeader(UUID playerId) {
        for (Map.Entry<UUID, Set<UUID>> entry : this.gangs.entrySet()) {
            if (!entry.getValue().contains(playerId)) continue;
            return entry.getKey();
        }
        return null;
    }

    public HashMap<UUID, String> getGangNames() {
        return this.gangNames;
    }

    public HashMap<UUID, Set<UUID>> getGangs() {
        return this.gangs;
    }

    private void invitePlayer(Player inviter, Player target) {
        UUID leaderId = this.findGangLeader(inviter.getUniqueId());
        if (leaderId == null) {
            inviter.sendMessage(String.valueOf(ChatColor.RED) + "You are not in a gang! Create one with /gang create.");
            return;
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers.contains(target.getUniqueId())) {
            inviter.sendMessage(String.valueOf(ChatColor.RED) + "The player is already in your gang!");
            return;
        }
        Set coLeaderSet = this.coLeaders.getOrDefault(leaderId, new HashSet());
        if (!leaderId.equals(inviter.getUniqueId()) && !coLeaderSet.contains(inviter.getUniqueId())) {
            inviter.sendMessage(String.valueOf(ChatColor.RED) + "Only the leader and co-leaders can invite players!");
            return;
        }
        if (gangMembers.size() >= 7) {
            inviter.sendMessage(String.valueOf(ChatColor.RED) + "Your gang is already full (max 7 members)!");
            return;
        }
        this.pendingInvites.put(target.getUniqueId(), leaderId);
        String gangName = this.gangNames.get(leaderId);
        target.sendMessage(String.valueOf(ChatColor.GREEN) + "GANG > " + String.valueOf(ChatColor.YELLOW) + inviter.getName() + String.valueOf(ChatColor.GREEN) + " has invited you to their gang named " + String.valueOf(ChatColor.YELLOW) + gangName + String.valueOf(ChatColor.GREEN) + "!");
        target.sendMessage(String.valueOf(ChatColor.GREEN) + "Type " + String.valueOf(ChatColor.YELLOW) + "/gang accept " + gangName + String.valueOf(ChatColor.GREEN) + " to join or " + String.valueOf(ChatColor.YELLOW) + "/gang decline" + String.valueOf(ChatColor.GREEN) + " to decline.");
        inviter.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GRAY) + "You have invited " + String.valueOf(ChatColor.GOLD) + " " + String.valueOf(ChatColor.BOLD) + target.getName() + String.valueOf(ChatColor.GRAY) + " to the gang");
    }

    private void acceptInvite(Player player, String gangName) {
        UUID leaderId = this.pendingInvites.get(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You have no pending gang invites!");
            return;
        }
        String invitedGangName = this.gangNames.get(leaderId);
        if (invitedGangName == null || !invitedGangName.equalsIgnoreCase(gangName)) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You have no pending invite for this gang!");
            return;
        }
        if (this.findGangLeader(player.getUniqueId()) != null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You are already in a gang! Leave your current gang with /gang leave.");
            return;
        }
        Player leader = Bukkit.getPlayer((UUID)leaderId);
        if (leader == null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "The player who invited you is no longer online.");
            return;
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers.size() >= 7) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "The gang is already full (max 7 members)!");
            this.pendingInvites.remove(player.getUniqueId());
            return;
        }
        Set coLeaderSet = this.coLeaders.getOrDefault(leaderId, new HashSet());
        coLeaderSet.remove(player.getUniqueId());
        this.coLeaders.put(leaderId, coLeaderSet);
        gangMembers.add(player.getUniqueId());
        this.gangs.put(leaderId, gangMembers);
        player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GREEN) + "You have joined " + leader.getName() + "'s gang named " + String.valueOf(ChatColor.YELLOW) + gangName + String.valueOf(ChatColor.GREEN) + "!");
        leader.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GREEN) + player.getName() + " has joined your gang!");
        this.pendingInvites.remove(player.getUniqueId());
        Inventory gui = this.gangGUIs.get(leaderId);
        if (gui != null) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta)skull.getItemMeta();
            meta.setOwningPlayer((OfflinePlayer)player);
            meta.setDisplayName(String.valueOf(ChatColor.YELLOW) + player.getName());
            ArrayList<CallSite> lore = new ArrayList<CallSite>();
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Status: " + String.valueOf(ChatColor.GREEN) + "Online")));
            lore.add((CallSite)((Object)(String.valueOf(ChatColor.GRAY) + "Rank: " + String.valueOf(ChatColor.WHITE) + "Member")));
            meta.setLore(lore);
            skull.setItemMeta((ItemMeta)meta);
            gui.addItem(new ItemStack[]{skull});
        }
    }

    private void declineInvite(Player player) {
        UUID leaderId = this.pendingInvites.get(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You have no pending gang invites!");
            return;
        }
        Player leader = Bukkit.getPlayer((UUID)leaderId);
        if (leader != null) {
            leader.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + player.getName() + " has declined your gang invite.");
        }
        player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You have declined the gang invite.");
        this.pendingInvites.remove(player.getUniqueId());
    }

    private void promotePlayer(Player promoter, Player target) {
        UUID leaderId = this.findGangLeader(promoter.getUniqueId());
        if (leaderId == null) {
            promoter.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You are not in a gang!");
            return;
        }
        if (!leaderId.equals(promoter.getUniqueId())) {
            promoter.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "Only the leader can promote players!");
            return;
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers == null || !gangMembers.contains(target.getUniqueId())) {
            promoter.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "The player is not in your gang!");
            return;
        }
        this.coLeaders.computeIfAbsent(leaderId, k -> new HashSet()).add(target.getUniqueId());
        promoter.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.WHITE) + "You have promoted " + target.getName() + " to co-leader!");
        target.sendMessage(String.valueOf(ChatColor.GREEN) + "You have been promoted to co-leader!");
    }

    private void leaveGang(Player player) {
        UUID leaderId = this.findGangLeader(player.getUniqueId());
        if (leaderId == null) {
            player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You are not in a gang!");
            return;
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers == null) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "An error occurred. Please contact an admin.");
            return;
        }
        Set coLeaderSet = this.coLeaders.getOrDefault(leaderId, new HashSet());
        coLeaderSet.remove(player.getUniqueId());
        this.coLeaders.put(leaderId, coLeaderSet);
        if (leaderId.equals(player.getUniqueId())) {
            String gangName = this.gangNames.get(leaderId);
            for (UUID memberId : gangMembers) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null) continue;
                member.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "The gang has been disbanded because the leader left.");
            }
            this.gangs.remove(leaderId);
            this.gangGUIs.remove(leaderId);
            this.coLeaders.remove(leaderId);
            this.gangXP.remove(leaderId);
            this.gangLevels.remove(leaderId);
            this.gangNames.remove(leaderId);
        } else {
            gangMembers.remove(player.getUniqueId());
            Player leader = Bukkit.getPlayer((UUID)leaderId);
            if (leader != null) {
                leader.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + player.getName() + " has left your gang.");
            }
        }
        player.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GREEN) + "You have left the gang!");
    }

    private void kickPlayer(Player kicker, Player target) {
        UUID leaderId = this.findGangLeader(kicker.getUniqueId());
        if (leaderId == null) {
            kicker.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You are not in a gang!");
            return;
        }
        Set coLeaderSet = this.coLeaders.getOrDefault(leaderId, new HashSet());
        if (!leaderId.equals(kicker.getUniqueId()) && !coLeaderSet.contains(kicker.getUniqueId())) {
            kicker.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "Only the leader and co-leaders can kick players!");
            return;
        }
        Set<UUID> gangMembers = this.gangs.get(leaderId);
        if (gangMembers == null || !gangMembers.contains(target.getUniqueId())) {
            kicker.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "The player is not in your gang!");
            return;
        }
        if (leaderId.equals(target.getUniqueId())) {
            kicker.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You cannot kick the leader of the gang!");
            return;
        }
        gangMembers.remove(target.getUniqueId());
        coLeaderSet.remove(target.getUniqueId());
        this.coLeaders.put(leaderId, coLeaderSet);
        kicker.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GREEN) + "You have kicked " + target.getName() + " from the gang.");
        target.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You have been kicked from the gang.");
        Inventory gui = this.gangGUIs.get(leaderId);
        if (gui != null) {
            gui.clear();
            for (UUID memberId : gangMembers) {
                Player member = Bukkit.getPlayer((UUID)memberId);
                if (member == null) continue;
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta)skull.getItemMeta();
                meta.setOwningPlayer((OfflinePlayer)member);
                skull.setItemMeta((ItemMeta)meta);
                gui.addItem(new ItemStack[]{skull});
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(String.valueOf(ChatColor.BLUE) + "Gang Members")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player target = (Player)event.getEntity();
            for (Set<UUID> gangMembers : this.gangs.values()) {
                if (!gangMembers.contains(damager.getUniqueId()) || !gangMembers.contains(target.getUniqueId())) continue;
                event.setCancelled(true);
                damager.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.RED) + "You cannot attack your gang members!");
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        UUID leaderId;
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer != null && !killer.equals((Object)victim) && (leaderId = this.findGangLeader(killer.getUniqueId())) != null) {
            this.addGangXP(leaderId, 1000);
            killer.sendMessage(String.valueOf(ChatColor.GREEN) + " " + String.valueOf(ChatColor.BOLD) + "GANG >  " + String.valueOf(ChatColor.GREEN) + "+5 Gang XP");
        }
    }
}

