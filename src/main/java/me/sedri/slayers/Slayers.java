package me.sedri.slayers;

import me.sedri.slayers.Commands.SlayerCommand;
import me.sedri.slayers.Commands.test;
import me.sedri.slayers.Data.*;
import me.sedri.slayers.Gui.MainSlayerGui;
import me.sedri.slayers.Listeners.PlayerInteractListener;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class Slayers extends JavaPlugin {
    private static Slayers plugin;
    private static Economy econ = null;
    public HashMap<Player, SlayerData> activeSlayer = new HashMap<>();
    public LinkedHashMap<String, SlayerData> allSlayers = new LinkedHashMap<>();
    public ItemStack[] mainslayermenu = new ItemStack[54];
    public ArrayList<ItemStack> slayermenu;
    public HashMap<String, ArrayList<Integer>> LevelList = new HashMap<>();
    public HashMap<String, ArrayList<SlayerLevel>> Levels = new HashMap<>();
    public LinkedHashMap<Integer, String> slayermenuindex = new LinkedHashMap<>();
    public LinkedHashMap<String, ItemStack> slayersubmenu = new LinkedHashMap<>();
    public static List<String> slayerkeys = new ArrayList<>();

    public static Slayers getPlugin(){
        return plugin;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        plugin = this;
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        //Player p = getServer().getPlayer(UUID.fromString("0b0172c6-e10f-49dc-9f27-c9cf12e9ed7b"));
        try {
            SlayerXpStorage.loadPlayerSlayerXp();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readyEvents();
        readyCommands();
        //getConfig().options().copyDefaults();
        //saveDefaultConfig();
        saveResource("slayers.yml", false);
        /*if (SlayerConfig.getFile().length() == 0){
            saveResource("slayers.yml", true);
        }*/
        SlayerConfig.setup();
        readySlayers();
        try {
            initDatabase();
            System.out.println("oi");
        } catch (SQLException e){
            getLogger().severe("Failed To load SQL database");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            SlayerXpStorage.savePlayerSlayerXp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void readyEvents(){
        PluginManager m = getServer().getPluginManager();
        m.registerEvents(new PlayerInteractListener(), this);
    }

    private void readyCommands(){

        Objects.requireNonNull(getCommand("slayer")).setExecutor(new SlayerCommand());
        Objects.requireNonNull(getCommand("sql")).setExecutor(new test());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void addPermission(UUID uuid, String permission) {
        // Add the permission
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            user.data().add(Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void removePermission(UUID uuid, String permission) {
        // remove the permission
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user != null) {
            user.data().remove(Node.builder(permission).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    private void initDatabase() throws SQLException {
        System.out.println("got here at least");
        Connection conn;
        conn = SlayerSQL.newConnection();
        if (conn == null) return;
        for (String key: slayerkeys) {
            try (Statement statement = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS "+key+"(UUID TEXT, SLAYER TEXT, XP INT, LEVEL INT)";
                statement.execute(sql);
            }
        }
        conn.close();
    }

    public void readySlayers(){
        slayermenu = new ArrayList<>();
        mainslayermenu = new ItemStack[54];
        slayermenuindex = new LinkedHashMap<>();
        slayersubmenu = new LinkedHashMap<>();
        allSlayers = new LinkedHashMap<>();
        ItemStack fillitem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        fillitem.getItemMeta().setDisplayName("");
        Arrays.fill(mainslayermenu, fillitem);
        Set<String> keys = SlayerConfig.get().getKeys(false);
        mainslayermenu[mainslayermenu.length-5] = MainSlayerGui.createGuiItem(Material.BARRIER, "&cClose");
        int i = 10;
        for (String key: keys){
            slayerkeys.add(key);
            ConfigurationSection slayer = SlayerConfig.get().getConfigurationSection(key);
            if (slayer == null) continue;
            Material mat = Material.ZOMBIE_HEAD;
            String mate = slayer.getString("material");
            if (mate != null) {
                mat = Material.valueOf(mate.toUpperCase());
            } else {
                getLogger().warning("Invalid material set in " + key);
            }
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String slayername = slayer.getString("name");
            if (slayername == null){
                slayername = key;
            }
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', slayername));
            List<String> lorelist = slayer.getStringList("description");
            ArrayList<String> lore = new ArrayList<>();
            for (String loreline : lorelist) {
                lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            if (i == 17 || i == 26 || i == 35){
                i+=2;
            } else if (i >= 44){
                break;
            }
            mainslayermenu[i] = item;
            slayermenu.add(item);
            slayermenuindex.put(i, key);
            i++;
            HashMap<EntityType, Integer> mobs = new HashMap<>();
            List<String> moblist = slayer.getStringList("mob-list");
            for (String mob: moblist) {
                String[] mobs2 = mob.split(":");
                try {
                    EntityType mob2 = EntityType.valueOf(mobs2[0].toUpperCase());
                    mobs.put(mob2, Integer.parseInt(mobs2[1]));
                } catch (IllegalArgumentException e){
                    getLogger().warning("Invalid mob defined in " + key);
                }
            }
            ConfigurationSection tiers = slayer.getConfigurationSection("tiers");
            if (tiers == null) {
                getLogger().warning("No tiers defined in " + key);
                continue;
            }
            Set<String> tierkeys = tiers.getKeys(false);
            for (String tierkey: tierkeys){
                ConfigurationSection tier = tiers.getConfigurationSection(tierkey);
                if (tier == null) continue;
                mat = Material.ZOMBIE_HEAD;
                mate = tier.getString("material");
                if (mate != null) {
                    mat = Material.valueOf(mate.toUpperCase());
                } else {
                    getLogger().warning("Invalid material set in " + tierkey + "in "+ key);
                }
                item = new ItemStack(mat);
                meta = item.getItemMeta();
                String name = tier.getString("name");
                if (name == null) {
                    name = tierkey;
                }
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
                lorelist = tier.getStringList("description");
                lore = new ArrayList<>();
                for (String loreline : lorelist) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', loreline));
                }
                String perm = tier.getString("required-perm");
                Double money = tier.getDouble("required-coins");
                meta.setLore(lore);
                item.setItemMeta(meta);
                EntityType type = null;
                String mob = tier.getString("boss");
                if (mob != null) {
                    type = EntityType.valueOf(mob.toUpperCase());
                } else {
                    getLogger().warning("Invalid boss mob set in "+ tierkey + " in " + key);
                }
                Integer max_xp = tier.getInt("required-xp");
                Integer reward_xp = tier.getInt("reward-xp");
                String id = key + ":" + tierkey;
                if (type != null) {
                    slayersubmenu.put(id, item);
                    SlayerData data = new SlayerData(mobs, type, max_xp, reward_xp, id, name, slayername, lore, perm, money);
                    allSlayers.put(id, data);
                }
            }
            ConfigurationSection levels = slayer.getConfigurationSection("levels");
            if (levels == null) {
                getLogger().warning("No tiers defined in " + key);
                continue;
            }
            Set<String> levelkeys = levels.getKeys(false);
            ArrayList<Integer> levelist = new ArrayList<>();
            ArrayList<SlayerLevel> slayerlevels = new ArrayList<>();
            for (String levelkey: levelkeys) {
                ConfigurationSection level = levels.getConfigurationSection(levelkey);
                if (level == null) continue;
                levelist.add(Integer.parseInt(levelkey));
                ArrayList<String> rewards = (ArrayList<String>) level.getStringList("rewards-lore");
                ArrayList<String> commands = (ArrayList<String>) level.getStringList("commands");
                ArrayList<String> permissions = (ArrayList<String>) level.getStringList("permissions");
                slayerlevels.add(new SlayerLevel(Integer.parseInt(levelkey), rewards, commands, permissions));
            }
            Levels.put(key, slayerlevels);
            LevelList.put(key, levelist);
        }
    }
}
