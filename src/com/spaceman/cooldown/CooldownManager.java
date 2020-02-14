package com.spaceman.cooldown;

import com.spaceman.Main;
import com.spaceman.fileHander.Files;
import com.spaceman.fileHander.GettingFiles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.spaceman.colorFormatter.ColorTheme.sendErrorTheme;

public enum CooldownManager {
    
    /*
    * This Cooldown Util uses the following utils:
    * - Pre-made Main template
    * - FileHandler
    * - ColorTheme
    *
    * in your onEnable:
    * CooldownManager.setDefaultValues();
    * CooldownManager.loopCooldown = false; //to reset the loop catcher (for if there are any chances made)
    * */
    
    COOLDOWN1("3000"),
    COOLDOWN2("COOLDOWN1");//todo edit
    
    private static final String pluginName = Main.getInstance().getDescription().getName();
    private static final String permissionRoot = pluginName;
    
    public static boolean loopCooldown = false;
    private static HashMap<UUID, HashMap<CooldownManager, Long>> cooldownTime = new HashMap<>();
    private static Files configFile = GettingFiles.getFile(/*fixme -> config name*/"config");
    
    private String defaultValue;
    
    CooldownManager(String defValue) {
        this.defaultValue = defValue;
    }
    
    public static void setDefaultValues() {
        for (CooldownManager cooldown : CooldownManager.values()) {
            if (!configFile.getConfig().contains("cooldown." + cooldown.name())) {
                cooldown.edit(cooldown.defaultValue);
            }
        }
    }
    
    public static boolean contains(String name) {
        for (CooldownManager cooldown : CooldownManager.values()) {
            if (cooldown.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String value() {
        return configFile.getConfig().getString("cooldown." + this.name());
    }
    
    public void edit(String value) {
        configFile.getConfig().set("cooldown." + this.name(), value);
        configFile.saveConfig();
        loopCooldown = false;
    }
    
    public void edit(long value) {
        edit(String.valueOf(value));
    }
    
    public void update(Player player) {
        if (contains(this.value())) {
            CooldownManager.valueOf(this.value()).update(player);
            return;
        }
        HashMap<CooldownManager, Long> timeMap = cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>());
        timeMap.put(this, System.currentTimeMillis());
        cooldownTime.put(player.getUniqueId(), timeMap);
    }
    
    private long getTime(Player player, List<String> prev) {
        if (prev.contains(this.name()) || loopCooldown) {
            Bukkit.getLogger().log(Level.WARNING, "[" + pluginName + "] There is a loop in the cooldown configuration... Please edit the cooldown configuration");
            loopCooldown = true;
            return 0;
        } else {
            prev.add(this.name());
        }
        
        if (!configFile.getConfig().contains("cooldown." + this.name())) {
            return 0;
        } else {
            String cooldownValue = this.value();
            if (cooldownValue.equals("permission")) {
                for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
                    if (permissionInfo.getPermission().startsWith(permissionRoot + "." + this.name() + ".")) {
                        try {
                            long value = Long.parseLong(permissionInfo.getPermission().replace(permissionRoot + "." + this.name() + ".", ""));
                            return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
                        } catch (NumberFormatException nfe) {
                            Bukkit.getLogger().log(Level.WARNING, "[" + pluginName + "] Permission " + permissionRoot + "." + this.name() + ".X is not a valid Long value");
                            return 0;
                        }
                    }
                }
                return 0;
            }
            for (CooldownManager cooldown : CooldownManager.values()) {
                if (cooldownValue.equals(cooldown.name())) {
                    return cooldown.getTime(player, prev);
                }
            }
            try {
                long value = Long.parseLong(this.value());
                return (cooldownTime.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(this, 0L) + value) - System.currentTimeMillis();
            } catch (NumberFormatException nfe) {
                Bukkit.getLogger().log(Level.WARNING, "[" + pluginName + "] The value of " + this.name() + " does not have a valid timeout value (" + this.value() + ")");
                return 0;
            }
        }
    }
    
    public long getTime(Player player) {
        return getTime(player, new ArrayList<>());
    }
    
    public boolean hasCooled(Player player) {
        return hasCooled(player, true);
    }
    
    public boolean hasCooled(Player player, boolean sendMessage) {
        long cooldown = this.getTime(player);
        if (cooldown / 1000 > 0) {
            if (sendMessage) sendErrorTheme(player, "You must wait another %s to use this again",
                    (cooldown / 1000) + " second" + ((cooldown / 1000) == 1 ? "" : "s"));
            return false;
        }
        return true;
    }
}
