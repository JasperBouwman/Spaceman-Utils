package com.spaceman;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    
    public static Main getInstance() {
        return JavaPlugin.getPlugin(Main.class);
    }
    
    public static void log(String s) {
        log(Level.INFO, s);
    }
    
    public static void log(Level l, String s) {
        Bukkit.getLogger().log(l, "[" + Main.getInstance().getDescription().getName() + "] " + s);
    }
    
}
