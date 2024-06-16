package net.gamedoctor.graphicalauth;

import lombok.Getter;
import net.gamedoctor.graphicalauth.commands.GraphicalAuthCommand;
import net.gamedoctor.graphicalauth.config.Config;
import net.gamedoctor.graphicalauth.database.DatabaseManager;
import net.gamedoctor.graphicalauth.events.Events;
import net.gamedoctor.graphicalauth.events.GuardEvents;
import net.gamedoctor.graphicalauth.gui.AuthGUI;
import net.gamedoctor.graphicalauth.utils.State;
import net.gamedoctor.graphicalauth.utils.Utils;
import net.gamedoctor.plugins.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Level;

@Getter
public class GraphicalAuth extends JavaPlugin {
    private final HashMap<String, AuthGUI> playersInAuth = new HashMap<>();
    private Utils utils;
    private Config cfg;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Launching the plugin...");
        long start = System.currentTimeMillis();
        this.saveDefaultConfig();
        this.utils = new Utils(this);
        this.getLogger().log(Level.INFO, "Processing the config...");
        cfg = new Config(this);
        this.getLogger().log(Level.INFO, "Completed!");
        this.getLogger().log(Level.INFO, "Connecting to the database...");
        databaseManager = new DatabaseManager(this);
        databaseManager.openConnection();
        this.getLogger().log(Level.INFO, "Completed!");

        this.getLogger().log(Level.INFO, "Performing other processes...");
        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        Bukkit.getPluginManager().registerEvents(new GuardEvents(this), this);

        GraphicalAuthCommand graphicalAuthCommand = new GraphicalAuthCommand(this);
        this.getCommand("graphicalauth").setExecutor(graphicalAuthCommand);
        this.getCommand("graphicalauth").setTabCompleter(graphicalAuthCommand);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (AuthGUI authGUI : playersInAuth.values()) {
                    if ((authGUI.getState().equals(State.LOGIN) || authGUI.getState().equals(State.REGISTER))) {
                        if (authGUI.getOpenTime() + cfg.getMaxAuthTime() * 1000L < System.currentTimeMillis()) {
                            authGUI.getPlayer().kickPlayer(cfg.getKick_timeLeft().replace("%player%", authGUI.getPlayer().getName()));
                        } else {
                            authGUI.updateInfoTimer();
                        }
                    }
                }
            }
        }, 20L, 20L);

        this.getLogger().log(Level.INFO, "Fully completed! (" + (System.currentTimeMillis() - start) + " ms)");

        Metrics.send(this);
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Disabling the plugin...");
        Bukkit.getScheduler().cancelTasks(this);

        playersInAuth.clear();

        if (databaseManager != null) databaseManager.closeConnection();
        this.getLogger().log(Level.INFO, "The plugin has been successfully disabled!");
    }

    public void executeAfterAuth(Player player) {
        if (cfg.getStandaloneServerConfig().isEnable()) {
            utils.connectBungeeCordServer(player, cfg.getStandaloneServerConfig().getServer());
        } else if (cfg.getAfterAuthTP() != null) {
            player.teleport(cfg.getAfterAuthTP());
        }
    }

    public void executePreAuth(Player player) {
        if (cfg.getSpawn() != null) {
            player.teleport(cfg.getSpawn());
        }
    }

    public boolean checkInteract(Player player) {
        return playersInAuth.containsKey(player.getName()) || (cfg.getStandaloneServerConfig().isEnable() && cfg.getStandaloneServerConfig().isAllowToInteract());
    }
}