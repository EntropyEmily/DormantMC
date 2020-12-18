package com.dormant.commands;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dormant.Util;

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
 * Command class for /tempban command. Temporarily bans player.
 * 
 * @author Emily
 * @version 2020.07.07
 */
@CommandAlias("tempban")
public class TempBanCommand extends BaseCommand {

    /**
     * Default empty constructor.
     */
    public TempBanCommand() {
        // No fields to instantiate.
    }

    /**
     * Temporarily bans player.
     * 
     * @param player  Player executing command.
     * @param other   User to ban.
     * @param minutes Minutes to ban user.
     * @param reason  Reason of ban.
     */
    @Default
    @Syntax("<user> <minutes> <reason>")
    @CommandPermission("dormant.tempban")
    @CommandCompletion("@players <minutes>")
    @Description("Temporarily ban a player.")
    public void onTempBan(Player player, @Flags("other") Player other,
            @Conditions("limits:min=10,max=10080") Integer minutes, String reason) {
        if (other.getPlayer().isBanned()) {
            Util.sendMessage(player, "&c" + other.getName() + " is already banned.");
        } else {
            Date date = new Date();
            long time = date.getTime() + (60000 * minutes);
            date.setTime(time);
            other.getPlayer().banPlayer(ChatColor.WHITE + reason, date);
        }
    }

}
