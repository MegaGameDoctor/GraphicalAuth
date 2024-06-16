package net.gamedoctor.graphicalauth.database;

import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.utils.State;
import org.bukkit.entity.Player;

public interface DBManager {

    void connect(GraphicalAuth plugin);

    void close();

    State getNeededState(Player player);

    void updateOrCreatePlayer(String player, String ip, String hash);

    String getPlayerHash(String player);
}
