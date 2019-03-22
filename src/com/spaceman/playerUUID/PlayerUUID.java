package com.spaceman.playerUUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static com.spaceman.ColorFormatter.formatError;

public class PlayerUUID {

    public static String getPlayerName(String uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        if (offlinePlayer == null) {
            return null;
        }
        return offlinePlayer.getName();
    }

    public static String getPlayerUUID(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName().equals(playerName)) {
                return op.getUniqueId().toString();
            }
        }
        return null;
    }

    public static ArrayList<String> getGlobalPlayerUUID(String playerName) {
        ArrayList<String> temp = new ArrayList<>();
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName().equalsIgnoreCase(playerName)) {
                temp.add(op.getUniqueId().toString());
            }
        }
        return temp;
    }

    public static String getPlayerUUID(Player player, String name) {
        String newPlayerUUID = PlayerUUID.getPlayerUUID(name);
        if (newPlayerUUID == null) {
            ArrayList<String> globalNames = PlayerUUID.getGlobalPlayerUUID(name);
            if (globalNames.size() == 1) {
                newPlayerUUID = globalNames.get(0);
            } else if (globalNames.size() == 0) {
                if (player != null) {
                    player.sendMessage(formatError("Could not find any players named %s",name));
                }
                return null;
            } else {
                if (player != null) {
                    player.sendMessage(formatError("There are more players found with the name %s, please type the correct name with correct capitals", name));
                }
                return null;
            }
        }
        return newPlayerUUID;
    }
}
