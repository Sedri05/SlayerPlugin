package me.sedri.slayers.Listeners;

import me.sedri.slayers.Data.SlayerData;
import me.sedri.slayers.Data.SlayerXp;
import me.sedri.slayers.Data.SlayerXpStorage;
import me.sedri.slayers.Slayers;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerInteractListener implements Listener {

    private final Slayers plugin = Slayers.getPlugin();

    @EventHandler
    public void playerKillEvent(EntityDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p == null) return;
        if (plugin.activeSlayer.containsKey(p)) {
            SlayerData slayer = plugin.activeSlayer.get(p);
            if (!slayer.isBossSpawned()) {
                Integer xp = slayer.getMobs().get(e.getEntity().getType());
                if (xp != null) {
                    slayer.addXp(xp);
                }
                if (slayer.reachedMaxXp()) {
                    slayer.setBossSpawned(true);
                    EntityType bosstype = slayer.getBoss();
                    p.sendMessage("");
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour " + slayer.getName() + " &cis spawning!"));
                    p.sendMessage("");
                    e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), bosstype);
                }
            } else {
                if (e.getEntity().getType().equals(slayer.getBoss())) {
                    String tier = slayer.getTier().split(":")[0];
                    SlayerXp slayerplayer = SlayerXpStorage.createPlayer(p, tier);
                    slayerplayer.addXp(slayer.getReward());
                    SlayerXpStorage.updatePlayerSlayerXp(slayerplayer);
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
}
