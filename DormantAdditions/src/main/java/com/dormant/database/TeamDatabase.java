package com.dormant.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.dormant.Util;
import com.dormant.teams.Rank;
import com.dormant.teams.Team;



public class TeamDatabase extends Database {
    public static final String CREATE_TEAMS_TABLE = "CREATE TABLE IF NOT EXISTS Teams("
            + "'Team_ID' char(36) NOT NULL,"
            + "'Name' varchar(20) NOT NULL COLLATE NOCASE,"
            + "'Prefix' varchar(6) NOT NULL COLLATE NOCASE,"
            + "'Color' varchar(12) NOT NULL,"
            + "PRIMARY KEY ('Team_ID'));";
    public static final String CREATE_MEMBERS_TABLE = "CREATE TABLE IF NOT EXISTS Members("
            + "'Player_ID' char(36) NOT NULL,"
            + "'Team_ID' char(36) NOT NULL,"
            + "'Rank' varchar(6) NOT NULL,"
            + "PRIMARY KEY ('Player_ID'));";
    public static final String CREATE_WARPS_TABLE = "CREATE TABLE IF NOT EXISTS Warps("
            + "'Team_ID' char(36) NOT NULL,"
            + "'World' varchar(255) NOT NULL,"
            + "'X' double NOT NULL,"
            + "'Y' double NOT NULL,"
            + "'Z' double NOT NULL,"
            + "'Yaw' float NOT NULL,"
            + "PRIMARY KEY ('Team_ID'));";
    
    public TeamDatabase(JavaPlugin main) {
        super(main, "Team_Database", CREATE_TEAMS_TABLE, CREATE_MEMBERS_TABLE, CREATE_WARPS_TABLE);
    }
    
    public void insertTeam(Team team) {
        String teamId = team.getUniqueId().toString();
        String name = team.getName();
        String prefix = team.getPrefix();
        String color = team.getColor().name();
        
        String playerId = "";
        String rank = "";
        for (Entry<UUID, Rank> entry : team.getMembers().entrySet()) {
            playerId = entry.getKey().toString();
            rank = entry.getValue().name();
        }
        
        String insertTeamStatement = "INSERT INTO Teams"
                + " VALUES ('" + teamId + "','" + name + "','" + prefix + "','" + color + "');";
        String insertMemberStatement = "INSERT INTO Members"
                + " VALUES ('" + playerId + "','" + teamId + "','" + rank + "');";
        String insertWarpStatement = "INSERT INTO Warps"
                + " VALUES ('" + teamId + "'," + Util.splitLocation(team.getWarp()) + ");";
        executeUpdate(insertTeamStatement, insertMemberStatement, insertWarpStatement);
    }
    
    public void updateTeamNameAndPrefix(UUID teamId, String name, String prefix) {
        String statement = "UPDATE Teams SET Name = '" + name + "', Prefix = '" + prefix
                + "' WHERE Team_ID = '" + teamId.toString() + "';";
        executeUpdate(statement);
    }
    
    public void updateTeamPrefix(UUID teamId, String prefix) {
        String statement = "UPDATE Teams SET Prefix = '" + prefix
                + " WHERE Team_ID = '" + teamId.toString() + "';";
        executeUpdate(statement);
    }
    
    public void updateTeamColor(UUID teamId, ChatColor color) {
        String statement = "UPDATE Teams SET Color = '" + color.name()
                + " WHERE Team_ID = '" + teamId.toString() + "';";
        executeUpdate(statement);
    }
    
    private Team executeTeamQuery(String condition) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        
        Team team = null;
        try {
            con = getSQLConnection();
            ps = con.prepareStatement("SELECT Team_ID,Name,Prefix,Color,World,X,Y,Z,Yaw,Player_ID,Rank"
                    + " FROM Teams INNER JOIN Members USING(Team_ID) INNER JOIN Warps USING(Team_ID) WHERE " + condition + ";");
            res = ps.executeQuery();
            
            if (res.next()) {
                UUID teamId = UUID.fromString(res.getString("Team_ID"));
                String name = res.getString("Name");
                String prefix = res.getString("Prefix");
                ChatColor color = ChatColor.valueOf(res.getString("Color"));
                
                World world = Bukkit.getWorld(res.getString("World"));
                double x = res.getDouble("X");
                double y = res.getDouble("Y");
                double z = res.getDouble("Z");
                float yaw = res.getFloat("Yaw");
                Location warp = new Location(world, x, y, z, yaw, 0);
                
                Map<UUID, Rank> members = new HashMap<>();
                members.put(UUID.fromString(res.getString("Player_ID")), Rank.valueOf(res.getString("Rank")));
                while (res.next()) {
                    members.put(UUID.fromString(res.getString("Player_ID")), Rank.valueOf(res.getString("Rank")));
                }
                
                team = new Team(teamId, name, prefix, color, warp, members);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        } finally {
            close(con, ps, res);
        }
        return team;
    }
    
    public Team getTeam(UUID playerId) {
        String condition = "Player_ID = '" + playerId + "'";
        return executeTeamQuery(condition);
    }
    
    public Team getTeam(String name, boolean exact) {
        String condition = "Name LIKE '" + name + "%'";
        if (exact) {
            condition = "Name = '" + name + "'";
        }
        return executeTeamQuery(condition);
    }
    
    public void deleteTeam(UUID teamId) {
        String deleteTeamStatement = "DELETE FROM Teams WHERE Team_ID = '" + teamId + "';";
        String deleteMembersStatement = "DELETE FROM Members WHERE Team_ID = '" + teamId + "';";
        String deleteWarpStatement = "DELET FROM Warps WHERE Team_ID = '" + teamId + "';";
        executeUpdate(deleteTeamStatement, deleteMembersStatement, deleteWarpStatement);
    }
    
    public void insertMember(UUID teamId, UUID playerId, Rank rank) {
        String insertMemberStatement = "REPLACE INTO Members"
                + " VALUES ('" + playerId.toString() + "','" + teamId.toString() + "','" + rank.name() + "');";
        executeUpdate(insertMemberStatement);
    }
    
    public void deleteMember(UUID playerId) {
        String statement = "DELETE FROM Members WHERE Player_ID = '" + playerId + "';";
        executeUpdate(statement);
    }
    
    public void insertWarp(UUID teamId, Location warp) {
        String statement = "REPLACE INTO Warps VALUES('" + teamId.toString() + "'," + Util.splitLocation(warp) + ");";
        executeUpdate(statement);
    }
    
}
