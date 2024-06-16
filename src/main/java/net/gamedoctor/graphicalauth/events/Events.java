package net.gamedoctor.graphicalauth.events;

import lombok.RequiredArgsConstructor;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.gui.AuthGUI;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class Events implements Listener {
    private final GraphicalAuth plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                State state = plugin.getDatabaseManager().getNeededState(player);
                if (!state.equals(State.COMPLETED)) {
                    plugin.executePreAuth(player);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            plugin.getPlayersInAuth().put(player.getName(), new AuthGUI(plugin, player, state));
                        }
                    }, 5L);
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getPlayersInAuth().remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        String name = player.getName();
        if (plugin.getPlayersInAuth().containsKey(name) && plugin.getPlayersInAuth().get(name).getInventory().equals(e.getInventory())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    if (plugin.getPlayersInAuth().containsKey(name)) plugin.getPlayersInAuth().get(name).reOpen();
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String name = player.getName();
        if (plugin.getPlayersInAuth().containsKey(name) && plugin.getPlayersInAuth().get(name).getInventory().equals(e.getClickedInventory())) {
            plugin.getPlayersInAuth().get(name).click(e.getSlot());
            e.setCancelled(true);
        }
    }
}