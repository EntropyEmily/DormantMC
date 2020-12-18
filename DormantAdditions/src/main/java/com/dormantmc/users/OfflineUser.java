package com.dormantmc.users;

import java.time.LocalDate;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.dormantmc.Util;

/**
 * Offline user of the server. Represents Minecraft player with additonal data.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class OfflineUser {
    protected UUID playerId;
    protected String username;
    protected Nickname nickname;
    protected LocalDate firstJoinedDate, lastSeenDate;
    protected int hours;
    protected Location home;

    /**
     * Constructor for existing player/user.
     * 
     * @param playerId        Unique player id.
     * @param username        Player username.
     * @param nickname        Chosen nickname.
     * @param hours           Hours played on server.
     * @param firstJoinedDate Date of first login.
     * @param lastSeenDate    Date of most recent login.
     * @param home            Location of player home.
     */
    public OfflineUser(UUID playerId, String username, Nickname nickname, int hours, LocalDate firstJoinedDate,
            LocalDate lastSeenDate, Location home) {
        this.playerId = playerId;
        this.username = username;
        this.nickname = nickname;
        this.hours = hours;
        this.firstJoinedDate = firstJoinedDate;
        this.lastSeenDate = lastSeenDate;
        this.home = home;
    }

    /**
     * Constructor for new player/user.
     * 
     * @param player Player to create user for.
     */
    public OfflineUser(Player player) {
        this(player.getUniqueId(), player.getName(), new Nickname(player.getName()), 0, LocalDate.now(),
                LocalDate.now(), Util.getSpawn());
    }

    /**
     * @return Unique id of player/user.
     */
    public UUID getUniqueId() {
        return playerId;
    }

    /**
     * @return Username of player/user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Nickname of user. Includes formatted and plaintext variants.
     */
    public Nickname getNickname() {
        return nickname;
    }

    /**
     * @return Date of first login.
     */
    public LocalDate getFirstJoinedDate() {
        return firstJoinedDate;
    }

    /**
     * @return Date of most recent login.
     */
    public LocalDate getLastSeenDate() {
        if (isOnline()) {
            return LocalDate.now();
        }
        return lastSeenDate;
    }

    /**
     * @return Hours spent on the server.
     */
    public int getHours() {
        return hours;
    }

    /**
     * @return Location of user's home (can be teleported to).
     */
    public Location getHome() {
        return home;
    }

    /**
     * @return Representation of profile info as string.
     */
    public String getProfile() {
        StringBuilder profile = new StringBuilder("<------ " + nickname.formatted + ChatColor.WHITE + " ------>");
        profile.append("\n" + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "Username: " + getUsername());
        profile.append("\n" + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "First Joined: " + getFirstJoinedDate());
        profile.append("\n" + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "Last Seen: " + getLastSeenDate());
        profile.append("\n" + ChatColor.DARK_GRAY + "> " + ChatColor.WHITE + "Hours: " + getHours());
        return profile.toString();
    }

    /**
     * Compares user to other object.
     * 
     * @return True if other is user with matching unique id, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof User) {
            User user = (User) other;
            return getUniqueId().equals(user.getUniqueId());
        }
        return false;
    }

    /**
     * @return True if user's associated player is online, false otherwise.
     */
    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(playerId).isOnline();
    }

}
