/**
 * 
 */
package me.mattgd.startupcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
		 // Configuration
 		getConfig().options().copyDefaults(true);
 		saveConfig();
 		
 		FileConfiguration config = getConfig();
 		
 		if (config.getConfigurationSection("commands") == null) {
 			getLogger().info("There are no startup commands present.");
 		} else {
 			int delay = 0;
 			
 			for (String command : config.getConfigurationSection("commands").getKeys(false)) {
 				delay = config.getInt("commands." + command + ".delay");
 				
 	 			if (delay > -1) {
 	 				commands.add(new Command(command, delay));
 	 			} else {
 	 				commands.add(new Command(command));
 	 			}
 	 		}
 		}
 		
		getCommand("startup").setExecutor(this); // Setup commands
		getLogger().info("Enabled!");
		
		String queue = String.format("Queuing %d startup command%s.", commands.size(), commands.size() > 1 ? "s" : "");
		getLogger().info(queue);
		
		for (Command cmd : commands) {
			getServer().getScheduler().scheduleSyncDelayedTask(this, cmd, cmd.getDelay() * 20L);
		}
	}
	
	/**
     * Disable the Foundation plugin.
     */
	@Override
	public void onDisable() {       
        Bukkit.getScheduler().cancelAllTasks(); // Cancel scheduled tasks
		this.getConfig().options().copyDefaults(true);
		getLogger().info("Disabled!");
	}
	
	/**
	 * Call the appropriate command based on player command input, or
	 * show plugin information or reload the plugin if specified.
	 * @return true always
	 */
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("startup")) {
			if (args.length == 1) {
				//TODO: View commands
			} else if (args.length > 1) {
				//TODO: Add command
				//TODO: Remove command
			} else {
				//TODO: Display command help
			}
		}
		
		return true;
	}
	
}
