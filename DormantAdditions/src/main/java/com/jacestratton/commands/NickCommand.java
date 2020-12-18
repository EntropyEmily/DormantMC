package com.jacestratton.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.jacestratton.DormantMain;
import com.jacestratton.Util;
import com.jacestratton.users.Nickname;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /nick command. Changes nickname displayed in chat.
 * 
 * @author JaceStratton
 * @version 2020.04.30
 */
@CommandAlias("nick")
public class NickCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public NickCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Changes nickname of player as is displayed in chat.
     * 
     * @param player   Command executor.
     * @param nickname New nickname. Can include format/color codes.
     */
    @Default
    @Syntax("<nickname>")
    @Description("Change your nickname.")
    public void onNickChange(Player player, @Single String nick) {
        // Changes string nickname to Nickname object.
        Nickname nickname = new Nickname(nick);

        // If nickname meets requirements and is not taken, nickname changes.
        if (nickname.unformatted.length() < 3 || nickname.unformatted.length() > 16) {
            Util.sendMessage(player, "&cMust be between 3 and 16 characters.");
        } else if (containsFormatCodes(nick)) {
            Util.sendMessage(player, "&cColor codes only. Nickname contains format codes.");
        } else if (!Util.isAlphanumeric(nickname.unformatted)) {
            Util.sendMessage(player, "&cAlphanumeric characters and underscore only.");
        } else if (!plugin.getUserManager().setNickname(player, nickname)) {
            Util.sendMessage(player, "&cNickname or username taken.");
        } else {
            Util.sendMessage(player, "&aNickname changed to " + nickname.formatted + ".");
        }

    }

    /**
     * Resets nickname of player.
     * 
     * @param player Command executor.
     */
    @Subcommand("reset")
    @Description("Reset your nickname.")
    public void onNickReset(Player player) {
        plugin.getUserManager().setNickname(player, new Nickname(player.getName()));
        Util.sendMessage(player, "&aNickname reset.");
    }

    /**
     * Determines if string contains format codes (excluding color codes).
     * 
     * @param plaintextNick Nickname containing format codes before translation.
     * @return True if nickname contains format codes, false otherwise.
     */
    private boolean containsFormatCodes(String plaintextNick) {
        // Loop ends 1 character short to avoid IndexOutOfBounds execption.
        for (int i = 0; i < plaintextNick.length() - 1; i++) {
            if (plaintextNick.charAt(i) == '&') {
                ChatColor code = ChatColor.getByChar(plaintextNick.charAt(i + 1));
                if (code != null && code.isFormat()) {
                    return true;
                }
            }
        }
        return false;
    }

}
