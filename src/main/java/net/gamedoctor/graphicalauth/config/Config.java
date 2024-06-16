package net.gamedoctor.graphicalauth.config;

import lombok.Getter;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.config.messages.Message;
import net.gamedoctor.graphicalauth.config.messages.Placeholder;
import net.gamedoctor.graphicalauth.config.other.Item;
import net.gamedoctor.graphicalauth.config.other.StandaloneServerConfig;
import net.gamedoctor.graphicalauth.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Config {
    private final Item gui_checked_item;
    private final Item gui_unchecked_item;
    private final Item gui_footer;
    private final Item gui_loading;
    private final Item gui_info_register;
    private final Item gui_info_login;
    private final Item gui_apply_register;
    private final Item gui_apply_login;
    private final Item gui_exit;
    private final Item gui_apply_fail_passwordLength;
    private final Item gui_apply_change;
    private final Item gui_info_changing;

    private final String gui_title_login;
    private final String gui_title_register;
    private final String gui_title_changing;

    private final String command_change_usePermission;
    private final String command_unregister_usePermission;
    private final String command_admin_usePermission;

    private final String kick_incorrectPassword;
    private final String kick_exit;
    private final String kick_timeLeft;

    private final int minPswdLength;
    private final int maxPswdLength;
    private final boolean authOnlyOnIPChange;
    private final int maxAuthTime;

    private final Message message_success_auth;
    private final Message message_success_register;
    private final Message message_success_changed;
    private final Message message_cmdNoPerm;
    private final Message message_cmdHelp;
    private final Message message_cmdAdminHelp;
    private final Message message_cmdSuccess;

    private final StandaloneServerConfig standaloneServerConfig;
    private final Location spawn;
    private final Location afterAuthTP;
    private final boolean timeStringFormat_displayOnlyHighest;
    private final String timeStringFormat_seconds;
    private final String timeStringFormat_minutes;
    private final String timeStringFormat_hours;
    private final String timeStringFormat_days;

    private final boolean usingPlaceholderAPI;

    public Config(GraphicalAuth plugin) {
        FileConfiguration cfg = plugin.getConfig();
        Utils utils = plugin.getUtils();

        minPswdLength = cfg.getInt("settings.minPswdLength");
        maxPswdLength = cfg.getInt("settings.maxPswdLength");
        authOnlyOnIPChange = cfg.getBoolean("settings.authOnlyOnIPChange");
        maxAuthTime = cfg.getInt("settings.maxAuthTime");

        gui_checked_item = new Item(plugin, "gui.checked_item");
        gui_unchecked_item = new Item(plugin, "gui.unchecked_item");
        gui_footer = new Item(plugin, "gui.footer");
        gui_loading = new Item(plugin, "gui.loading");
        gui_info_register = new Item(plugin, "gui.info_register");
        gui_info_login = new Item(plugin, "gui.info_login");
        gui_apply_register = new Item(plugin, "gui.apply_register");
        gui_apply_login = new Item(plugin, "gui.apply_login");
        gui_exit = new Item(plugin, "gui.exit");
        gui_apply_fail_passwordLength = new Item(plugin, "gui.apply_fail_passwordLength", new Placeholder("%min%", String.valueOf(minPswdLength)), new Placeholder("%max%", String.valueOf(maxPswdLength)));
        gui_info_changing = new Item(plugin, "gui.info_changing");
        gui_apply_change = new Item(plugin, "gui.apply_change");

        gui_title_login = utils.color(cfg.getString("gui.title_login"));
        gui_title_register = utils.color(cfg.getString("gui.title_register"));
        gui_title_changing = utils.color(cfg.getString("gui.title_changing"));

        command_change_usePermission = cfg.getString("command.change.usePermission");
        command_unregister_usePermission = cfg.getString("command.unregister.usePermission");
        command_admin_usePermission = cfg.getString("command.admin.usePermission");

        StringBuilder kick_incorrectPassword = new StringBuilder();
        for (String line : cfg.getStringList("kick.incorrectPassword")) {
            kick_incorrectPassword.append(utils.color(line)).append("\n");
        }
        this.kick_incorrectPassword = kick_incorrectPassword.toString();

        StringBuilder kick_exit = new StringBuilder();
        for (String line : cfg.getStringList("kick.exit")) {
            kick_exit.append(utils.color(line)).append("\n");
        }
        this.kick_exit = kick_exit.toString();

        StringBuilder kick_timeLeft = new StringBuilder();
        for (String line : cfg.getStringList("kick.timeLeft")) {
            kick_timeLeft.append(utils.color(line)).append("\n");
        }
        this.kick_timeLeft = kick_timeLeft.toString();

        message_success_auth = new Message(plugin, "success_auth");
        message_success_register = new Message(plugin, "success_register");
        message_success_changed = new Message(plugin, "success_changed");
        message_cmdHelp = new Message(plugin, "cmdHelp");
        message_cmdNoPerm = new Message(plugin, "cmdNoPerm");
        message_cmdAdminHelp = new Message(plugin, "cmdAdminHelp");
        message_cmdSuccess = new Message(plugin, "cmdSuccess");

        standaloneServerConfig = new StandaloneServerConfig(plugin);

        if (cfg.getBoolean("settings.spawn.enable", false)) {
            spawn = new Location(Bukkit.getWorld(cfg.getString("settings.spawn.world")), cfg.getDouble("settings.spawn.x"), cfg.getDouble("settings.spawn.y"), cfg.getDouble("settings.spawn.z"), Float.parseFloat(cfg.getString("settings.spawn.yaw", "0")), Float.parseFloat(cfg.getString("settings.spawn.pitch", "0")));
        } else {
            spawn = null;
        }

        if (cfg.getBoolean("settings.afterAuthTP.enable", false)) {
            afterAuthTP = new Location(Bukkit.getWorld(cfg.getString("settings.afterAuthTP.world")), cfg.getDouble("settings.afterAuthTP.x"), cfg.getDouble("settings.afterAuthTP.y"), cfg.getDouble("settings.afterAuthTP.z"), Float.parseFloat(cfg.getString("settings.afterAuthTP.yaw", "0")), Float.parseFloat(cfg.getString("settings.afterAuthTP.pitch", "0")));
        } else {
            afterAuthTP = null;
        }

        timeStringFormat_displayOnlyHighest = cfg.getBoolean("settings.timeStringFormat.displayOnlyHighest", true);
        timeStringFormat_seconds = cfg.getString("settings.timeStringFormat.seconds");
        timeStringFormat_minutes = cfg.getString("settings.timeStringFormat.minutes");
        timeStringFormat_hours = cfg.getString("settings.timeStringFormat.hours");
        timeStringFormat_days = cfg.getString("settings.timeStringFormat.days");

        usingPlaceholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
