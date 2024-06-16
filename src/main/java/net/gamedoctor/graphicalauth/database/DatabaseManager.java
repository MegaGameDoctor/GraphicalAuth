package net.gamedoctor.graphicalauth.database;

import lombok.Getter;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.database.managers.FileDBManager;
import net.gamedoctor.graphicalauth.database.managers.MySQLDBManager;
import net.gamedoctor.graphicalauth.database.managers.SQLiteDBManager;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DatabaseManager {
    private final GraphicalAuth plugin;
    @Getter
    private final String dbFileName;
    @Getter
    private final String playersTableName;
    @Getter
    private DBManager dbManager;

    public DatabaseManager(GraphicalAuth plugin) {
        this.plugin = plugin;
        FileConfiguration cfg = plugin.getConfig();
        this.dbFileName = cfg.getString("database.fileName");
        this.playersTableName = cfg.getString("database.playersTableName");
        switch (DBType.valueOf(cfg.getString("database.type"))) {
            case FILE: {
                dbManager = new FileDBManager();
                break;
            }
            case MYSQL: {
                dbManager = new MySQLDBManager();
                break;
            }
            case SQLITE: {
                dbManager = new SQLiteDBManager();
                break;
            }
        }
    }

    public void openConnection() {
        dbManager.connect(plugin);
    }

    public void updateOrCreatePlayerAsync(String player, String ip, String hash) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                dbManager.updateOrCreatePlayer(player, ip, hash);
            }
        });
    }

    public void closeConnection() {
        dbManager.close();
    }

    public State getNeededState(Player player) {
        State state = dbManager.getNeededState(player);
        if (state.equals(State.COMPLETED) && !plugin.getCfg().isAuthOnlyOnIPChange()) {
            return State.LOGIN;
        } else {
            return state;
        }
    }
}