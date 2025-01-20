package net.gamedoctor.graphicalauth.events;

import lombok.RequiredArgsConstructor;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

@RequiredArgsConstructor
public class GuardEvents implements Listener {
    private final GraphicalAuth plugin;

    @EventHandler
    public void onPick(PlayerPickupItemEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (plugin.checkInteract(p))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (plugin.checkInteract(p))
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageOther(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            if (plugin.checkInteract(p))
                e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onDisableCommands(PlayerCommandPreprocessEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (plugin.checkInteract((Player) e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onChat(AsyncPlayerChatEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (plugin.checkInteract(e.getPlayer()))
            e.setCancelled(true);
    }
}
