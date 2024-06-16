package net.gamedoctor.graphicalauth.database.managers;

import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.database.DBManager;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class FileDBManager implements DBManager {
    private GraphicalAuth plugin;
    private File file;
    private FileConfiguration db;
    private String playersTableName;

    public void connect(GraphicalAuth plugin) {
        this.plugin = plugin;
        playersTableName = plugin.getDatabaseManager().getPlayersTableName();

        file = new File(plugin.getDataFolder(), plugin.getDatabaseManager().getDbFileName() + ".yml");
        db = YamlConfiguration.loadConfiguration(file);

    }

    private void makeSectionEmpty(String section) {
        if (db.isConfigurationSection(section)) {
            for (String value : db.getConfigurationSection(section).getKeys(false)) {
                db.set(section + "." + value, null);
            }
            saveFileSync();
        }
    }

    public void close() {
    }

    public State getNeededState(Player player) {
        String path = playersTableName + "." + player.getName();
        if (db.isConfigurationSection(path)) {
            String address = "-";

            try {
                address = player.getAddress().getAddress().getHostAddress();
            } catch (Exception ignored) {
            }

            if (db.getString(path + ".hash").equals("-")) {
                return State.REGISTER;
            } else if (address.equals(db.getString(path + ".ip", "--"))) {
                return State.COMPLETED;
            } else {
                return State.LOGIN;
            }
        } else {
            return State.REGISTER;
        }
    }

    public void updateOrCreatePlayer(String player, String ip, String hash) {
        String path = playersTableName + "." + player;
        db.set(path + ".ip", ip);
        db.set(path + ".hash", hash);
        saveFileSync();
    }

    public String getPlayerHash(String player) {
        String path = playersTableName + "." + player;
        return db.getString(path + ".hash", "-");
    }

    public void saveFile() {
        try {
            db.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFileSync() {
        new BukkitRunnable() {
            public void run() {
                try {
                    db.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTask(plugin);
    }
}