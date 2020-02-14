package com.spaceman.colorFormatter;

import org.bukkit.ChatColor;

public class ColorFormatter {
    /*
    * This util gives the string standardized colors, and gives variables different colors
    * Example usage: player.sendMessage(formatInfo("%s is set to the value &s", "Some object", "Some value"));
    * */

    public static ChatColor infoColor = ChatColor.DARK_AQUA;
    public static ChatColor varInfoColor = ChatColor.BLUE;
    public static ChatColor varInfo2Color = ChatColor.DARK_BLUE;

    public static ChatColor successColor = ChatColor.GREEN;
    public static ChatColor varSuccessColor = ChatColor.DARK_GREEN;

    public static ChatColor errorColor = ChatColor.RED;
    public static ChatColor varErrorColor = ChatColor.DARK_RED;

    public static String formatInfo(String text, String... args) {
        for (String arg : args) {
            text = text.replaceFirst("%s", varInfoColor + arg + infoColor);
        }
        return infoColor + text;
    }

    public static String formatError(String text, String... args) {
        for (String arg : args) {
            text = text.replaceFirst("%s", varErrorColor + arg + errorColor);
        }
        return errorColor + text;
    }

    public static String formatSuccess(String text, String... args) {
        for (String arg : args) {
            text = text.replaceFirst("%s", varSuccessColor + arg + successColor);
        }
        return successColor + text;
    }
}
