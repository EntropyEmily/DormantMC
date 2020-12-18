package com.jacestratton;

import java.util.Collection;

import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event that fires when block broken is instance of Ageable.
 * 
 * @author JaceStratton
 * @version 2020.01.02
 */
public class AgeableBreakEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled = false;
    private final DormantMain plugin;
    private Block block;
    private ItemStack item;

    /**
     * Initializes block parameter and isCancelled to false.
     * 
     * @param main  Plugin's main class.
     * @param block Broken block.
     */
    public AgeableBreakEvent(DormantMain main, BlockBreakEvent event) {
        plugin = main;
        block = event.getBlock();
        item = event.getPlayer().getInventory().getItemInMainHand();
    }

    /**
     * @return True if event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets event to cancelled or not cancelled.
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    /**
     * @return HandlerList
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * @return HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * @return Block broken in event.
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return Collection<ItemStack> of drops
     */
    public Collection<ItemStack> getDrops() {
        return block.getDrops();
    }

    /**
     * @return boolean True if ageable broken with hoe, false otherwise.
     */
    public boolean usedFarmingTool() {
        return item.getType().name().contains("HOE");
    }

    /**
     * @return boolean True if ageable is max age, false otherwise.
     */
    public boolean isMaxAge() {
        Ageable ageable = (Ageable) block.getBlockData();
        return ageable.getAge() == ageable.getMaximumAge();
    }

    /**
     * @return True if ageable is crop as defined in config, false otherwise.
     */
    public boolean isCrop() {
        return plugin.getConfig().getStringList("crops").contains(block.getType().name());
    }

}
