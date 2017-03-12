package me.mattgd.startupcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * StartupCommands plugin main class.
 * 
 * @author mattgd
 */
public class StartupCommands extends JavaPlugin {

	/** ArrayList of commands to run on startup/enable */
	private List<Command> commands = new ArrayList<Command>();
	
    /**
     * Enable the StartupCommand plugin.
     */
	@Override
	public void onEnable() {
 		saveDefaultConfig(); // Create default the configuration if config.yml doesn't exist
		
 		Command.loadCommands(this); // Load the startup command list
 		
		getCommand("startup").setExecutor(this); // Setup commands
		getLogger().info("Enabled!");
		
		runStartupCommands();
	}
	
	/**
     * Disable the StartupCommands plugin.
     */
	@Override
	public void onDisable() {       
        Bukkit.getScheduler().cancelAllTasks(); // Cancel scheduled tasks
		getConfig().options().copyDefaults(true);
		getLogger().info("Disabled!");
	}
	
	/**
	 * Call the appropriate command based on player command input, or
	 * show plugin information or reload the plugin if specified.
	 * @return true always
	 */
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		MessageManager msg = MessageManager.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("startup")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("view")) {
					String commandStr;
					
					if (commands.isEmpty()) {
						commandStr = "&eThere are currently no startup commands configured.";
					} else {
						commandStr = msg.messageTitle("Startup Commands", ChatColor.AQUA, ChatColor.YELLOW);
						
						int index = 1;
						for (Command command : commands) {
							commandStr += String.format("%n&e%s &7- &a%s &7(%ds delay)", index, command.getCommand(), command.getDelay());
							commandStr = commandStr.replaceAll("\\r", "");
							index++;
						}
						
						commandStr += msg.messageTrail(ChatColor.YELLOW); // Add message trail
					}
					
					MessageManager.getInstance().good(sender, commandStr); // Send the message to the sender
				} else if (args[0].equalsIgnoreCase("help")) {
					msg.good(sender, helpMessage());
				} else if (args[0].equalsIgnoreCase("run")) {
					runStartupCommands();
				} else {
					msg.severe(sender, "Invalid command usage. Type /startup help for proper usage information.");
				}
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("add")) {
					String cmdStr;
					int delay = 0;
					boolean hasDelay = false;
					
					// Check if the delay is an integer
					if (isInteger(args[1])) {
						delay = Integer.parseInt(args[1]);
						hasDelay = true;
					}
					
					if (args.length >= 2 && hasDelay) {
						cmdStr = msg.assembleMessage(args, 2, args.length);
					} else {
						cmdStr = msg.assembleMessage(args, 1, args.length);
					}
					
					try {
						Command command = new Command(cmdStr, delay);
						Command.addCommand(this, command);
						msg.good(sender, "Added startup command with delay " + delay + "s: " + cmdStr);
					} catch (IllegalArgumentException e) {
						msg.severe(sender, e.getMessage());
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					String removeStr = msg.assembleMessage(args, 1, args.length);
					
					try {
						msg.info(sender, "Removed startup command: &a" + Command.removeCommand(this, removeStr));
					} catch (IllegalArgumentException e) {
						msg.severe(sender, e.getMessage());
					}
				}
			} else {
				msg.good(sender, helpMessage());
			}
		}
		
		return true;
	}
	
	/**
	 * Returns a String with the StartupCommands help message.
	 * @return a String with the StartupCommands help message.
	 */
	private String helpMessage() {
		MessageManager msg = MessageManager.getInstance();
		String msgStr = msg.messageTitle("StartupCommands Help", ChatColor.AQUA, ChatColor.YELLOW);
		
		msgStr += "\n&a/startup view &7- &aview the active startup commands and their delay"
				+ "\n&a/startup add <command string> <delay> &7- &aadd a startup command"
				+ "\n&a/startup remove <exact command string> &7- &aremove a startup command";
		
		msgStr += msg.messageTrail(ChatColor.YELLOW); // Add message trail
		return msgStr;
	}
	
	/**
	 * Executes the commands in the commands ArrayList.
	 */
	private void runStartupCommands() {
		String queue = String.format("Queuing %d startup command%s.", commands.size(), commands.size() == 1 ? "" : "s");
		getLogger().info(queue);
		
		for (Command cmd : commands) {
			getServer().getScheduler().scheduleSyncDelayedTask(this, cmd, cmd.getDelay() * 20L);
		}
	}
	
	/**
	 * Returns the List of startup commands.
	 * @return the List of startup commands.
	 */
	public List<Command> getCommands() {
		return commands;
	}
	
	/**
	 * Returns true if the String s can be parsed as an integer, false otherwise.
	 * @param s The String to parse to an Integer.
	 * @return true if the String s can be parsed as an integer, false otherwise.
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
}
