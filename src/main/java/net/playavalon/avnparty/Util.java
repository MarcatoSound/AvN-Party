package net.playavalon.avnparty;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.playavalon.avnparty.AvNParty.debugPrefix;

public class Util {

    public static String colorize(String s) {

        return s == null ? null : ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String fullColor(String s) {

        StringBuilder sb = new StringBuilder();

        String[] strs = s.split("(?=(\\{#[a-fA-F0-9]*}))");

        Pattern pat = Pattern.compile("(\\{(#[a-fA-F0-9]*)})(.*)");
        Matcher matcher;

        for (String str : strs) {
            matcher = pat.matcher(str);
            if (matcher.find()) {
                sb.append(ChatColor.of(matcher.group(2)));
                sb.append(matcher.group(3));
            } else {
                sb.append(str);
            }
        }

        return colorize(sb.toString());
    }

    public static boolean hasPermission(CommandSender sender, String node) {

        if (sender.hasPermission("*") || sender.hasPermission(node))
            return true;
        sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
        return false;
    }

    public static boolean hasPermissionSilent(CommandSender sender, String node) {

        return sender.hasPermission("*") || sender.hasPermission(node);
    }

    public static double round (double value, int places) {

        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;

    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;

    }

    public static boolean getRandomBoolean(double chance) {
        return Math.random() < chance;
    }

    public static Timestamp getFutureTime(int minutes) {
        return new Timestamp(System.currentTimeMillis()+(minutes*60*1000));
    }

    public static void giveOrDrop(Player player, ItemStack item) {

        if (item == null || item.getType() == Material.AIR) return;
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(Util.colorize(debugPrefix + "&cYour inventory is full! Item dropped to the ground..."));
            player.getWorld().dropItem(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }

    }
}
