package com.dormantmc.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dormantmc.DormantMain;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /who command. Retrieves user profile.
 * 
 * @author Emily
 * @version 2020.07.07
 */
@CommandAlias("who")
public class WhoCommand extends BaseCommand {
    private final DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public WhoCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Retrieves a profile with user info and stats.
     * 
     * @param player Command executor.
     * @param name   Name or partial name of user.
     */
    @Default
    @Syntax("<username|nickname>")
    @CommandCompletion("@players")
    @Description("Get a player profile.")
    public void onWho(Player player, @Single String name) {
        String profile = plugin.getUserManager().getProfile(name);
        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Profile not found.");
        } else {
            player.sendMessage(profile);
        }
    }

}
