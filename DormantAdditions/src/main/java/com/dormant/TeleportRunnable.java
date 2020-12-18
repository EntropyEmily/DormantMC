package com.dormant;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable for teleporting player to location.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class TeleportRunnable extends BukkitRunnable {
    private Player player;
    private Location destination;
    private Location origin;
    private int seconds = 3;

    /**
     * Constructs runnable with destination and player to teleport.
     * 
     * @param player      Player to teleport.
     * @param destination Destination to teleport player.
     */
    public TeleportRunnable(Player player, Location destination) {
        this.player = player;
        this.destination = destination;
        origin = player.getLocation();

        // Provides reference point for last damage cause.
        player.setLastDamageCause(null);
    }

    /**
     * @return True if player's displacement more than 1.73, false otherwise.
     */
    private boolean hasMoved() {
        return origin.distance(player.getLocation()) > 1.73;
    }

    /**
     * @return True if player damaged since teleport started, false otherwise.
     */
    private boolean isHurt() {
        return player.getLastDamageCause() != null;
    }

    /**
     * Decrements seconds left by one.
     * 
     * @return Seconds left. Minimum value of 0.
     */
    private int passOneSecond() {
        seconds--;
        return seconds <= 0 ? 0 : seconds;
    }

    /**
     * Teleports player to target. Executes after given number of iterations.
     */
    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            cancel();
        } else if (hasMoved() || isHurt()) {
            player.sendActionBar(ChatColor.RED + "Teleport cancelled.");
            cancel();
        } else if (seconds == 0) {
            player.teleport(destination);
            player.sendActionBar(ChatColor.GREEN + "Destination reached.");
            cancel();
        } else {
            player.sendActionBar(ChatColor.YELLOW + "Teleporting in " + seconds + "...");
        }
        passOneSecond();
    }

    /**
     * Starts the teleporter.
     * 
     * @param plugin Plugin's main class.
     */
    public void start(JavaPlugin plugin) {
        this.runTaskTimer(plugin, 0L, 20L);
    }

}
