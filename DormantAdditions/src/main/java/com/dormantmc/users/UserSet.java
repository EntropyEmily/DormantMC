package com.dormantmc.users;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.entity.Player;

/**
 * Set containing online users. Cannot contain duplicates.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class UserSet {
    private Set<User> users = new LinkedHashSet<>();

    /**
     * Default empty constructor.
     */
    public UserSet() {
        // No fields to instantiate.
    }

    /**
     * Add user to set.
     * 
     * @param user User to add to online set.
     * @return True if user not already in set, false otherwise.
     */
    public boolean add(User user) {
        return users.add(user);
    }

    /**
     * Remove player's associated user from set.
     * 
     * @param player Player logging off with association to user.
     * @return Removed user. Null if remove already called and user not found.
     */
    public User remove(Player player) {
        for (User user : users) {
            if (user.getUniqueId().equals(player.getUniqueId())) {
                users.remove(user);
                return user;
            }
        }
        return null;
    }

    /**
     * Get player's associated user.
     * 
     * @param player Player with association to user.
     * @return Associated user. Null if offline/not found.
     */
    public User get(Player player) {
        for (User user : users) {
            if (user.getUniqueId().equals(player.getUniqueId())) {
                return user;
            }
        }
        return null;
    }

}
