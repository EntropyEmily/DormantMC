package com.dormantmc.database;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class Error {

    public static void execute(JavaPlugin plugin, Exception e) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", e);
    }
    
    public static void close(JavaPlugin plugin, Exception e) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection.", e);
    }
    
}
