package com.dormant;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages loading, saving, and reloading configurations.
 * 
 * @author Emily
 * @version 2020.04.15
 */
public class ConfigurationManager {
    private DormantMain plugin;
    private FileConfiguration config;
    private File configFile;

    /**
     * Performs configuration setup.
     * 
     * @param main Plugin's main class.
     */
    public ConfigurationManager(DormantMain main) {
        plugin = main;
        setUp();
    }

    /**
     * Copies file to server if it doesn't exist and loads configuration.
     */
    private void setUp() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * @return FileConfiguration.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Helper method for quick access to teleport warmup.
     * 
     * @return Teleport warmup time.
     */
    public int getTpWarmup() {
        return config.getInt("teleport.warmup");
    }

    /**
     * Helper method for quick access to teleport timeout.
     * 
     * @return Teleport timeout time.
     */
    public int getTpTimeout() {
        return config.getInt("teleport.timeout");
    }

    /**
     * Saves file configurations.
     */
    public void saveConfigs() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, ChatColor.RED + "Unable to save config to file.", e.getMessage());
        }
    }

    /**
     * Reloads configuration from file.
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

}
