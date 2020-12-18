package com.dormant.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dormant.database.TeamDatabase;

public class TeamManager {
    private List<Team> teams = new ArrayList<>();
    private TeamDatabase database;

    public TeamManager(JavaPlugin plugin) {
        database = new TeamDatabase(plugin);
    }

    public void createTeam(UUID ownerId, String name) {
        Team team = new Team(ownerId, name);
        teams.add(team);
        database.insertTeam(team);
    }

    public Team getTeam(UUID playerId) {
        for (Team team : teams) {
            if (team.contains(playerId)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    public Team getTeam(String name, boolean exact) {
        return database.getTeam(name, exact);
    }

    public void loadTeam(UUID playerId) {
        if (getTeam(playerId) == null) {
            Team team = database.getTeam(playerId);
            if (team != null) {
                teams.add(team);
            }
        }
    }

    public void unloadTeam(UUID playerId) {
        Team team = getTeam(playerId);
        if (team != null && !team.isOnline()) {
            teams.remove(team);
        }
    }

    public boolean setTeamName(UUID senderId, String name, String prefix) {
        Team team = getTeam(senderId);
        if (team.setName(senderId, name) && team.setName(senderId, prefix)) {
            database.updateTeamNameAndPrefix(team.getUniqueId(), name, prefix);
            return true;
        }
        return false;
    }

    public boolean setTeamPrefix(UUID senderId, String prefix) {
        Team team = getTeam(senderId);
        if (team.setPrefix(senderId, prefix)) {
            database.updateTeamPrefix(team.getUniqueId(), prefix);
            return true;
        }
        return false;
    }

    public boolean setTeamColor(UUID senderId, ChatColor color) {
        Team team = getTeam(senderId);
        if (team.setColor(senderId, color)) {
            database.updateTeamColor(team.getUniqueId(), color);
            return true;
        }
        return false;
    }

    public boolean setTeamWarp(UUID senderId, Location warp) {
        Team team = getTeam(senderId);
        if (team.setWarp(senderId, warp)) {
            database.insertWarp(team.getUniqueId(), warp);
            return true;
        }
        return false;
    }

    public boolean inviteTeamMember(UUID senderId, UUID targetId) {
        Team team = getTeam(senderId);
        if (team.inviteMember(senderId, targetId)) {
            return true;
        }
        return false;
    }

    public void addTeamMember(UUID targetId) {
        Team team = getTeam(targetId);
        team.addMember(targetId);
        database.insertMember(team.getUniqueId(), targetId, team.getMemberRank(targetId));
    }

    public boolean removeTeamMember(UUID senderId, UUID targetId) {
        Team team = getTeam(senderId);
        if (team.removeMember(senderId, targetId)) {
            database.deleteMember(targetId);
            return true;
        }
        return false;
    }

    public boolean setTeamMemberRank(UUID senderId, UUID targetId, Rank rank) {
        Team team = getTeam(senderId);
        if (team.setMemberRank(senderId, targetId, rank)) {
            database.insertMember(team.getUniqueId(), targetId, rank);
            return true;
        }
        return false;
    }

    public boolean destroyTeam(UUID senderId) {
        Team team = getTeam(senderId);
        if (team.destroyTeam(senderId)) {
            database.deleteTeam(senderId);
            return true;
        }
        return false;
    }

}
