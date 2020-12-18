package com.jacestratton.commands;

import com.jacestratton.DormantMain;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;

/**
 * Registers commands, argument tab comletions, and object-resolving contexts.
 * 
 * @author JaceStratton
 * @version 2020.07.07
 */
public class CommandManager extends PaperCommandManager {
    private final DormantMain plugin;

    /**
     * Registers all commands, completions, and contexts.
     * 
     * @param main Plugin's main class.
     */
    public CommandManager(DormantMain main) {
        super(main);
        plugin = main;

        // Registers completions, contexts, and commands in respective order.
        registerConditions();
        registerCompletions();
        registerContexts();
        registerCommands();
    }

    /**
     * Registers command argument conditions.
     */
    private void registerConditions() {
        getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("Min value must be " + c.getConfigValue("min", 0));
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 3) < value) {
                throw new ConditionFailedException("Max value must be " + c.getConfigValue("max", 3));
            }
        });
    }

    /**
     * Registers dynamic command tab completions.
     */
    private void registerCompletions() {
        // No completions to register.
    }

    /**
     * Registers command contexts.
     */
    private void registerContexts() {
        // No contexts to register.
    }

    /**
     * Registers commands.
     */
    private void registerCommands() {
        registerCommand(new FillXPCommand());
        registerCommand(new HomeCommand(plugin));
        registerCommand(new MeCommand(plugin));
        registerCommand(new MuteCommand(plugin));
        registerCommand(new NickCommand(plugin));
        registerCommand(new SpawnCommand(plugin));
        // registerCommand(new TeamCommand(plugin));
        registerCommand(new TeleportCommand(plugin));
        registerCommand(new TellCommand(plugin));
        registerCommand(new TempBanCommand());
        registerCommand(new TempMuteCommand(plugin));
        registerCommand(new UnmuteCommand(plugin));
        registerCommand(new WhoCommand(plugin));
    }

}
