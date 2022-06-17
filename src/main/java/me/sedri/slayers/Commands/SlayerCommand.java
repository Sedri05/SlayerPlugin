package me.sedri.slayers.Commands;

import me.sedri.slayers.Data.SlayerSQL;
import me.sedri.slayers.Data.SlayerXp;
import me.sedri.slayers.Gui.MainSlayerGui;
import me.sedri.slayers.Slayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayerCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        MainSlayerGui inv;
        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            inv = new MainSlayerGui(p);
            inv.openInventory();
            return true;
        }
        if (!p.hasPermission("sedri.editslayer")){
            return false;
        }
        switch (args[0]) {
            case "level" -> {
                if (!(args.length >= 4)){
                    sender.sendMessage(ChatColor.RED + "Invallid command");
                    return false;
                }
                if (args.length == 5) {
                    Player p2 = Bukkit.getPlayer(args[4]);
                    if (p2 != null) {
                        p = p2;
                    }
                }
                SlayerXp xp = SlayerSQL.getUser(p.getUniqueId(), args[2]);
                if (xp == null) return false;
                for (int i = 0; i < Integer.parseInt(args[3]); i++) {
                    if (args[1].equalsIgnoreCase("add")) {
                        xp.incrementLevel();
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        xp.decrementLevel();
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid command");
                    }
                }
                SlayerSQL.saveSlayerXp(xp);
            }
            case "xp" -> {
                if (!(args.length >= 4)){
                    sender.sendMessage(ChatColor.RED + "Invalid command");
                    return false;
                }
                if (args.length == 5) {
                    Player p2 = Bukkit.getPlayer(args[4]);
                    if (p2 != null) {
                        p = p2;
                    }
                }
                SlayerXp xp = SlayerSQL.getUser(p.getUniqueId(), args[2]);
                if (xp == null) return false;
                if (args[1].equalsIgnoreCase("add")) {
                    xp.addXp(Integer.parseInt(args[3]));
                } else if (args[1].equalsIgnoreCase("remove")) {
                    xp.removeXp(Integer.parseInt(args[3]));
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid command");
                }
                SlayerSQL.saveSlayerXp(xp);
            }
        }
        return false;
    }

    private List<String> getOnlinePlayerNames(){
        List<String> list = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> ls = new ArrayList<>(List.of("open"));
        if (sender.hasPermission("sedri.editslayer")){
            if (args[0].equalsIgnoreCase("level") || args[0].equalsIgnoreCase("xp")) {
                if (args.length == 2) {
                    return Arrays.asList("add", "remove");
                } else if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    return Slayers.slayerkeys;
                } else if (args.length == 4) {
                    return List.of("<int>");
                } else if (args.length == 5) {
                    return getOnlinePlayerNames();
                }
                return List.of();
            }
            ls.add("level");
            ls.add("xp");
        } else if (args[0].equalsIgnoreCase("open")){
            return List.of();
        }
        return ls;

    }
}
