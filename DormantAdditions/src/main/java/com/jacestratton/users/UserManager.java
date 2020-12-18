package com.jacestratton.users;

import java.time.LocalDate;

import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.jacestratton.database.UserDatabase;

/**
 * Retrieves users from database and manages user data. Syncrhonizes information
 * on server and in database.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
public class UserManager {
    private UserSet users = new UserSet();
    private UserDatabase database;

    /**
     * Loads database for user data.
     * 
     * @param plugin Plugin's main class.
     */
    public UserManager(JavaPlugin main) {
        database = new UserDatabase(main);
    }

    /**
     * Gets user from online set by player.
     * 
     * @param player Player associated with user.
     * @return Online user.
     */
    private User getUser(Player player) {
        return users.get(player);
    }

    /**
     * Gets user from partial name.
     * 
     * @param name Name or beginning of name.
     * @return User with similar name or null if not found.
     */
    private User getUser(String name) {
        return database.getUser(name);
    }

    /**
     * Updates all displayed names of player (e.g. list, display, and tag names).
     * 
     * @param player  Player to update names of.
     * @param oldName Old name to replace in existing display names.
     */
    private void updateDisplayNames(Player player, String oldName) {
        String nickname = getUser(player).getNickname().formatted;

        // Replace player name in display name with nickname.
        String displayName = player.getDisplayName();
        player.setDisplayName(displayName.replace(oldName, nickname));

        // Headers and footers will not be replaced.
        player.setPlayerListName(nickname);
    }

    /**
     * Loads user from database and adds to online set.
     * 
     * @param player Player logging in.
     */
    public void loadUser(Player player) {
        // Retrieves user to load or creates new user.
        User user = database.getUser(player);
        if (user == null) {
            user = new User(player);
            database.insertUser(user);
        }
        users.add(user);
        updateDisplayNames(player, player.getName());
    }

    /**
     * Removes user from online set and updates database.
     * 
     * @param player Player associated with user.
     */
    public void unloadUser(Player player) {
        int hours = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000;
        database.updateHoursAndLastSeen(player, hours, LocalDate.now());
        database.updateUsername(player);
        users.remove(player);
    }

    /**
     * Get user's nickname.
     * 
     * @param player Player with user.
     * @return Nickname of user.
     */
    public Nickname getNickname(Player player) {
        return getUser(player).getNickname();
    }

    /**
     * Get user's first joined date.
     * 
     * @param player Player with user.
     * @return Date user first joined.
     */
    public LocalDate getFirstJoinedDate(Player player) {
        return getUser(player).getFirstJoinedDate();
    }

    /**
     * Get user's last seen date.
     * 
     * @param player Player with user.
     * @return Date user was last seen.
     */
    public LocalDate getLastSeenDate(Player player) {
        return getUser(player).getLastSeenDate();
    }

    /**
     * Get user's home.
     * 
     * @param player Player with user.
     * @return User's home location.
     */
    public Location getHome(Player player) {
        return getUser(player).getHome();
    }

    /**
     * Get user's hours.
     * 
     * @param player Player with user.
     * @return User's total hours played on server.
     */
    public int getHours(Player player) {
        return getUser(player).getHours();
    }

    /**
     * Get user's profile.
     * 
     * @param player Player with user.
     * @return User's profile as string.
     */
    public String getProfile(Player player) {
        return getUser(player).getProfile();
    }

    /**
     * Get user's profile.
     * 
     * @param name User's name or partial name.
     * @return User's profile as string, or null if no user found.
     */
    public String getProfile(String name) {
        OfflineUser user = getUser(name);
        return user != null ? user.getProfile() : null;
    }

    /**
     * Updates nickname for player's associated user.
     * 
     * @param player   Player of associated user.
     * @param nickname New nickname.
     * @return boolean True if nickname not taken, false if taken.
     */
    public boolean setNickname(Player player, Nickname nickname) {
        User user = getUser(player);
        if (database.nameTaken(nickname.unformatted)) {
            if (!nickname.unformatted.equals(player.getName())
                    && !nickname.unformatted.equals(user.getNickname().unformatted)) {
                return false;
            }
        }
        String oldName = user.getNickname().formatted;
        user.setNickname(nickname);
        database.updateNickname(player, nickname);
        updateDisplayNames(player, oldName);
        return true;
    }

    /**
     * Updates player home.
     * 
     * @param player Player of associated user.
     * @param home   New location of player home.
     */
    public void setHome(Player player) {
        Location home = player.getLocation();
        getUser(player).setHome(home);
        database.insertHome(player, home);
    }

}
