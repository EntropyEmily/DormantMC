package com.dormantmc;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.dormantmc.commands.CommandManager;
import com.dormantmc.teams.TeamManager;
import com.dormantmc.users.UserManager;

/**
 * Main class of plugin. Initializes and houses managers for different sections.
 * 
 * @author Emily
 * @version 2020.07.07
 */
public class DormantMain extends JavaPlugin {
    private ConfigurationManager configManager;
    private ChatManager chatManager;
    private UserManager userManager;
    private TeamManager teamManager;
    private CommandManager commandManager;

    /**
     * On enabling plugin on server startup, intializes managers and systems.
     */
    @Override
    public void onEnable() {
        // Config manager for editing values and enabling/disabling plugin sections.
        configManager = new ConfigurationManager(this);

        // Chat manager to handle sending of public and private messages.
        chatManager = new ChatManager(this);

        // Event listener to handle player-invoked events.
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new EventListener(this), this);

        // User manager for storing additional data such as name and home location.
        userManager = new UserManager(this);

        // Team manager for grouping players together.
        teamManager = new TeamManager(this);

        // Command manager for initializing commands, completions, and contexts.
        commandManager = new CommandManager(this);

        // Displays successful initialization message to console.
        getLogger().info("DormantAdditions is online!");
    }

    /**
     * On disabling plugin on server shutdown, closes connections and open
     * processes.
     */
    @Override
    public void onDisable() {
        // Displays shutdown message to console.
        getLogger().info("DormantAdditions is offline.");
    }

    /**
     * @return ConfigurationManager.
     */
    public ConfigurationManager getConfigManager() {
        return configManager;
    }

    /**
     * @return ChatManager.
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * @return UserManager.
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * @return TeamManager.
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * @return CommandManager.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

}
