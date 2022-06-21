package me.sedri.slayers.Data;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.sedri.slayers.Slayers;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SlayerData {
    private final HashMap<String, Integer> mobs;
    private final HashMap<EntityType, Integer> vanillaMobs = new HashMap<>();
    private final HashMap<MythicMob, Integer> MMmobs = new HashMap<>();
    private EntityType boss;
    private boolean isMMBoss = false;
    private MythicMob mythicboss;
    private boolean bossSpawned = false;
    private final int max_xp;
    private final int reward;
    private int xp = 0;
    private final String tier;
    private final String slayername;
    private final String name;
    private final ArrayList<String> description;
    private String perm = null;
    private double money = 0;
    private BossBar bossBar;

    public SlayerData(HashMap<String, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, String perm, Double money){
        defineMobtypes(mobs);
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.perm = perm;
        this.money = money;
        this.slayername = slayername;
    }

    public SlayerData(HashMap<String, Integer> mobs, MythicMob mboss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, String perm, Double money){
        this.mobs = mobs;
        defineMobtypes(mobs);
        this.mythicboss = mboss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.perm = perm;
        this.money = money;
        this.slayername = slayername;
        this.isMMBoss = true;
    }
    /*public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.slayername = slayername;
    }
    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, String perm){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.perm = perm;
        this.slayername = slayername;
    }
    public SlayerData(HashMap<EntityType, Integer> mobs, EntityType boss, Integer max_xp, Integer reward, String tier, String name, String slayername, ArrayList<String> desc, Double money){
        this.mobs = mobs;
        this.boss = boss;
        this.max_xp = max_xp;
        this.reward = reward;
        this.tier = tier;
        this.name = name;
        this.description = desc;
        this.money = money;
        this.slayername = slayername;
    }*/

    public SlayerData(SlayerData data, Player p){
        defineMobtypes(data.getMobs());
        this.mobs = data.getMobs();
        this.boss = data.getBoss();
        this.max_xp = data.getMax_xp();
        this.reward = data.getReward();
        this.bossSpawned = data.isBossSpawned();
        this.xp = data.getXp();
        this.tier = data.getTier();
        this.name = data.getName();
        this.description = data.getDescription();
        this.perm = data.getPerm();
        this.money = data.getMoney();
        this.bossBar = data.getBossBar();
        this.slayername = data.getSlayername();
        this.isMMBoss = data.isMMBoss;
        this.mythicboss = data.getMythicboss();
    }

    public String getTier() {
        return tier;
    }

    public HashMap<String, Integer> getMobs() {
        return mobs;
    }

    public EntityType getBoss() {
        return boss;
    }

    public int getReward() {
        return reward;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossSpawned(Boolean bossSpawned){
        this.bossSpawned = bossSpawned;
    }

    public int getMax_xp() {
        return max_xp;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int xp_to_add){
        xp = xp + xp_to_add;
        updateBossBar();
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void setToMaxXp(){
        xp = max_xp;
    }
    public String getName() {
        return name;
    }

    public ArrayList<String> getDescription() {
        return description;
    }

    public String getSlayername() {
        return slayername;
    }

    public String getPerm() {
        return perm;
    }

    public double getMoney() {
        return money;
    }

    public boolean isMMBoss() {
        return isMMBoss;
    }

    public boolean canStart(Player p){
        if (perm != null) {
            if (!p.hasPermission(perm)) return false;
        }
        if (money > 0) {
            Economy econ = Slayers.getEconomy();
            if (!(econ.getBalance(p) >= money)) return false;
            econ.withdrawPlayer(p, money);
        }
        return true;
    }

    public boolean reachedMaxXp(){
        if (xp >= max_xp){
            removeBossBar();
            return true;
        }
        return false;
    }

    public void removeBossBar(){
        bossBar.setVisible(false);
        bossBar.removeAll();
        bossBar = null;
    }

    public MythicMob getMythicboss() {
        return mythicboss;
    }
    public void initBossBar(Player p){
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', name + ": &c" + xp + " &4/ " + max_xp), BarColor.YELLOW, BarStyle.SOLID);
        bossBar.addPlayer(p);
        bossBar.setProgress(0);
        bossBar.setVisible(true);
    }
    public void updateBossBar(){
        bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', name + ": &c" + xp + " &4/ " + max_xp));
        if (xp*1D/max_xp > 1){
            bossBar.setProgress(1);
            return;
        }
        bossBar.setProgress(xp*1D/max_xp);
    }

    public HashMap<EntityType, Integer> getVanillaMobs() {
        return vanillaMobs;
    }
    public HashMap<MythicMob, Integer> getMMmobs() {
        return MMmobs;
    }

    private void defineMobtypes(HashMap<String, Integer> mobs){
        Set<String> keys = mobs.keySet();
        BukkitAPIHelper api = MythicBukkit.inst().getAPIHelper();
        for (String key: keys){
            Integer xp = mobs.get(key);
            if (key.startsWith("mm_")){
                MythicMob mmboss = api.getMythicMob(key.replace("mm_", ""));
                MMmobs.put(mmboss, xp);
            } else {
                EntityType mob = EntityType.valueOf(key.toUpperCase());
                vanillaMobs.put(mob, xp);
            }
        }
    }
}
