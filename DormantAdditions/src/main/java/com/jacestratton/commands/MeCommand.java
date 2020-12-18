package com.jacestratton.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.jacestratton.DormantMain;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /me command.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
@CommandAlias("me")
public class MeCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public MeCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Displays action message (ex: "*Steve123 fed a cow live TNT.*")
     * 
     * @param player  Command executor.
     * @param message Action message to display in chat.
     */
    @Default
    @Syntax("<message>")
    @Description("Display message in chat. (ex: *Steve123 fed a cow live TNT.*)")
    public void onMe(Player player, String message) {
        String name = plugin.getUserManager().getNickname(player).unformatted;
        message = ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "* " + name + " " + message + " *";
        plugin.getServer().broadcastMessage(message);
    }

}
