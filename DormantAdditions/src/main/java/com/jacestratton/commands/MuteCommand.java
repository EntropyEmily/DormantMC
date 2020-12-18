package com.jacestratton.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.jacestratton.DormantMain;
import com.jacestratton.Util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /mute command. Mutes player until restart or unmute.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
@CommandAlias("mute")
public class MuteCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public MuteCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Mutes a player.
     * 
     * @param player Player muting.
     * @param other  Player to mute.
     */
    @Default
    @Syntax("<user>")
    @CommandCompletion("@players")
    @CommandPermission("dormant.mute")
    @Description("Mute a player.")
    public void onMute(Player player, @Flags("other") Player other) {
        String name = plugin.getUserManager().getNickname(other).unformatted;

        if (plugin.getChatManager().isMuted(other)) {
            Util.sendMessage(player, "&c" + name + " is already muted.");
        } else {
            plugin.getChatManager().mute(other);
            player.sendMessage(ChatColor.GREEN + "Muted " + name + ".");
            other.sendMessage(ChatColor.RED + "You have been muted.");
        }
    }

}
