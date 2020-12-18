package com.jacestratton.commands;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.jacestratton.DormantMain;
import com.jacestratton.TeleportRunnable;
import com.jacestratton.users.UserManager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * Teleport command to send, accept, or decline teleport requests.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
@CommandAlias("tpa")
public class TeleportCommand extends BaseCommand {
    private final DormantMain plugin;
    private Map<Player, SimpleEntry<Player, Integer>> requests = new HashMap<>();
    private final int TIMEOUT;

    /**
     * Constructs teleport command with given teleport timeout.
     * 
     * @param main Plugin's main class.
     */
    public TeleportCommand(DormantMain main) {
        plugin = main;
        TIMEOUT = plugin.getConfigManager().getTpTimeout();
    }

    /**
     * Sends teleport request message to recipient with clickable actions in text.
     * 
     * @param player Request sender.
     * @param other  Request recipient.
     */
    private void sendRequestMessage(Player player, Player other) {
        UserManager manager = plugin.getUserManager();

        ComponentBuilder message = new ComponentBuilder(
                manager.getNickname(player).unformatted + " wants to teleport to you. ")
                        .color(ChatColor.YELLOW.asBungee());
        message.append("[Accept]").color(ChatColor.GREEN.asBungee())
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + player.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/tpaccept " + player.getName())
                        .color(ChatColor.BLUE.asBungee()).create()));
        message.append(" ").append("[Decline]").color(ChatColor.RED.asBungee())
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdecline " + player.getName()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("/tpdecline " + player.getName()).color(ChatColor.BLUE.asBungee())
                                .create()));
        message.append("\nExpires in " + TIMEOUT + " seconds.").color(ChatColor.YELLOW.asBungee());
        other.sendMessage(message.create());
    }

    /**
     * Sends teleport request from one player to another.
     * 
     * @param player Request sender.
     * @param other  Request recipient.
     */
    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    @Description("Send a teleport request.")
    public void onTeleportRequest(Player player, @Flags("other") Player other) {
        if (requests.get(player) != null && requests.get(player).getKey().equals(other)) {
            player.sendMessage(ChatColor.RED + "Request already sent!");
        } else if (player.equals(other)) {
            player.sendMessage(ChatColor.RED + "Cannot teleport to yourself!");
        } else {
            requests.put(player, new SimpleEntry<Player, Integer>(other, (new BukkitRunnable() {

                // Removes request after TIMEOUT seconds.
                @Override
                public void run() {
                    requests.remove(player);
                }

            }.runTaskLater(plugin, (TIMEOUT * 20L)).getTaskId())));
            sendRequestMessage(player, other);
            player.sendMessage(ChatColor.YELLOW + "Request sent.");
        }
    }

    /**
     * Accepts teleport request.
     * 
     * @param player Request recipient.
     * @param other  Request sender.
     */
    @Subcommand("accept")
    @CommandAlias("tpaccept")
    @Syntax("<player>")
    @Description("Accept teleport request.")
    public void onTeleportAccept(Player player, @Flags("other") Player other) {
        UserManager manager = plugin.getUserManager();

        if (requests.get(other) == null || !requests.get(other).getKey().equals(player)) {
            player.sendMessage(ChatColor.RED + "No request from " + manager.getNickname(other).unformatted + ".");
        } else if (!((Entity) player).isOnGround()) {
            player.sendMessage(ChatColor.RED + "Must be on ground to accept request.");
        } else {
            plugin.getServer().getScheduler().cancelTask(requests.remove(other).getValue());
            player.sendMessage(ChatColor.GREEN + "Accepted teleport request.");
            other.sendMessage(
                    ChatColor.GREEN + manager.getNickname(player).unformatted + " accepted your teleport request.");
            new TeleportRunnable(other, player.getLocation()).start(plugin);
        }
    }

    /**
     * Declines teleport request.
     * 
     * @param player Request recipient.
     * @param other  Request sender.
     */
    @Subcommand("decline")
    @CommandAlias("tpdecline")
    @Description("Decline teleport request.")
    public void onTeleportDecline(Player player, @Flags("other") Player other) {
        UserManager manager = plugin.getUserManager();

        if (requests.get(other) == null || !requests.get(other).getKey().equals(player)) {
            player.sendMessage(ChatColor.RED + "No request from " + manager.getNickname(other).unformatted + ".");
        } else {
            plugin.getServer().getScheduler().cancelTask(requests.remove(other).getValue());
            player.sendMessage(ChatColor.RED + "Declined teleport request.");
            other.sendMessage(
                    ChatColor.RED + manager.getNickname(player).unformatted + " declined your teleport request.");
        }
    }

}
