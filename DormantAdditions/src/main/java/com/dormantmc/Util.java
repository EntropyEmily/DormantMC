package com.dormantmc;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Utility class with general purpose helper methods.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class Util {

    /**
     * Default empty constructor.
     */
    private Util() {
        // No fields to initialize.
    }

    /**
     * @return Main overworld.
     */
    public static World getOverworld() {
        World world = Bukkit.getWorld("world");
        if (world == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment() == Environment.NORMAL) {
                    world = w;
                }
            }
        }
        return world;
    }

    /**
     * @return World spawn of main overworld.
     */
    public static Location getSpawn() {
        return getOverworld().getSpawnLocation();
    }
    

    /**
     * Splits location into x, y, z, and yaw components.
     * 
     * @param location Location to split.
     * @return Location components formatted in list form.
     */
    public static String splitLocation(Location location) {
        String world = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();

        return "'" + world + "'," + x + "," + y + "," + z + "," + yaw;
    }

    /**
     * Sends automatically formatted message to player.
     * 
     * @param player  Player to send message.
     * @param message Message to send.
     */
    public static void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Gives player items and drops items when inventory full.
     * 
     * @param player   Player to give items.
     * @param material Type of items to give player.
     * @param amount   Amount of items to give player.
     */
    public static void give(Player player, Material material, int amount) {
        HashMap<Integer, ItemStack> excess = player.getInventory().addItem(new ItemStack(material, amount));
        if (!excess.isEmpty()) {
            for (ItemStack stack : excess.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), stack);
            }
            sendMessage(player, "&eInventory full. Dropping items on ground.");
        }
    }

    /**
     * Clears given amount and type of items from player's inventory.
     * 
     * @param player   Player to take items from.
     * @param material Type of items to take from player.
     * @param amount   Amount of items to take from player.
     * @return True if player has enough items to be cleared, false otherwise.
     */
    public static boolean clear(Player player, Material material, int amount) {
        PlayerInventory inventory = player.getInventory();
        if (inventory.containsAtLeast(new ItemStack(material), amount)) {
            for (int index : inventory.all(material).keySet()) {
                ItemStack stack = inventory.getItem(index);
                if (amount > stack.getAmount()) {
                    amount -= stack.getAmount();
                    stack.setAmount(0);
                } else {
                    stack.setAmount(stack.getAmount() - amount);
                    amount = 0;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if string is alphanumeric, including underscore.
     * 
     * @param string String to test.
     * @return True if string has only letters, numbers, and '_', false otherwise.
     */
    public static boolean isAlphanumeric(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

}
