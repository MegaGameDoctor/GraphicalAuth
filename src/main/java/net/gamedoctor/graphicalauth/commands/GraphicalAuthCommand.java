package net.gamedoctor.graphicalauth.commands;

import lombok.RequiredArgsConstructor;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.config.Config;
import net.gamedoctor.graphicalauth.config.messages.Placeholder;
import net.gamedoctor.graphicalauth.gui.AuthGUI;
import net.gamedoctor.graphicalauth.utils.State;
import net.gamedoctor.graphicalauth.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class GraphicalAuthCommand implements CommandExecutor, TabExecutor {
    private final GraphicalAuth plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        Config cfg = plugin.getCfg();
        Utils utils = plugin.getUtils();
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "change":
                    if (cfg.getCommand_change_usePermission().equals("-") || commandSender.hasPermission(cfg.getCommand_change_usePermission())) {
                        if (commandSender instanceof Player player) {
                            plugin.getPlayersInAuth().put(player.getName(), new AuthGUI(plugin, player, State.CHANGING));
                        }
                    } else {
                        cfg.getMessage_cmdNoPerm().display(commandSender);
                    }
                    return true;
                case "unregister":
                    if (cfg.getCommand_unregister_usePermission().equals("-") || commandSender.hasPermission(cfg.getCommand_unregister_usePermission())) {
                        if (commandSender instanceof Player player) {
                            plugin.getDatabaseManager().updateOrCreatePlayerAsync(player.getName(), "-", "-");
                            plugin.getPlayersInAuth().put(player.getName(), new AuthGUI(plugin, player, State.REGISTER));
                        }
                    } else {
                        cfg.getMessage_cmdNoPerm().display(commandSender);
                    }
                    return true;
                case "admin":
                    if (commandSender.hasPermission(cfg.getCommand_admin_usePermission()) || cfg.getCommand_admin_usePermission().equals("-")) {
                        if (args.length > 2) {
                            String name = args[2];
                            Player target = Bukkit.getPlayer(name);
                            switch (args[1].toLowerCase()) {
                                case "unregister":
                                    if (target != null && target.isOnline()) {
                                        plugin.getPlayersInAuth().remove(target.getName());
                                        plugin.getPlayersInAuth().put(target.getName(), new AuthGUI(plugin, target, State.REGISTER));
                                    }

                                    plugin.getDatabaseManager().updateOrCreatePlayerAsync(name, "-", "-");

                                    cfg.getMessage_cmdSuccess().display(commandSender);
                                    return true;
                                case "unlogin":
                                    if (target != null && target.isOnline()) {
                                        plugin.getPlayersInAuth().remove(target.getName());
                                        plugin.getPlayersInAuth().put(target.getName(), new AuthGUI(plugin, target, State.LOGIN));
                                    }
                                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            plugin.getDatabaseManager().updateOrCreatePlayerAsync(name, "-", plugin.getDatabaseManager().getDbManager().getPlayerHash(name));
                                        }
                                    });

                                    cfg.getMessage_cmdSuccess().display(commandSender);
                                    return true;
                                default:
                                    cfg.getMessage_cmdAdminHelp().display(commandSender, new Placeholder("%cmd%", s));
                                    return true;
                            }
                        } else {
                            cfg.getMessage_cmdAdminHelp().display(commandSender, new Placeholder("%cmd%", s));
                        }
                    } else {
                        cfg.getMessage_cmdNoPerm().display(commandSender);
                    }
                    return true;
                default:
                    cfg.getMessage_cmdHelp().display(commandSender, new Placeholder("%cmd%", s));
            }
        } else {
            cfg.getMessage_cmdHelp().display(commandSender, new Placeholder("%cmd%", s));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String ss, @NotNull String[] args) {
        List<String> list = Arrays.asList("unregister", "change", "admin");
        String input = args[0].toLowerCase();
        Config cfg = plugin.getCfg();
        if (args.length > 1) {
            input = args[1].toLowerCase();
            if (args[0].equalsIgnoreCase("admin") && (commandSender.hasPermission(cfg.getCommand_admin_usePermission()) || cfg.getCommand_admin_usePermission().equals("-"))) {
                list = Arrays.asList("unregister", "unlogin");
            } else {
                list = new ArrayList<>();
            }
        }

        if (args.length > 2) {
            input = args[2].toLowerCase();
            list = new ArrayList<>();
        }

        List<String> completions = null;
        for (String s : list) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }

        if (completions != null)
            Collections.sort(completions);

        return completions;
    }
}