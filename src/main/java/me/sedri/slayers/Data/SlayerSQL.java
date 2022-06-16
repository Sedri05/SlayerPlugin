package me.sedri.slayers.Data;

import me.sedri.slayers.Slayers;

import java.sql.*;
import java.util.UUID;

public class SlayerSQL {
    private final Slayers plugin = Slayers.getPlugin();
    private static final String url = "jdbc:sqlite:plugins/Slayers/slayers.db";

    public static Connection newConnection(){
        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn == null) {
                Slayers.getPlugin().getLogger().severe("SQL database offline");
                return null;
            }
            return conn;
        } catch (SQLException e){
            return null;
        }
    }

    public static SlayerXp getUser(UUID uuid, String slayer) throws SQLException{
        Connection conn = newConnection();
        if (conn == null) return null;
        String stmt = String.format("SELECT * FROM %s WHERE UUID=?", slayer);
        PreparedStatement statement = conn.prepareStatement(stmt);
        statement.setString(1, uuid.toString());
        ResultSet set = statement.executeQuery();
        try {
            if (set.next()) {
                return new SlayerXp(uuid, slayer, set.getInt("XP"), set.getInt("LEVEL"));
            }
            return new SlayerXp(uuid, slayer);
        } finally {
            statement.close();
            conn.close();
        }
    }

    public static void saveSlayerXp(SlayerXp slayerxp) throws SQLException{
        Connection conn = newConnection();
        if (conn == null) return;
        String stmt = String.format("SELECT * FROM %s WHERE UUID=?", slayerxp.getSlayer());
        PreparedStatement statement = conn.prepareStatement(stmt);
        statement.setString(1, slayerxp.getUuid().toString());
        ResultSet set = statement.executeQuery();
        if (set.next()){
            statement.close();
            stmt = String.format("UPDATE %s SET UUID=?, SLAYER=?, XP=?, LEVEL=?", slayerxp.getSlayer());
            statement = conn.prepareStatement(stmt);
            statement.setString(1, slayerxp.getUuid().toString());
            statement.setString(2, slayerxp.getSlayer());
            statement.setInt(3, (int) slayerxp.getXp());
            statement.setInt(4, (int) slayerxp.getLevel());
            statement.execute();
            statement.close();
            conn.close();
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
            conn.close();
        }
    }
}
