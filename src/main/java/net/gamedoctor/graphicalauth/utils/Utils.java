package net.gamedoctor.graphicalauth.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import net.gamedoctor.graphicalauth.GraphicalAuth;
import net.gamedoctor.graphicalauth.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mindrot.BCrypt;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Utils {
    private final GraphicalAuth plugin;

    public String color(String from) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        from = from.replace("&#", "#");
        Matcher matcher = pattern.matcher(from);
        while (matcher.find()) {
            String hexCode = from.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch)
                builder.append("&").append(c);
            from = from.replace(hexCode, builder.toString());
            matcher = pattern.matcher(from);
        }

        return ChatColor.translateAlternateColorCodes('&', from);
    }

    public ItemStack makeItem(Material material, String name, LinkedList<String> lore, boolean glowing) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setLore(lore);
            itemMeta.setDisplayName(name);

            if (glowing) {
                itemMeta.addEnchant(Enchantment.MENDING, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    public String hash(String msg) {
        return BCrypt.hashpw(msg, BCrypt.gensalt());
    }

    public boolean isHashValid(String hash, String original) {
        return BCrypt.checkpw(original, hash);
    }

    public void connectBungeeCordServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public String getTimeString(long time) {
        Config cfg = plugin.getCfg();
        int seconds = (int) ((time /= 1000L) % 60L);
        int minutes = (int) ((time /= 60L) % 60L);
        int hours = (int) ((time /= 60L) % 24L);
        int days = (int) (time / 24L);

        StringBuilder sb = new StringBuilder();
        if (cfg.isTimeStringFormat_displayOnlyHighest()) {
            if (days != 0) {
                sb.append(days).append(" ").append(cfg.getTimeStringFormat_days());
            } else if (hours != 0) {
                sb.append(hours).append(" ").append(cfg.getTimeStringFormat_hours());
            } else if (minutes != 0) {
                sb.append(minutes).append(" ").append(cfg.getTimeStringFormat_minutes());
            } else if (seconds >= 0) {
                sb.append(seconds).append(" ").append(cfg.getTimeStringFormat_seconds());
            }
        } else {
            if (days != 0) {
                sb.append(days).append(" ").append(cfg.getTimeStringFormat_days()).append(" ");
            }

            if (hours != 0) {
                sb.append(hours).append(" ").append(cfg.getTimeStringFormat_hours()).append(" ");
            }

            if (minutes != 0) {
                sb.append(minutes).append(" ").append(cfg.getTimeStringFormat_minutes()).append(" ");
            }

            if (seconds != 0) {
                sb.append(seconds).append(" ").append(cfg.getTimeStringFormat_seconds());
            }
        }
        return sb.toString().trim();
    }
}