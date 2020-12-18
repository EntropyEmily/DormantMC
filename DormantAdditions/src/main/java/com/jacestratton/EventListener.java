package com.jacestratton;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.jacestratton.users.UserManager;

/**
 * Listens for player events. Class needs separation.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
public class EventListener implements Listener {
    private final DormantMain plugin;
    private final int EXP_RATE;

    /**
     * @param main Plugin's main class.
     */
    public EventListener(DormantMain main) {
        plugin = main;
        EXP_RATE = plugin.getConfigManager().getConfig().getInt("experience.rate");
    }

    /**
     * On player joining, loads associated user and formats join message.
     * 
     * @param event Player joining event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserManager manager = plugin.getUserManager();

        manager.loadUser(player);

        String name = manager.getNickname(player).unformatted;
        event.setJoinMessage(event.getJoinMessage().replace(player.getName(), name));
    }

    /**
     * On player quitting, unloads associated user and formats quit message.
     * 
     * @param event Player quitting event.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UserManager manager = plugin.getUserManager();

        String name = manager.getNickname(player).unformatted;
        event.setQuitMessage(event.getQuitMessage().replace(player.getName(), name));
        
        manager.unloadUser(player);
    }

    /**
     * On player dying formats death message color and replaces player names.
     * 
     * @param event Player dying event.
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        UserManager manager = plugin.getUserManager();

        String victimName = manager.getNickname(victim).unformatted;
        String message = event.getDeathMessage().replace(victim.getName(), victimName);
        if (killer != null) {
            String killerName = manager.getNickname(killer).unformatted;
            message = message.replace(killer.getName(), killerName);
        }
        event.setDeathMessage(ChatColor.RED + message);
    }

    /**
     * On player chatting formats chat to add team prefix and nickname.
     * 
     * @param event Player chatting event.
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getChatManager().isMuted(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    /**
     * On experience changing, if positive, adjusts gain to fit flat rate model.
     * 
     * @param event Player changing experience event.
     */
    @EventHandler
    public void onExperience(PlayerExpChangeEvent event) {
        // Change must be positive (earn experience).
        if (event.getAmount() > 0) {
            float total = event.getPlayer().getExp() * EXP_RATE + event.getAmount();
            int levels = (int) total / EXP_RATE;
            float exp = total % EXP_RATE;
            event.getPlayer().setLevel(event.getPlayer().getLevel() + levels);
            event.getPlayer().setExp(exp / EXP_RATE);
            event.setAmount(0);
        }
    }

    /**
     * On throwing experience bottle, changes amount to drop one level.
     * 
     * @param event Throwing experience bottle event.
     */
    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        event.setExperience(EXP_RATE);
    }

    /**
     * On destroying block, determine if block is of type ageable.
     * 
     * @param event Destroying ageable block event.
     */
    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        // Checks if block is instance of ageable.
        if (event.getBlock().getBlockData() instanceof Ageable) {
            AgeableBreakEvent ageableBreakEvent = new AgeableBreakEvent(plugin, event);
            plugin.getServer().getPluginManager().callEvent(ageableBreakEvent);
            if (!ageableBreakEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * On destroying block, where block is instance of ageable, replants block
     * automatically.
     * 
     * @param event Destroying ageable block event.
     */
    @EventHandler
    public void onAgeableDestroy(AgeableBreakEvent event) {
        if (event.usedFarmingTool()) {
            if (event.isCrop() && event.isMaxAge()) {
                World world = event.getBlock().getWorld();

                // Simulates block drops.
                event.getDrops().forEach(n -> world.dropItemNaturally(event.getBlock().getLocation(), n));

                // Resets block's age.
                Ageable ageable = (Ageable) event.getBlock().getBlockData();
                ageable.setAge(0);
                event.getBlock().setBlockData(ageable);
            }
        } else {
            event.setCancelled(true);
        }
    }

}
