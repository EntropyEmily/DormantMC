package com.dormant.commands;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.dormant.DormantMain;
import com.dormant.Util;
import com.dormant.users.UserManager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /tell command. Sends a private message to a player.
 * 
 * @author Emily
 * @version 2020.07.07
 */
@CommandAlias("tell|msg|w|whisper")
public class TellCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public TellCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Sends a private message to another player, along with a notification sound.
     * 
     * @param player  Command executor/player sending the message.
     * @param other   Player receiving the message.
     * @param message Message to be sent.
     */
    @Default
    @CommandCompletion("@players")
    @Syntax("<player> <message>")
    @Description("Message another user.")
    public void onMessage(Player player, @Flags("other") Player other, String message) {
        if (!plugin.getChatManager().isMuted(other)) {
            UserManager manager = plugin.getUserManager();

            Util.sendMessage(player, "&eTo &f" + manager.getNickname(other).formatted + "&e: " + message);
            Util.sendMessage(other, "&eFrom &f" + manager.getNickname(player).formatted + "&e: " + message);
            other.playSound(other.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
        }
    }

}
