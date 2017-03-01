/**
 * 
 */
package me.mattgd.startupcommands;

import org.bukkit.Bukkit;

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
	
}
