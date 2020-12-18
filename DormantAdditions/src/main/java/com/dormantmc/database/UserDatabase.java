package com.dormantmc.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dormantmc.Util;
import com.dormantmc.users.Nickname;
import com.dormantmc.users.User;

/**
 * Creates, inserts, updates, and queries database containing user data.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class UserDatabase extends Database {
    // User table containing identifying information and profile stats.
    public static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users("
            + "'Player_ID' char(36) NOT NULL," + "'Username' varchar(16) NOT NULL COLLATE NOCASE,"
            + "'Formatted_Nickname' varchar(255) NOT NULL,"
            + "'Unformatted_Nickname' varchar(16) NOT NULL COLLATE NOCASE," + "'Hours' int NOT NULL,"
            + "'First_Joined_Date' char(10) NOT NULL," + "'Last_Seen_Date' char(10) NOT NULL,"
            + "PRIMARY KEY ('Player_ID'));";
    // Homes table containing "home" location of user.
    public static final String CREATE_HOMES_TABLE = "CREATE TABLE IF NOT EXISTS Homes("
            + "'Player_ID' char(36) NOT NULL," + "'World' varchar(255) NOT NULL," + "'X' double NOT NULL,"
            + "'Y' double NOT NULL," + "'Z' double NOT NULL," + "'Yaw' float NOT NULL," + "PRIMARY KEY ('Player_ID'));";

    /**
     * Constructs database with tables and procedure methods.
     * 
     * @param main Plugin's main class.
     */
    public UserDatabase(JavaPlugin main) {
        super(main, "User_Database", CREATE_USERS_TABLE, CREATE_HOMES_TABLE);
    }

    /**
     * Inserts new user into database.
     * 
     * @param user User with data to insert.
     */
    public void insertUser(User user) {
        String playerId = user.getUniqueId().toString();
        String username = user.getUsername();
        Nickname nickname = user.getNickname();
        int hours = user.getHours();
        LocalDate firstJoinedDate = user.getFirstJoinedDate();
        LocalDate lastSeenDate = user.getLastSeenDate();

        String insertUserStatement = "INSERT INTO Users" + " VALUES ('" + playerId + "','" + username + "','"
                + nickname.formatted + "','" + nickname.unformatted + "'," + hours + ",'" + firstJoinedDate + "','"
                + lastSeenDate + "');";
        String insertHomeStatement = "INSERT INTO Homes" + " VALUES ('" + playerId + "',"
                + Util.splitLocation(user.getHome()) + ");";
        executeUpdate(insertUserStatement, insertHomeStatement);
    }

    /**
     * Updates username of user in case of account name change.
     * 
     * @param player Player with user.
     */
    public void updateUsername(Player player) {
        String playerId = player.getUniqueId().toString();
        String username = player.getName();

        String updateUsernameStatement = "UPDATE Users SET Username = '" + username + "' WHERE Player_ID = '" + playerId
                + "';";
        executeUpdate(updateUsernameStatement);
    }

    /**
     * Updates nickname of user.
     * 
     * @param player   Player with user.
     * @param nickname New nickname to insert.
     */
    public void updateNickname(Player player, Nickname nickname) {
        String playerId = player.getUniqueId().toString();

        String statement = "UPDATE Users SET Formatted_Nickname = '" + nickname.formatted
                + "', Unformatted_Nickname = '" + nickname.unformatted + "' WHERE Player_ID = '" + playerId + "';";
        executeUpdate(statement);
    }

    /**
     * Updates hours and last seen date of user as data changes frequently.
     * 
     * @param player       Player with user.
     * @param hours        Hours under player statistics.
     * @param lastSeenDate Date user was last seen (e.g. current date).
     */
    public void updateHoursAndLastSeen(Player player, int hours, LocalDate lastSeenDate) {
        String playerId = player.getUniqueId().toString();

        String statement = "UPDATE Users SET Hours = " + hours + ", Last_Seen_Date = '" + lastSeenDate
                + "' WHERE Player_ID = '" + playerId.toString() + "';";
        executeUpdate(statement);
    }

    /**
     * Executes query returning user object given search condition.
     * 
     * @param condition Search condition of query.
     * @return Found user object, or null if no match found.
     */
    private User executeUserQuery(String condition) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        User user = null;
        try {
            con = getSQLConnection();
            ps = con.prepareStatement("SELECT Player_ID,Username,Formatted_Nickname,"
                    + "Hours,First_Joined_Date,Last_Seen_Date,World,X,Y,Z,Yaw"
                    + " FROM Users INNER JOIN Homes USING(Player_ID) WHERE " + condition + ";");
            res = ps.executeQuery();

            if (res.next()) {
                UUID playerId = UUID.fromString(res.getString("Player_ID"));
                String username = res.getString("Username");
                Nickname nickname = new Nickname(res.getString("Formatted_Nickname"));
                int hours = res.getInt("Hours");
                LocalDate firstJoinedDate = LocalDate.parse(res.getString("First_Joined_Date"));
                LocalDate lastSeenDate = LocalDate.parse(res.getString("Last_Seen_Date"));

                World world = Bukkit.getWorld(res.getString("World"));
                double x = res.getDouble("X");
                double y = res.getDouble("Y");
                double z = res.getDouble("Z");
                float yaw = res.getFloat("Yaw");
                Location home = new Location(world, x, y, z, yaw, 0);

                user = new User(playerId, username, nickname, hours, firstJoinedDate, lastSeenDate, home);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        } finally {
            close(con, ps, res);
        }
        return user;
    }

    /**
     * Executes query returning user object from unique player ID.
     * 
     * @param playerId Unique player ID as search condition.
     * @return Found user object, or null if no match found.
     */
    public User getUser(UUID playerId) {
        String condition = "Player_ID = '" + playerId.toString() + "'";
        return executeUserQuery(condition);
    }

    /**
     * Executes query returning user object from player.
     * 
     * @param player Player with ID as search condition.
     * @return Found user object, or null if no match found.
     */
    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    /**
     * Executes query returning user object from name or partial name.
     * 
     * @param name Name or partial name of user as search condition.
     * @return Found user object, or null if no match found.
     */
    public User getUser(String name) {
        String condition = "Unformatted_Nickname LIKE '" + name + "%' OR Username LIKE '" + name + "%'";
        return executeUserQuery(condition);
    }

    /**
     * Executes query returning user from name and returns whether match was found.
     * 
     * @param name Exact name of user (username or nickname).
     * @return True if match found, false if no match found.
     */
    public boolean nameTaken(String name) {
        String condition = "Unformatted_Nickname = '" + name + "' OR Username = '" + name + "'";
        return executeUserQuery(condition) != null;
    }

    /**
     * Inserts user home or replaces existing record.
     * 
     * @param player Player with user.
     * @param home   New home location of user.
     */
    public void insertHome(Player player, Location home) {
        String playerId = player.getUniqueId().toString();

        String statement = "REPLACE INTO Homes VALUES('" + playerId + "'," + Util.splitLocation(home) + ");";
        executeUpdate(statement);
    }

}
