package me.mattgd.startupcommands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Object container for a command that is run on startup.
 * 
 * @author mattgd
 */
public class Command implements Runnable {

	/** Default delay for commands constructed without the delay parameter, two seconds */
	private static final int DEFAULT_DELAY = 2;
	/** The command configuration prefix */
	private static final String CMD_CONFIG_PREFIX = "commands.";
	/** The command to execute */
	private String commandStr;
	/** The amount of time to delay the command after startup */
	private int delay;
	
	/**
	 * Constructs a new Command object with the specified command and delay.
	 * @param command the command the run
	 * @param delay the amount of seconds to wait after startup before running
	 * the command.
	 * @throws IllegalArgumentException if the command String is null
	 */
	public Command(String command, int delay) {
		if (command == null)
			throw new IllegalArgumentException("Command string cannot be null.");
		
		this.commandStr = command;
		this.delay = delay;
	}
	
	/**
	 * Constructs a new Command object with the specified command and a default delay.
	 * @param command the command the run
	 */
	public Command(String command) {
		this(command, DEFAULT_DELAY);
	}
	
	/**
	 * Returns the command String.
	 * @return the command String.
	 */
	public String getCommand() {
		return commandStr;
	}
	
	/**
	 * Returns the command delay in seconds.
	 * @return the command delay in seconds.
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Run the command.
	 */
	public void run() {
		Bukkit.getServer().getLogger().info("[StartupCommands] Executing command: " + commandStr);
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), getCommand());
	}
	
	/**
	 * Static method for adding a command to the commands List and the
	 * plugin configuration.
	 * @param plugin The StartupCommands instance.
	 * @param cmd The command to add.
	 * @throws IllegalArgumentException if the configuration cannot be saved.
	 */
	public static void addCommand(StartupCommands plugin, Command cmd) {
		plugin.getCommands().add(cmd); // Add Command to commands List
		
		FileConfiguration config = plugin.getConfig();
		
		config.set(CMD_CONFIG_PREFIX + cmd.getCommand() + ".delay", cmd.getDelay());
		
		try {
			config.save(plugin.getDataFolder() + File.separator + "config.yml");
		} catch (IOException e) {
			throw new IllegalArgumentException("Could not save configuration file.");
		}
	}
	
	/**
	 * Static method for removing a command from the configuration.
	 * @param plugin The StartupCommands instance.
	 * @param removeStr The String of the command to remove. If it is an Integer
	 * the command will be removed by index, otherwise, the method looks for a matching
	 * command String.
	 * @return removeStr The command String of the removed command.
	 * @throws IllegalArgumentException if the command doesn't exist, if the index provided 
	 * is less than zero or greater than commands.size() - 1, or if the configuration file 
	 * cannot be saved.
	 */
	public static String removeCommand(StartupCommands plugin, String removeStr) {
		FileConfiguration config = plugin.getConfig();

		// Find the command String if removeStr is an Integer
		if (StartupCommands.isInteger(removeStr)) {
			List<Command> commands = plugin.getCommands();
			int index = Integer.parseInt(removeStr) - 1;
			
			// Ensure index is valid
			if (index < 0 || index > commands.size() - 1) {
				throw new IllegalArgumentException("Index must be greater than 0 and less than the number of startup commands.");
			}
			
			removeStr = plugin.getCommands().remove(index).getCommand();
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
	 * @param plugin the StartupCommands plugin instance
	 */
	public static void loadCommands(StartupCommands plugin) {
		FileConfiguration config = plugin.getConfig();
 		
 		if (config.getConfigurationSection("commands") == null) {
 			plugin.getLogger().info("There are no startup commands present.");
 		} else {
 			int delay = 0;

 			for (String command : config.getConfigurationSection("commands").getKeys(false)) {
 				delay = config.getInt(CMD_CONFIG_PREFIX + command + ".delay", 0);
 				
 				// Try to create the command
 				try {
 					plugin.getCommands().add(new Command(command, delay));
 				} catch (IllegalArgumentException e) {
 					plugin.getLogger().severe(e.getMessage());
 				}
 	 		}
 		}
	}
	
}
