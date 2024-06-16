package net.gamedoctor.graphicalauth.database.managers;

import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.database.DBManager;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.entity.Player;

import java.sql.*;

public class SQLiteDBManager implements DBManager {
    private GraphicalAuth plugin;
    private Connection connection;
    private String playersTableName;

    public void connect(GraphicalAuth plugin) {
        this.plugin = plugin;
        playersTableName = plugin.getDatabaseManager().getPlayersTableName();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite://" + plugin.getDataFolder().getAbsolutePath() + "//" + plugin.getDatabaseManager().getDbFileName() + ".db");
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + playersTableName + " (\n" +
                    "\t`player`\tTEXT(255) UNIQUE,\n" +
                    "\t`ip`\tTEXT(255),\n" +
                    "\t`hash`\tTEXT(255));").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public State getNeededState(Player player) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + playersTableName + " WHERE player=?");
            preparedStatement.setString(1, player.getName());
            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                String address = "-";

                try {
                    address = player.getAddress().getAddress().getHostAddress();
                } catch (Exception ignored) {
                }

                if (set.getString("hash").equals("-")) {
                    return State.REGISTER;
                } else if (address.equals(set.getString("ip"))) {
                    return State.COMPLETED;
                } else {
                    return State.LOGIN;
                }
            } else {
                return State.REGISTER;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateOrCreatePlayer(String player, String ip, String hash) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + playersTableName + " (`player`, `ip`, `hash`) VALUES (?, ?, ?) " +
                    "ON CONFLICT(player) DO UPDATE SET ip=excluded.ip, hash=excluded.hash WHERE player=excluded.player");
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, ip);
            preparedStatement.setString(3, hash);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerHash(String player) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + playersTableName + " WHERE player=?");
            preparedStatement.setString(1, player);
            ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
                return set.getString("hash");
            } else {
                return "-";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}