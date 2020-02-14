package com.spaceman.playerUUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

import static com.spaceman.colorFormatter.ColorFormatter.formatError;
import static com.spaceman.colorFormatter.ColorTheme.sendErrorTheme;

public class PlayerUUID {
    
    /*
     * This PlayerUUID Util uses the following utils:
     * - ColorFormatter or ColorTheme
     * */
    
    public static String getPlayerName(String uuid) {
        return getPlayerName(UUID.fromString(uuid));
    }
    
    public static String getPlayerName(UUID uuid) {
        if (uuid == null) return null;
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
                return null;
            }
            return offlinePlayer.getName();
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
    
    public static String getPlayerUUID(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if ((op.hasPlayedBefore() || op.isOnline()) && op.getName() != null && op.getName().equalsIgnoreCase(playerName)) {
                return op.getUniqueId().toString();
            }
        }
        return null;
    }
    
    public static String getPlayerUUID(@Nullable Player player, String name) {
        String newPlayerUUID = PlayerUUID.getPlayerUUID(name);
        if (newPlayerUUID == null) {
            if (player != null) {
//                player.sendMessage(formatError("Could not find a player named %s", name)); //uses ColorFormatter
                sendErrorTheme(player, "Could not find a player named %s", name); //uses ColorTheme
            }
            return null;
        }
        return newPlayerUUID;
    }
}
