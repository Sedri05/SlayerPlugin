package me.sedri.slayers.Data;

import me.sedri.slayers.Slayers;
import me.sedri.slayers.Slayers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SlayerConfig {

    private static File file;
    private static FileConfiguration customfile;

     public static void setup(){
         file = new File(Slayers.getPlugin().getDataFolder(), "slayers.yml");
         if (!file.exists()){
             try {
                 file.createNewFile();
             } catch (IOException e){
                 Bukkit.getLogger().info("Failed to load Slayers.yml");
             }
         }
         customfile = YamlConfiguration.loadConfiguration(file);

     }

     public static FileConfiguration get(){
         return customfile;
     }

     public static File getFile(){
         return file;
     }

     public static void save(){
         try{
             customfile.save(file);
         } catch (IOException e){
             Bukkit.getLogger().info("Failed to save Slayers.yml");
         }
     }

     public static void reload(){
         customfile = YamlConfiguration.loadConfiguration(file);
     }

     public static void defaults(){

     }
}
