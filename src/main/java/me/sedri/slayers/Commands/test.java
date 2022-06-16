package me.sedri.slayers.Commands;

import jdk.jfr.StackTrace;
import me.sedri.slayers.Data.SlayerSQL;
import me.sedri.slayers.Data.SlayerXp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class test implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        if (args[0].equals("get")) {
            try {
                System.out.println(SlayerSQL.getUser(p.getUniqueId(), "zombie_slayer"));
            } catch (SQLException e) {
                p.sendMessage("Something went wrong");
                e.printStackTrace();
            }
        } else if (args[0].equals("set")){
            try {
                SlayerSQL.saveSlayerXp(new SlayerXp(p.getUniqueId(), "zombie_slayer"));
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
