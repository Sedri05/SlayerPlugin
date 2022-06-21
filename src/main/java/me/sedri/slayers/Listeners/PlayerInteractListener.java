package me.sedri.slayers.Listeners;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.sedri.slayers.Data.SlayerData;
import me.sedri.slayers.Data.SlayerSQL;
import me.sedri.slayers.Data.SlayerXp;
import me.sedri.slayers.Slayers;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Set;

public class PlayerInteractListener implements Listener {
    public static HashMap<ActiveMob, Player> activeMobHashMap = new HashMap<>();
    public static HashMap<Player, ActiveMob> activeMobHashMapReverse = new HashMap<>();
    private final Slayers plugin = Slayers.getPlugin();

    @EventHandler
    public void playerKillEvent(EntityDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p == null) return;
        if (plugin.activeSlayer.containsKey(p)) {
            SlayerData slayer = plugin.activeSlayer.get(p);
            if (!slayer.isBossSpawned()) {
                Integer xp = slayer.getVanillaMobs().get(e.getEntity().getType());
                if (xp != null) {
                    slayer.addXp(xp);
                    if (slayer.reachedMaxXp()) {
                        slayer.setBossSpawned(true);
                        p.sendMessage("");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour " + slayer.getName() + " &cis spawning!"));
                        p.sendMessage("");
                        if (slayer.isMMBoss()) {
                            MythicMob boss = slayer.getMythicboss();
                            AbstractWorld world = new BukkitWorld(e.getEntity().getWorld());
                            AbstractLocation loc = new AbstractLocation(world, e.getEntity().getLocation().getX(),
                                    e.getEntity().getLocation().getY(), e.getEntity().getLocation().getZ());
                            ActiveMob mob = boss.spawn(loc, 1);
                            activeMobHashMap.put(mob, p);
                            activeMobHashMapReverse.put(p, mob);
                        } else {
                            EntityType bosstype = slayer.getBoss();
                            e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), bosstype);
                        }
                    }
                }
            } else {
                if (e.getEntity().getType().equals(slayer.getBoss())) {
                    String tier = slayer.getTier().split(":")[0];
                    SlayerXp slayerplayer = SlayerSQL.getUser(p.getUniqueId(), tier);
                    if (slayerplayer == null) return;
                    slayerplayer.addXp(slayer.getReward());
                    SlayerSQL.saveSlayerXp(slayerplayer);
                    String bar = slayerplayer.getBar();
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSLAYER DEFEATED!"));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have gained &e" + slayer.getReward() + " " + slayer.getSlayername() + " &aXP!"));
                    if (!slayerplayer.reachedMaxLevel()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aProgress to level " + slayerplayer.getNextLevel() + " " + bar + " &e" + slayerplayer.getPercent()));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lMAX LEVEL &e" + slayerplayer.getPercent()));
                    }
                    p.sendMessage("");
                    plugin.activeSlayer.remove(p);
                }
            }
        }
    }

    @EventHandler
    public void onMMKill(MythicMobDeathEvent e){
        if (!(e.getKiller() instanceof Player p)) return;
        if (plugin.activeSlayer.containsKey(p)) {
            SlayerData slayer = plugin.activeSlayer.get(p);
            if (!slayer.isBossSpawned()) {
                Integer xp = slayer.getMMmobs().get(e.getMobType());
                if (xp != null) {
                    slayer.addXp(xp);
                    if (slayer.reachedMaxXp()) {
                        slayer.setBossSpawned(true);
                        p.sendMessage("");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour " + slayer.getName() + " &cis spawning!"));
                        p.sendMessage("");
                        if (slayer.isMMBoss()) {
                            MythicMob boss = slayer.getMythicboss();
                            AbstractWorld world = new BukkitWorld(p.getWorld());
                            AbstractLocation loc = new AbstractLocation(world, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
                            ActiveMob mob = boss.spawn(loc, 1);
                            activeMobHashMap.put(mob, p);
                            activeMobHashMapReverse.put(p, mob);
                        } else {
                            EntityType bosstype = slayer.getBoss();
                            e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), bosstype);
                        }
                    }
                }
            }
        }
        if (activeMobHashMap.containsKey(e.getMob())){
            Player pl = activeMobHashMap.get(e.getMob());
            SlayerData slayer = plugin.activeSlayer.get(pl);
            String tier = slayer.getTier().split(":")[0];
            SlayerXp slayerplayer = SlayerSQL.getUser(pl.getUniqueId(), tier);
            if (slayerplayer == null) return;
            slayerplayer.addXp(slayer.getReward());
            SlayerSQL.saveSlayerXp(slayerplayer);
            String bar = slayerplayer.getBar();
            pl.sendMessage("");
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2&lSLAYER DEFEATED!"));
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have gained &e" + slayer.getReward() + " " + slayer.getSlayername() + " &aXP!"));
            if (!slayerplayer.reachedMaxLevel()) {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aProgress to level " + slayerplayer.getNextLevel() + " " + bar + " &e" + slayerplayer.getPercent()));
            } else {
                pl.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lMAX LEVEL &e" + slayerplayer.getPercent()));
            }
            pl.sendMessage("");
            plugin.activeSlayer.remove(pl);
            activeMobHashMap.remove(e.getMob());
            activeMobHashMapReverse.remove(p);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player p = e.getPlayer();
        if (activeMobHashMapReverse.containsKey(p)){
            removeFromActiveMap(p);
        }
    }

    public static void removeFromActiveMap(Player p){
        ActiveMob mob = activeMobHashMapReverse.get(p);
        mob.despawn();
        activeMobHashMap.remove(mob);
        activeMobHashMapReverse.remove(p);
    }
}
