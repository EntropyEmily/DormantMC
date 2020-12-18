package com.dormantmc.commands;

import org.bukkit.entity.Player;

import com.dormantmc.DormantMain;
import com.dormantmc.TeleportRunnable;
import com.dormantmc.Util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;

/**
 * Command class for /spawn command. Teleports player to spawn.
 * 
 * @author Emily
 * @version 2020.04.18
 */
@CommandAlias("spawn")
public class SpawnCommand extends BaseCommand {
    private DormantMain plugin;

    /**
     * Default empty constructor.
     */
    public SpawnCommand(DormantMain main) {
        plugin = main;
    }

    /**
     * Teleports player to main overworld's spawn location.
     * 
     * @param player Command executor.
     */
    @Default
    @Description("Teleport to spawn.")
    public void onSpawn(Player player) {
        new TeleportRunnable(player, Util.getSpawn()).start(plugin);
    }

}
