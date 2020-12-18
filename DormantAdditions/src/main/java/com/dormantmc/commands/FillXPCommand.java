package com.dormantmc.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.dormantmc.Util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;

/**
 * Command class for /fillxp or /bottlexp command. Converts experience levels to
 * bottles o' xp.
 * 
 * @author Emily
 * @version 2020.04.14
 */
@CommandAlias("fillxp|bottlexp")
public class FillXPCommand extends BaseCommand {

    /**
     * Default empty constructor.
     */
    public FillXPCommand() {
        // No fields to instantiate.
    }

    /**
     * Fills bottles in player's inventory with their experience.
     * 
     * @param player Command executor.
     * @param amount Desired amount of levels to convert.
     */
    @Default
    @Syntax("<amount>")
    @Description("Fill bottles with experience.")
    public void onFillXP(Player player, int amount) {
        if (amount > 0 && player.getLevel() >= amount) {
            if (Util.clear(player, Material.GLASS_BOTTLE, amount)) {
                Util.give(player, Material.EXPERIENCE_BOTTLE, amount);
                player.giveExpLevels(-amount);
                Util.sendMessage(player, "&aFilled " + amount + " bottles with xp.");
            } else
                Util.sendMessage(player, "&cNot enough bottles.");
        } else
            Util.sendMessage(player, "&cOnly enough xp for " + player.getLevel() + " bottles. ");
    }

}
