package net.gamedoctor.graphicalauth.gui;

import lombok.Getter;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.api.callEvents.PlayerAuthEvent;
import net.gamedoctor.graphicalauth.api.enums.AuthState;
import net.gamedoctor.graphicalauth.config.Config;
import net.gamedoctor.graphicalauth.config.other.Item;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AuthGUI {
    private final GraphicalAuth plugin;
    @Getter
    private final Player player;
    @Getter
    private final Inventory inventory;
    @Getter
    private final long openTime;
    private final List<Integer> attachedSlots = new ArrayList<>();
    @Getter
    private State state;

    public AuthGUI(GraphicalAuth plugin, Player player, State state) {
        this.plugin = plugin;
        this.player = player;
        this.state = state;
        this.openTime = System.currentTimeMillis();
        Config cfg = plugin.getCfg();

        String title = "NONE";
        if (state.equals(State.REGISTER)) {
            title = cfg.getGui_title_register();
        } else if (state.equals(State.LOGIN)) {
            title = cfg.getGui_title_login();
        } else if (state.equals(State.CHANGING)) {
            title = cfg.getGui_title_changing();
        }

        inventory = Bukkit.getServer().createInventory(null, 54, title);

        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, cfg.getGui_unchecked_item().getItemStack());
        }

        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, cfg.getGui_footer().getItemStack());
        }

        if (state.equals(State.LOGIN)) {
            inventory.setItem(cfg.getGui_info_login().getSlot(), cfg.getGui_info_login().getItemStack());
        } else if (state.equals(State.REGISTER)) {
            inventory.setItem(cfg.getGui_info_register().getSlot(), cfg.getGui_info_register().getItemStack());
        } else if (state.equals(State.CHANGING)) {
            inventory.setItem(cfg.getGui_info_changing().getSlot(), cfg.getGui_info_changing().getItemStack());
        }

        setUpFailedApplyItem(cfg.getGui_apply_fail_passwordLength());

        inventory.setItem(cfg.getGui_exit().getSlot(), cfg.getGui_exit().getItemStack());

        reOpen();
        updateInfoTimer();
    }

    private void setUpFailedApplyItem(Item item) {
        inventory.setItem(49, item.getItemStack());
    }

    private void setUpApplyItems() {
        Config cfg = plugin.getCfg();
        if (state.equals(State.LOGIN)) {
            inventory.setItem(49, cfg.getGui_apply_login().getItemStack());
        } else if (state.equals(State.REGISTER)) {
            inventory.setItem(49, cfg.getGui_apply_register().getItemStack());
        } else if (state.equals(State.CHANGING)) {
            inventory.setItem(49, cfg.getGui_apply_change().getItemStack());
        }
    }

    public void reOpen() {
        player.openInventory(inventory);
    }

    public void updateInfoTimer() {
        if (player.getOpenInventory().getTopInventory().equals(inventory)) {
            Config cfg = plugin.getCfg();
            LinkedList<String> originalLore = new LinkedList<>();
            int slot = 0;
            if (state.equals(State.REGISTER)) {
                originalLore = cfg.getGui_info_register().getLore();
                slot = cfg.getGui_info_register().getSlot();
            } else if (state.equals(State.LOGIN)) {
                originalLore = cfg.getGui_info_login().getLore();
                slot = cfg.getGui_info_login().getSlot();
            }

            LinkedList<String> lore = new LinkedList<>();
            for (String line : originalLore) {
                lore.add(plugin.getUtils().color(line.replace("%timeLeft%", plugin.getUtils().getTimeString(openTime + cfg.getMaxAuthTime() * 1000L - System.currentTimeMillis()))));
            }

            if (state.equals(State.REGISTER)) {
                inventory.setItem(slot, cfg.getGui_info_register().recreateItemStack(plugin, lore));
            } else if (state.equals(State.LOGIN)) {
                inventory.setItem(slot, cfg.getGui_info_register().recreateItemStack(plugin, lore));
            }

            player.updateInventory();
        }
    }

    public void click(int slot) {
        Config cfg = plugin.getCfg();
        ItemStack item = inventory.getItem(slot);
        if (!state.equals(State.LOADING) && item != null) {
            if (slot < 45) {
                if (attachedSlots.contains(slot)) {
                    attachedSlots.remove((Object) slot);
                    inventory.setItem(slot, cfg.getGui_unchecked_item().getItemStack());
                } else {
                    attachedSlots.add(slot);
                    inventory.setItem(slot, cfg.getGui_checked_item().getItemStack());
                }

                if (getPasswordLength() > cfg.getMaxPswdLength() || getPasswordLength() < cfg.getMinPswdLength()) {
                    setUpFailedApplyItem(cfg.getGui_apply_fail_passwordLength());
                } else {
                    setUpApplyItems();
                }

                player.updateInventory();
            } else if (slot == 49 && (item.equals(cfg.getGui_apply_login().getItemStack()) || item.equals(cfg.getGui_apply_register().getItemStack()) || item.equals(cfg.getGui_apply_change().getItemStack()))) {
                changeToLoading();
                State originalState = state;
                state = State.LOADING;
                String playerName = player.getName();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (originalState.equals(State.LOGIN)) {
                            if (checkPassword()) {
                                plugin.getDatabaseManager().updateOrCreatePlayerAsync(player.getName(), player.getAddress().getAddress().getHostAddress(), getHashedSlots(true));
                                cfg.getMessage_success_auth().display(player);
                                plugin.executeAfterAuth(player);
                                new BukkitRunnable() {
                                    public void run() {
                                        Bukkit.getPluginManager().callEvent(new PlayerAuthEvent(player, AuthState.LOGIN_SUCCESS));
                                    }
                                }.runTask(plugin);
                            } else {
                                new BukkitRunnable() {
                                    public void run() {
                                        player.kickPlayer(cfg.getKick_incorrectPassword().replace("%player%", player.getName()));
                                        Bukkit.getPluginManager().callEvent(new PlayerAuthEvent(player, AuthState.LOGIN_FAILED));
                                    }
                                }.runTask(plugin);
                            }

                            plugin.getPlayersInAuth().remove(playerName);
                            new BukkitRunnable() {
                                public void run() {
                                    player.closeInventory();
                                }
                            }.runTask(plugin);
                        } else if (originalState.equals(State.REGISTER)) {
                            plugin.getDatabaseManager().updateOrCreatePlayerAsync(player.getName(), player.getAddress().getAddress().getHostAddress(), getHashedSlots(true));
                            plugin.getPlayersInAuth().remove(player.getName());
                            cfg.getMessage_success_register().display(player);
                            plugin.executeAfterAuth(player);
                            new BukkitRunnable() {
                                public void run() {
                                    player.closeInventory();
                                    Bukkit.getPluginManager().callEvent(new PlayerAuthEvent(player, AuthState.REGISTERED));
                                }
                            }.runTask(plugin);
                        } else if (originalState.equals(State.CHANGING)) {
                            plugin.getDatabaseManager().updateOrCreatePlayerAsync(player.getName(), player.getAddress().getAddress().getHostAddress(), getHashedSlots(true));
                            plugin.getPlayersInAuth().remove(player.getName());
                            cfg.getMessage_success_changed().display(player);
                            new BukkitRunnable() {
                                public void run() {
                                    player.closeInventory();
                                    Bukkit.getPluginManager().callEvent(new PlayerAuthEvent(player, AuthState.CHANGED));
                                }
                            }.runTask(plugin);
                        }
                    }
                });
            } else if (slot == plugin.getCfg().getGui_exit().getSlot()) {
                if (state.equals(State.CHANGING)) {
                    plugin.getPlayersInAuth().remove(player.getName()).player.closeInventory();
                } else {
                    player.kickPlayer(cfg.getKick_exit().replace("%player%", player.getName()));
                }

                Bukkit.getPluginManager().callEvent(new PlayerAuthEvent(player, AuthState.CANCELLED));
            }
        }
    }

    private void changeToLoading() {
        //inventory.clear();
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, plugin.getCfg().getGui_footer().getItemStack());
        }
        inventory.setItem(49, plugin.getCfg().getGui_loading().getItemStack());
        player.updateInventory();
    }

    private String getHashedSlots(boolean encode) {
        Collections.sort(attachedSlots);

        StringBuilder builder = new StringBuilder();
        for (Integer attachedSlot : attachedSlots) {
            builder.append(attachedSlot).append(",");
        }

        if (encode) {
            return plugin.getUtils().hash(builder.toString());
        } else {
            return builder.toString();
        }
    }

    private boolean checkPassword() {
        return plugin.getUtils().isHashValid(plugin.getDatabaseManager().getDbManager().getPlayerHash(player.getName()), getHashedSlots(false));
    }

    public int getPasswordLength() {
        return attachedSlots.size();
    }
}