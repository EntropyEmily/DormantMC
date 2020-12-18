package com.jacestratton.users;

import java.time.LocalDate;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import com.jacestratton.Util;

/**
 * User of the server. Represents Minecraft player with additional data.
 * 
 * @author JaceStratton
 * @version 2020.04.30
 */
public class User extends OfflineUser {

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
    public User(UUID playerId, String username, Nickname nickname, int hours, LocalDate firstJoinedDate,
            LocalDate lastSeenDate, Location home) {
        super(playerId, username, nickname, hours, firstJoinedDate, lastSeenDate, home);
    }

    /**
     * Constructor for new player/user.
     * 
     * @param player Player to create user for.
     */
    public User(Player player) {
        this(player.getUniqueId(), player.getName(), new Nickname(player.getName()), 0, LocalDate.now(),
                LocalDate.now(), Util.getSpawn());
    }

    /**
     * @return Current Minecraft account name.
     */
    @Override
    public String getUsername() {
        return getPlayer().getName();
    }

    /**
     * @return Current date.
     */
    @Override
    public LocalDate getLastSeenDate() {
        return LocalDate.now();
    }

    /**
     * @return Current Minecraft "minutes played" statistic converted to hours.
     */
    @Override
    public int getHours() {
        return getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000;
    }

    /**
     * Sets nickname of user.
     * 
     * @param nickname New nickname.
     */
    public void setNickname(Nickname nickname) {
        this.nickname = nickname;
    }

    /**
     * Sets location of user's home.
     * 
     * @param home Location of new home.
     */
    public void setHome(Location home) {
        this.home = home;
    }

    /**
     * @return Player associated with user.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

}
