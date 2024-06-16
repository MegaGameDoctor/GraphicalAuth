package net.gamedoctor.graphicalauth.api.callEvents;

import lombok.Getter;
import net.gamedoctor.graphicalauth.api.enums.AuthState;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerAuthEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AuthState authState;

    public PlayerAuthEvent(Player who, AuthState authState) {
        super(who);
        this.player = who;
        this.authState = authState;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}