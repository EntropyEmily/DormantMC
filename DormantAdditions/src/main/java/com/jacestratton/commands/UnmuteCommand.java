package com.jacestratton.commands;

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
import net.md_5.bungee.api.ChatColor;

/**
 * Command class for /unmute command. Unmutes a player.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
@CommandAlias("unmute")
public class UnmuteCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public UnmuteCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Unmutes a player.
     * 
     * @param player Player unmuting.
     * @param other  Player being unmuted.
     */
    @Default
    @Syntax("<user>")
    @CommandCompletion("@players")
    @CommandPermission("dormant.unmute")
    @Description("Unmute a player.")
    public void onUnmute(Player player, @Flags("other") Player other) {
        plugin.getChatManager().unmute(player);
        Util.sendMessage(player, "&aUnmuted " + other.getName() + ".");
        other.sendMessage(ChatColor.GREEN + "You are no longer muted.");
    }

}
