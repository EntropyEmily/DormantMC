package com.jacestratton.commands;

import org.bukkit.entity.Player;

import com.jacestratton.DormantMain;
import com.jacestratton.TeleportRunnable;
import com.jacestratton.Util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;

/**
 * Command class for /home command. Teleports player to user's home location.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
@CommandAlias("home")
public class HomeCommand extends BaseCommand {
    private final DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public HomeCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Teleports player to home location.
     * 
     * @param player Command executor.
     */
    @Default
    @Description("Teleport home.")
    public void onHome(Player player) {
        new TeleportRunnable(player, plugin.getUserManager().getHome(player)).start(plugin);
    }

    /**
     * Sets home location to player's current location.
     * 
     * @param player Command executor.
     */
    @Subcommand("set")
    @CommandAlias("sethome")
    @Description("Set home.")
    public void onSetHome(Player player) {
        plugin.getUserManager().setHome(player);
        Util.sendMessage(player, "&aHome set.");
    }

}
