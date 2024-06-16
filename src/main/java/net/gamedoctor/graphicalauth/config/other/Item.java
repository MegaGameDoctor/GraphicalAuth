package net.gamedoctor.graphicalauth.config.other;

import lombok.Getter;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.config.messages.Placeholder;
import net.gamedoctor.graphicalauth.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

@Getter
public class Item {
    private final String name;
    private final boolean glowing;
    private final LinkedList<String> lore = new LinkedList<>();
    private final Material material;
    private final ItemStack itemStack;
    private final int slot;

    public Item(GraphicalAuth plugin, String path, Placeholder... lorePlaceholders) {
        FileConfiguration cfg = plugin.getConfig();
        Utils utils = plugin.getUtils();

        if (cfg.isSet(path + ".lore")) {
            for (String line : cfg.getStringList(path + ".lore")) {
                for (Placeholder placeholder : lorePlaceholders) {
                    line = line.replace(placeholder.getTarget(), placeholder.getReplace());
                }

                lore.add(utils.color(line));
            }
        }

        glowing = cfg.isSet(path + ".glowing") && cfg.getBoolean(path + ".glowing");

        name = utils.color(cfg.getString(path + ".name"));

        material = Material.matchMaterial(cfg.getString(path + ".material", "BARRIER").toUpperCase());

        itemStack = plugin.getUtils().makeItem(material, name, lore, glowing);

        slot = cfg.getInt(path + ".slot", 0);
    }

    public ItemStack recreateItemStack(GraphicalAuth plugin, LinkedList<String> lore) {
        return plugin.getUtils().makeItem(material, name, lore, glowing);
    }

    public ItemStack recreateItemStack(GraphicalAuth plugin, LinkedList<String> lore, String name) {
        return plugin.getUtils().makeItem(material, name, lore, glowing);
    }

    public ItemStack recreateItemStack(GraphicalAuth plugin, LinkedList<String> lore, String name, Material material) {
        return plugin.getUtils().makeItem(material, name, lore, glowing);
    }
}
