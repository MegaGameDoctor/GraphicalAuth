package net.gamedoctor.graphicalauth.config.other;

import lombok.Getter;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class StandaloneServerConfig {
    private final boolean enable;
    private final String server;
    private final boolean allowToInteract;

    public StandaloneServerConfig(GraphicalAuth plugin) {
        String path = "standaloneServer.";
        FileConfiguration cfg = plugin.getConfig();

        enable = cfg.getBoolean(path + "enable", false);
        server = cfg.getString(path + "server", "-");
        allowToInteract = cfg.getBoolean(path + "allowToInteract", false);
    }
}
