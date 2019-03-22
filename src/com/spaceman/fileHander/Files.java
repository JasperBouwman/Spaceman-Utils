package com.spaceman.fileHander;

import com.spaceman.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Files {

    private final String fileName;
    
    private File configFile;
    private FileConfiguration fileConfiguration;

    public Files(String fileName) {
        this.fileName = fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml");
        this.configFile = new File(Main.getInstance().getDataFolder(), fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
    }

    public Files(String extraPath, String fileName) {
        this.fileName = fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml");
        this.configFile = new File(Main.getInstance().getDataFolder() + extraPath, fileName + (fileName.toLowerCase().endsWith(".yml") ? "" : ".yml"));
    }

    private void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig() {

        if (fileConfiguration != null && configFile != null) {
            try {
                getConfig().save(configFile);

            } catch (IOException ex) {
                Main.log(Level.SEVERE, "Could not save config to " + configFile + ", " + ex.getMessage());
            }
        }
    }
    
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            Main.getInstance().saveResource(fileName, false);
        }
    }
}
