package me.sedri.slayers.Data;

import me.sedri.slayers.Slayers;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class SlayerSQL {
    private final Slayers plugin = Slayers.getPlugin();
    private static final String url = "jdbc:sqlite:plugins/Slayers/slayers.db";

    public static Connection conn;

    public static Connection getConn() {
        return conn;
    }

    public static void initDatabase() throws SQLException {
        newConnection();
        for (String key: Slayers.slayerkeys) {
            try (Statement statement = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS "+key+"(UUID TEXT, SLAYER TEXT, XP INT, LEVEL INT)";
                statement.execute(sql);
            }
        }
    }

    public static void newConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(url);
        if (conn == null) {
            Slayers.getPlugin().getLogger().severe("SQL database offline");
            throw new SQLException("Database Offline");
        }
        SlayerSQL.conn = conn;
    }

    public static SlayerXp getUser(UUID uuid, String slayer){
        String stmt = String.format("SELECT * FROM %s WHERE UUID=?", slayer);
        try {
            PreparedStatement statement = conn.prepareStatement(stmt);
            try (statement) {
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return new SlayerXp(uuid, slayer, set.getInt("XP"), set.getInt("LEVEL"));
                }
                return new SlayerXp(uuid, slayer);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static SlayerXp getUser(Player p, String slayer){
        String stmt = String.format("SELECT * FROM %s WHERE UUID=?", slayer);
        UUID uuid = p.getUniqueId();
        try {
            PreparedStatement statement = conn.prepareStatement(stmt);
            try (statement) {
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                if (set.next()) {
                    return new SlayerXp(uuid, slayer, set.getInt("XP"), set.getInt("LEVEL"));
                }
                return new SlayerXp(uuid, slayer);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public static void saveSlayerXp(SlayerXp slayerxp){
        String stmt = String.format("SELECT * FROM %s WHERE UUID=?", slayerxp.getSlayer());
        try {
            PreparedStatement statement = conn.prepareStatement(stmt);
            statement.setString(1, slayerxp.getUuid().toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement.close();
                stmt = String.format("UPDATE %s SET UUID=?, SLAYER=?, XP=?, LEVEL=?", slayerxp.getSlayer());
                statement = conn.prepareStatement(stmt);
                statement.setString(1, slayerxp.getUuid().toString());
                statement.setString(2, slayerxp.getSlayer());
                statement.setInt(3, (int) slayerxp.getXp());
                statement.setInt(4, (int) slayerxp.getLevel());
                statement.execute();
                statement.close();
            } else {
                statement.close();
                stmt = String.format("INSERT INTO %s VALUES( ?, ?, ?, ?)", slayerxp.getSlayer());
                statement = conn.prepareStatement(stmt);
                statement.setString(1, slayerxp.getUuid().toString());
                statement.setString(2, slayerxp.getSlayer());
                statement.setInt(3, (int) slayerxp.getXp());
                statement.setInt(4, (int) slayerxp.getLevel());
                statement.execute();
                statement.close();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
