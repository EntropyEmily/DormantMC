package com.dormantmc.users;

import org.bukkit.ChatColor;

/**
 * Simple nickname class including plaintext and formatted variants.
 * 
 * @author Emily
 * @version 2020.04.18
 */
public class Nickname {
    // Variants are final so can be public.
    // Nickname object replaced when nickname changes.
    public final String formatted;
    public final String unformatted;

    /**
     * Constructs new nickname with formatted and plaintext variants.
     * 
     * @param nickname String to convert to nickname object.
     */
    public Nickname(String nickname) {
        formatted = ChatColor.translateAlternateColorCodes('&', nickname) + ChatColor.RESET;
        unformatted = ChatColor.stripColor(formatted);
    }

}
