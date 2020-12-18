package com.dormantmc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dormantmc.DormantMain;
import com.dormantmc.Util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /tempmute command.
 * 
 * @author Emily
 * @version 2020.07.07
 */
@CommandAlias("tempmute")
public class TempMuteCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public TempMuteCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Temporarily mutes a player.
     * 
     * @param player  Player muting.
     * @param other   Player to mute.
     * @param minutes Duration of mute in minutes.
     */
    @Default
    @Syntax("<user> <minutes>")
    @CommandCompletion("@players <minutes>")
    @CommandPermission("dormant.tempmute")
    @Description("Temporarily mute a player.")
    public void onTempMute(Player player, @Flags("other") Player other,
            @Conditions("limits:min=10,max=120") Integer minutes) {
        if (!plugin.getChatManager().tempMute(other, minutes)) {
            Util.sendMessage(player, "&c" + other.getName() + " is already muted.");
        } else {
            Util.sendMessage(player, "&aMuted " + other.getName() + " for " + minutes + " minutes.");
            player.sendMessage(ChatColor.RED + "You are muted for " + minutes + " minutes.");
        }
    }

}
