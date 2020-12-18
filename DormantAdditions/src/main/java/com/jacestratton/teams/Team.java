package com.jacestratton.teams;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.jacestratton.Util;

public class Team {
    private UUID teamId;
    private String name, prefix;
    private ChatColor color;
    private Location warp;
    private Map<UUID, Rank> members;

    public Team(UUID teamId, String name, String prefix, ChatColor color, Location warp, Map<UUID, Rank> members) {
        this.teamId = teamId;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.warp = warp;
        this.members = members;
    }

    public Team(UUID senderId, String name) {
        this(UUID.randomUUID(), name, name, ChatColor.GRAY, Util.getSpawn(), new HashMap<>());
        members.put(senderId, Rank.OWNER);
        if (name.length() > 4) {
            prefix = name.substring(0, 4) + "..";
        }
    }

    public void addMember(UUID targetId) {
        members.put(targetId, Rank.MEMBER);
    }

    public boolean inviteMember(UUID senderId, UUID targetId) {
        if (members.get(senderId).atLeast(Rank.MOD)) {
            return true;
        }
        return false;
    }

    public boolean removeMember(UUID senderId, UUID targetId) {
        if (members.get(senderId).atLeast(Rank.OWNER)) {
            members.remove(targetId);
            return true;
        }
        return false;
    }

    public boolean setMemberRank(UUID senderId, UUID targetId, Rank rank) {
        if (members.get(senderId).atLeast(Rank.OWNER)) {
            members.put(targetId, rank);
            return true;
        }
        return false;
    }

    public boolean setName(UUID senderId, String name) {
        if (members.get(senderId).atLeast(Rank.ADMIN)) {
            this.name = name;
            return true;
        }
        return false;
    }

    public boolean setPrefix(UUID senderId, String prefix) {
        if (members.get(senderId).atLeast(Rank.ADMIN)) {
            this.prefix = prefix;
            return true;
        }
        return false;
    }

    public boolean setColor(UUID senderId, ChatColor color) {
        if (members.get(senderId).atLeast(Rank.ADMIN)) {
            this.color = color;
            return true;
        }
        return false;
    }

    public boolean setWarp(UUID senderId, Location warp) {
        if (members.get(senderId).atLeast(Rank.ADMIN)) {
            this.warp = warp;
            return true;
        }
        return false;
    }

    public boolean destroyTeam(UUID senderId) {
        if (members.get(senderId).atLeast(Rank.OWNER)) {
            return true;
        }
        return false;
    }

    public boolean contains(UUID playerId) {
        return members.get(playerId) != null;
    }

    public UUID getUniqueId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getColor() {
        return color;
    }

    public Location getWarp() {
        return warp;
    }

    public Set<UUID> getMemberIds() {
        return members.keySet();
    }

    public Rank getMemberRank(UUID playerId) {
        return members.get(playerId);
    }

    public Map<UUID, Rank> getMembers() {
        return members;
    }

    public String[] getProfile() {
        String[] profile = new String[members.size() + 1];
        profile[0] = "<------ " + color + name + ChatColor.WHITE + " ------>";
        int i = 1;
        for (UUID playerId : members.keySet()) {
            profile[i] = "> " + playerId;
        }
        return profile;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() == this.getClass()) {
            Team team = (Team) other;
            if (team.getUniqueId().equals(this.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnline() {
        for (UUID playerId : members.keySet()) {
            if (Bukkit.getOfflinePlayer(playerId).isOnline()) {
                return true;
            }
        }
        return false;
    }

}
