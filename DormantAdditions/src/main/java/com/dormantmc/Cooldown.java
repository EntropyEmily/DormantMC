package com.dormantmc;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Maps players to system time for tracking time since last action.
 * 
 * @author Emily
 * @date 2019.06.13
 */
public class Cooldown {
    private final int seconds;
    private HashMap<UUID, Long> cooldowns;

    /**
     * Creates new map and sets cooldown time.
     * 
     * @param seconds Cooldown time in seconds.
     */
    public Cooldown(int seconds) {
        this.seconds = seconds;
        cooldowns = new HashMap<UUID, Long>();
    }

    /**
     * Gets cooldown of player.
     * 
     * @param player Player to check for cooldown
     * @return Seconds left in cooldown.
     */
    public int getCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long time = System.currentTimeMillis();

        if (cooldowns.get(playerId) == null || cooldowns.get(playerId) <= time - (seconds * 1000)) {
            cooldowns.put(playerId, time);
            return 0;
        }
        if (time % 1000 > 0) {
            time += 1000;
        }

        return seconds - (int) ((time - cooldowns.get(playerId)) / 1000);
    }

    /**
     * @return Cooldown time in seconds.
     */
    public int getCooldownTime() {
        return seconds;
    }

}
