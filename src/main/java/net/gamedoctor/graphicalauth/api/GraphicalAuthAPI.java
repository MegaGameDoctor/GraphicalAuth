package net.gamedoctor.graphicalauth.api;

import lombok.RequiredArgsConstructor;
import net.gamedoctor.graphicalauth.GraphicalAuth;

@RequiredArgsConstructor
public class GraphicalAuthAPI {
    private final GraphicalAuth plugin;

    public boolean isPlayerInAuth(String playerName) {
        return plugin.getPlayersInAuth().containsKey(playerName);
    }
}
