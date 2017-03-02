/**
 * 
 */
package me.mattgd.startupcommands;

import java.io.File;
import java.io.IOException;

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
	/** The command to execute */
	private String command;
	/** The amount of time to delay the command after startup */
	private int delay;
	
	/**
	 * Contructs a new Command object with the specified command and delay.
	 * @param command the command the run
	 * @param delay the amount of seconds to wait after startup before running
	 * the command.
	 */
	public Command(String command, int delay) {
		this.command = command;
		this.delay = delay;
	}
	
	/**
	 * Contructs a new Command object with the specified command and a default delay.
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
		return command;
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
		Bukkit.getServer().getLogger().info("[StartupCommands] Executing command: " + getCommand());
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), getCommand());
	}
	
	/**
	 * Static method for adding a command to the commands List and the
	 * plugin configuration.
	 * @param command the command to add
	 */
	public static void addCommand(StartupCommands plugin, Command cmd) {
		plugin.getCommands().add(cmd); // Add Command to commands List
		
		FileConfiguration config = plugin.getConfig();
		
		config.set("commands." + cmd.getCommand() + ".delay", cmd.getDelay());
		
		try {
			config.save(plugin.getDataFolder() + File.separator + "config.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Static method for removing a command from the configuration.
	 * @param command the command to remove
	 */
	public static boolean removeCommand(StartupCommands plugin, String cmdStr) {
		FileConfiguration config = plugin.getConfig();
		
		if (config.contains("commands." + cmdStr)) {
			config.set("commands." + cmdStr, null);
			
			// Try to save configuration
			try {
				config.save(plugin.getDataFolder() + File.separator + "config.yml");
			} catch (IOException e) {
				return false;
			}
			
			return true;
		} else {
			return false;
		}
	}
	
}
