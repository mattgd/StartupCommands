package me.mattgd.startupcommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the adding, removing, and running of startup commands.
 *
 * @author mattgd
 */
public class CommandManager {

    /** The command configuration prefix */
    private static final String CMD_CONFIG_PREFIX = "commands.";
    /** The plugin StartupCommands instance */
    private StartupCommands plugin;
    /** ArrayList of commands to run on startup/enable */
    private List<Command> commands;

    /**
     * Constructs a new CommandManager instance.
     * @param plugin The StartupCommands instance.
     */
    CommandManager(StartupCommands plugin) {
        this.plugin = plugin;
        this.commands = new ArrayList<Command>();

        loadCommands(); // Load the startup command list
    }

    /**
     * Returns the List of startup commands.
     * @return the List of startup commands.
     */
    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Method for adding a command to the commands List and the
     * plugin configuration.
     * @param cmd The command to add.
     * @throws IllegalArgumentException if the configuration cannot be saved.
     */
    void addCommand(Command cmd) {
        commands.add(cmd); // Add Command to commands List

        FileConfiguration config = plugin.getConfig();

        config.set(CMD_CONFIG_PREFIX + cmd.getCommand() + ".delay", cmd.getDelay());

        try {
            config.save(plugin.getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save configuration file.");
        }
    }

    /**
     * Method for removing a command from the configuration.
     * @param removeStr The String of the command to remove. If it is an Integer
     * the command will be removed by index, otherwise, the method looks for a matching
     * command String.
     * @return removeStr The command String of the removed command.
     * @throws IllegalArgumentException if the command doesn't exist, if the index provided
     * is less than zero or greater than commands.size() - 1, or if the configuration file
     * cannot be saved.
     */
    String removeCommand(String removeStr) {
        FileConfiguration config = plugin.getConfig();

        // Find the command String if removeStr is an Integer
        if (StartupCommands.isInteger(removeStr)) {
            int index = Integer.parseInt(removeStr) - 1;

            // Ensure index is valid
            if (index < 0 || index > commands.size() - 1) {
                throw new IllegalArgumentException("Index must be greater than 0 and less than the number of startup commands.");
            }

            removeStr = commands.remove(index).getCommand();
        }

        if (config.contains(CMD_CONFIG_PREFIX + removeStr)) {
            config.set(CMD_CONFIG_PREFIX + removeStr, null);

            // Try to save configuration
            try {
                config.save(plugin.getDataFolder() + File.separator + "config.yml");
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not save configuration file.");
            }

            return removeStr;
        } else {
            throw new IllegalArgumentException("Could not identify command to remove by " + removeStr + ".");
        }
    }

    /**
     * Loads all of the startup commands from the plugin's configuration file.
     */
    private void loadCommands() {
        FileConfiguration config = plugin.getConfig();

        if (config.getConfigurationSection("commands") == null) {
            Bukkit.getLogger().info("There are no startup commands present.");
        } else {
            int delay;
            boolean notifyOnExec;

            for (String command : config.getConfigurationSection("commands").getKeys(false)) {
                delay = config.getInt(CMD_CONFIG_PREFIX + command + ".delay", Command.DEFAULT_DELAY);
                notifyOnExec = config.getBoolean(CMD_CONFIG_PREFIX + command + ".notify-on-exec", Command.DEFAULT_NOTIFY_ON_EXEC);

                // Try to create the command
                try {
                    commands.add(new Command(command, delay, notifyOnExec));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().severe(e.getMessage());
                }
            }
        }
    }

    /**
     * Executes the commands in the commands ArrayList.
     */
    void runStartupCommands() {
        String queue = String.format("Queuing %d startup command%s.", commands.size(), commands.size() == 1 ? "" : "s");
        Bukkit.getLogger().info(queue);

        for (Command cmd : commands) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, cmd, cmd.getDelay() * 20L);
        }
    }

}