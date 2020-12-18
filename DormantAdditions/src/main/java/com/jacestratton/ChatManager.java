package com.jacestratton;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Manages the sending and receiving of private and public messages.
 * 
 * @author JaceStratton
 * @version 2020.18.04
 */
public class ChatManager {
    private Map<UUID, Set<UUID>> ignores = new LinkedHashMap<>();
    private Map<UUID, BukkitTask> mutes = new LinkedHashMap<>();
    private DormantMain plugin;

    /**
     * @param main Plugin's main class.
     */
    public ChatManager(DormantMain main) {
        plugin = main;
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PlayerChatPacketAdapter(main));
        manager.addPacketListener(new ServerChatPacketAdapter(main));
    }

    /**
     * Ignores player for player ignoring.
     * 
     * @param player Player ignoring.
     * @param other  Player getting ignored.
     */
    public void ignore(Player player, Player other) {
        if (ignores.get(other.getUniqueId()) == null) {
            ignores.put(other.getUniqueId(), new LinkedHashSet<UUID>());
        }
        ignores.get(other.getUniqueId()).add(player.getUniqueId());
    }

    /**
     * Unignores player for player who previously ignored.
     * 
     * @param player Player unignoring.
     * @param other  Player getting unignored.
     */
    public void unignore(Player player, Player other) {
        Set<UUID> list = ignores.get(other.getUniqueId());
        if (list != null) {
            list.remove(player.getUniqueId());
        }
    }

    /**
     * @param player Player to check ignores of.
     * @param other  Player to check ignores by.
     * @return True if other is ignoring player, false otherwise.
     */
    public boolean isIgnoring(Player player, Player other) {
        Set<UUID> list = ignores.get(player.getUniqueId());
        if (list != null && list.contains(other.getUniqueId())) {
            return true;
        }
        return false;
    }

    /**
     * Mutes player for all.
     * 
     * @param player Player to mute.
     */
    public void mute(Player player) {
        mutes.put(player.getUniqueId(), null);
    }

    /**
     * Temporarily mute a player for a given amount of minutes.
     * 
     * @param player  Player to mute.
     * @param minutes Minutes to mute player for.
     * @return True if player does not already have ban, false otherwise.
     */
    public boolean tempMute(Player player, int minutes) {
        if (mutes.get(player.getUniqueId()) == null) {
            mutes.put(player.getUniqueId(), new BukkitRunnable() {

                // Unmutes player after given minutes.
                @Override
                public void run() {
                    mutes.remove(player.getUniqueId());
                }

            }.runTaskLater(plugin, minutes * 1200L));
            return true;
        }
        return false;
    }

    /**
     * Unmutes player for all players.
     * 
     * @param player Player to unmute.
     */
    public void unmute(Player player) {
        BukkitTask task = mutes.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * @param player Player to check for mute.
     * @return True if player is muted, false otherwise.
     */
    public boolean isMuted(Player player) {
        if (mutes.containsKey(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    /**
     * Clears all mutes of and by player.
     * 
     * @param player Player to clear.
     */
    public void clearMutesAndIgnores(Player player) {
        // Removes ignores from player.
        for (UUID other : ignores.keySet()) {
            Set<UUID> list = ignores.get(other);
            if (list != null) {
                list.remove(player.getUniqueId());
            }
        }
    }

    // Private classes for intercepting incoming and outgoing chat packets.

    /**
     * Packet adapter to intercept chat packets sent by player.
     * 
     * @author JaceStratton
     * @version 2020.18.04
     */
    private class PlayerChatPacketAdapter extends PacketAdapter {

        /**
         * Constructs packet adatper with player chat packet.
         * 
         * @param main Plugin's main class.
         */
        public PlayerChatPacketAdapter(DormantMain main) {
            super(main, ListenerPriority.NORMAL, PacketType.Play.Client.CHAT);
        }

        /**
         * Intercepts chat packets sent by player to server. Stores player as last
         * message sender. Enforces mutes.
         * 
         * @param event Sending of chat packet to server event.
         */
        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (isMuted(event.getPlayer())) {
                event.setCancelled(true);
            }
        }

    }

    /**
     * Intercepts chat packets sent by server.
     * 
     * @author JaceStratton
     * @version 2020.18.04
     */
    private class ServerChatPacketAdapter extends PacketAdapter {

        /**
         * Constructs packet adapter with server chat packet.
         * 
         * @param main Plugin's main class.
         */
        public ServerChatPacketAdapter(DormantMain main) {
            super(main, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT);
        }

        /**
         * Intercepts chat packets sent from server to players. Enforces player ignores.
         * 
         * @param event Sending of chat packet to player event.
         */
        @Override
        public void onPacketSending(PacketEvent event) {

        }

    }

}
