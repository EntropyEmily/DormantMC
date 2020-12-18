package com.dormant.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dormant.DormantMain;
import com.dormant.Util;
import com.dormant.teams.Rank;
import com.dormant.teams.Team;
import com.dormant.teams.TeamManager;
import com.dormant.users.User;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /team command and team subcommands.
 * 
 * @author Emily
 * @version 2020.04.30
 */
@CommandAlias("team")
public class TeamCommand extends BaseCommand {
    private final DormantMain plugin;
    private TeamManager teamManager;

    public TeamCommand(DormantMain main) {
        plugin = main;
        teamManager = plugin.getTeamManager();
    }

    @Subcommand("create")
    @Syntax("<name>")
    @Conditions("team:false")
    @Description("Create a team.")
    public void createTeam(Player player, @Conditions("limits:min=3,max=20 | alphanumeric") String name) {
        if (teamManager.getTeam(name, true) != null) {
            Util.sendMessage(player, "&cTeam with that name already exists.");
        } else {
            teamManager.createTeam(player.getUniqueId(), name);
            Util.sendMessage(player, "&aTeam created.");
        }
    }

    @Subcommand("destroy")
    @Conditions("team:true")
    @Description("Destroy your team.")
    public void destroyTeam(Player player) {
        if (teamManager.destroyTeam(player.getUniqueId())) {
            Util.sendMessage(player, "&aTeam destroyed.");
        }
        Util.sendMessage(player, "&cRank not high enough.");
    }

    @Subcommand("invite")
    @Syntax("<user>")
    @Conditions("team:true")
    @Description("Invite user to your team.")
    public void inviteToTeam(Player player, @Flags("other") User other) {

    }

    @Subcommand("kick")
    @Syntax("<team member>")
    @CommandCompletion("@members")
    @Conditions("team:true")
    @Description("Kick your team member.")
    public void kickFromTeam(Player player, @Flags("other") User user) {
        if (teamManager.removeTeamMember(player.getUniqueId(), user.getUniqueId())) {
            Util.sendMessage(player, "&aUser kicked from team.");
            if (user.isOnline()) {
                Util.sendMessage(user.getPlayer(), "&cYou have been kicked from your team.");
            }
        }
    }

    @Subcommand("name")
    @Syntax("<name>")
    @Description("Change your team name. Will also change prefix.")
    public void setTeamName(Player player, String name, Team team) {
        if (Util.isAlphanumeric(name)) {
            if (name.length() <= 20 && name.length() >= 3) {
                if (teamManager.getTeam(name, true) == null) {
                    String prefix = name;
                    if (prefix.length() > 6) {
                        prefix = prefix.substring(0, 4) + "..";
                    }
                    if (teamManager.setTeamName(player.getUniqueId(), name, prefix)) {
                        Util.sendMessage(player, "&aTeam name changed.");
                    } else {
                        Util.sendMessage(player, "&cTeam rank not high enough.");
                    }
                } else {
                    Util.sendMessage(player, "&cTeam with that name already exists.");
                }
            } else {
                Util.sendMessage(player, "&cName must be 3 - 20 characters.");
            }
        } else {
            Util.sendMessage(player, "&cName must be alphanumeric.");
        }
    }

    @Subcommand("prefix")
    @Syntax("<prefix>")
    @Description("Change your team prefix.")
    public void setTeamPrefix(Player player, String prefix, Team team) {
        if (Util.isAlphanumeric(prefix)) {
            if (prefix.length() >= 1 && prefix.length() <= 6) {
                if (teamManager.setTeamPrefix(player.getUniqueId(), prefix)) {
                    Util.sendMessage(player, "&aTeam prefix changed.");
                } else {
                    Util.sendMessage(player, "&cTeam rank not high enough.");
                }
            } else {
                Util.sendMessage(player, "&cPrefix must be 1 - 6 characters.");
            }
        } else {
            Util.sendMessage(player, "&cPrefix must be alphanumeric.");
        }
    }

    @Subcommand("color")
    @Syntax("<color>")
    @CommandCompletion("@chat-colors")
    @Description("Set your team color.")
    public void setTeamColor(Player player, ChatColor color, Team team) {
        if (!color.isFormat()) {
            if (teamManager.setTeamColor(player.getUniqueId(), color)) {
                Util.sendMessage(player, "&aTeam color changed.");
            } else {
                Util.sendMessage(player, "&cTeam rank not high enough.");
            }
        } else {
            Util.sendMessage(player, "&cMust be color code, not format.");
        }
    }

    @Subcommand("promote")
    @Syntax("<team member>")
    @CommandCompletion("@members")
    @Description("Promote your team member.")
    public void promoteTeamMember(Player player, @Flags("other") User user, Team team) {
        if (team.contains(user.getUniqueId())) {
            Rank rank = team.getMemberRank(user.getUniqueId());
            if (teamManager.setTeamMemberRank(player.getUniqueId(), user.getUniqueId(), rank.next())) {
                Util.sendMessage(player, "&aUser promoted to " + rank.toString() + ".");
            } else {
                Util.sendMessage(player, "&cTeam rank not high enough.");
            }
        } else {
            Util.sendMessage(player, "&cUser not on your team.");
        }
    }

    @Subcommand("demote")
    @Syntax("<team member>")
    @CommandCompletion("@members")
    @Description("Demote your team member.")
    public void demoteTeamMember(Player player, @Flags("other") User user, Team team) {
        if (team.contains(user.getUniqueId())) {
            Rank rank = team.getMemberRank(user.getUniqueId());
            if (teamManager.setTeamMemberRank(player.getUniqueId(), user.getUniqueId(), rank.previous())) {
                Util.sendMessage(player, "&aUser demoted to " + rank.toString() + ".");
            } else {
                Util.sendMessage(player, "&cTeam rank not high enough.");
            }
        } else {
            Util.sendMessage(player, "&cUser not on your team.");
        }
    }

    @Subcommand("setwarp")
    @Description("Set team warp location.")
    public void setTeamWarp(Player player, Team team) {
        if (team.setWarp(player.getUniqueId(), player.getLocation())) {
            Util.sendMessage(player, "&aTeam warp set.");
        } else {
            Util.sendMessage(player, "&cTeam rank not high enough.");
        }
    }

    @Subcommand("warp")
    @Description("Teleport to team warp location.")
    public void teamWarp(Player player, Team team) {

    }

}
