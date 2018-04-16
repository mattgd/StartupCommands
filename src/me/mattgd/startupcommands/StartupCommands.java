package me.mattgd.startupcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * StartupCommands plugin main class.
 * 
 * @author mattgd
 */
public class StartupCommands extends JavaPlugin {

    /** The CommandManager instance */
    private CommandManager cmdManager;
    /** Directory that the server is running from */
    private File serverDir = new File(System.getProperty("user.dir"));

	/**
	 * This is used for unit testing.
	 * @param loader The PluginLoader to use.
	 * @param description The Description file to use.
	 * @param dataFolder The folder that other data files can be found in.
	 * @param file The location of the plugin.
	 */
	public StartupCommands(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

    /**
     * Sets this server's root directory.
     * @param newServerDirectory The new server-root
     */
    public void setServerDirectory(File newServerDirectory) {
        if (!newServerDirectory.isDirectory())
            throw new IllegalArgumentException("That's not a folder!");

        this.serverDir = newServerDirectory;
    }

    /**
     * Enable the StartupCommand plugin.
     */
	@Override
	public void onEnable() {
 		saveDefaultConfig(); // Create default the configuration if config.yml doesn't exist
        cmdManager = new CommandManager(this);

		getCommand("startup").setExecutor(this); // Setup commands

        cmdManager.runStartupCommands(); // Run all startup commands
	}
	
	/**
     * Disable the StartupCommands plugin.
     */
	@Override
	public void onDisable() {
        getServer().getScheduler().cancelAllTasks(); // Cancel scheduled tasks
		getConfig().options().copyDefaults(true);
	}
	
	/**
	 * Call the appropriate command based on player command input, or
	 * show plugin information or reload the plugin if specified.
	 * @return true always.
	 */
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
		MessageManager msg = MessageManager.getInstance();
		
		if (cmd.getName().equalsIgnoreCase("startup")) {
			if (args.length == 1) {
			    String subCmd = args[0];

				if (subCmd.equalsIgnoreCase("view")) {
					StringBuilder commandList = new StringBuilder();
					
					if (cmdManager.getCommands().isEmpty()) {
						commandList.append("There are currently no startup commands configured.");
					} else {
						commandList.append(msg.messageTitle("Startup Commands", ChatColor.AQUA, ChatColor.YELLOW));
						
						int index = 1;
						String cmdStr;
						for (Command command : cmdManager.getCommands()) {
							cmdStr = String.format("%n&e%s &7- &a%s &7(%ds delay)", index, command.getCommand(), command.getDelay());
							cmdStr = cmdStr.replaceAll("\\r", "");
							commandList.append(cmdStr);
							index++;
						}
						
						commandList.append(msg.messageTrail(ChatColor.YELLOW)); // Add message trail
					}
					
					MessageManager.getInstance().info(sender, commandList.toString()); // Send the message to the sender
				} else if (subCmd.equalsIgnoreCase("help") || subCmd.equalsIgnoreCase("?")) {
					msg.good(sender, helpMessage());
				} else if (subCmd.equalsIgnoreCase("run")) {
					cmdManager.runStartupCommands();
				} else {
					msg.severe(sender, "Invalid command usage. Type /startup help for proper usage information.");
				}
			} else if (args.length > 1) {
                String subCmd = args[0];

				if (subCmd.equalsIgnoreCase("add") || subCmd.equalsIgnoreCase("create")) {
					String cmdStr;
					int delay;
					boolean hasDelay = false;
					
					// Check if a delay was provided
                    if (isInteger(args[1])) {
                        delay = Integer.parseInt(args[1]);
                        hasDelay = true;
                    } else {
                        delay = Command.DEFAULT_DELAY;
                    }

                    // Calculate where the commandString should start to be assembled
                    int start = 1 + (hasDelay ? 1 : 0);
					cmdStr = msg.assembleMessage(args, start, args.length);
					
					try {
						Command command = new Command(cmdStr, delay);
                        cmdManager.addCommand(command);
						msg.info(sender, "Added startup command with delay &7" + delay + "s&e: &a" + cmdStr);
					} catch (IllegalArgumentException e) {
						msg.severe(sender, e.getMessage());
					}
				} else if (subCmd.equalsIgnoreCase("remove") || subCmd.equalsIgnoreCase("delete")) {
					String removeStr = msg.assembleMessage(args, 1, args.length);
					
					try {
						msg.info(sender, "Removed startup command: &a" + cmdManager.removeCommand(removeStr));
					} catch (IllegalArgumentException e) {
						msg.severe(sender, e.getMessage());
					}
                } else if (subCmd.equalsIgnoreCase("setdelay")) {
                    // Check if an index and delay were provided
                    if (args.length != 3 || !(isInteger(args[1]) && isInteger(args[2]))) {
                        msg.severe(sender, "Usage: /sc setdelay <command ID> <delay in seconds>");
                    } else {
                        int index = Integer.parseInt(args[1]);
                        int delay = Integer.parseInt(args[2]);

                        try {
                            cmdManager.setCommandDelay(index, delay);
                            msg.info(sender, "Set the delay for command &7#" + index + " &eto &7" + delay + "s&e.");
                        } catch (IllegalArgumentException ex) {
                            msg.severe(sender, ex.getMessage());
                        }
                    }
                } else {
				    msg.severe(sender, "Invalid command usage. Type /startup help for proper usage information.");
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
		
		msgStr += "\n&a/sc view &7- &aview the active startup commands and their delay"
				+ "\n&a/sc add <command string> <delay> &7- &aadd a startup command"
				+ "\n&a/sc remove <command ID or exact command string> &7- &aremove a startup command"
                + "\n&a/sc setdelay <command ID> <delay in seconds> &7- &aset a startup command's delay";
		
		msgStr += msg.messageTrail(ChatColor.YELLOW); // Add message trail
		return msgStr;
	}

    /**
     * Returns true if the String s can be parsed as an integer, false otherwise.
     * @param s The String to parse to an Integer.
     * @return true if the String s can be parsed as an integer, false otherwise.
     */
    static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
	
}
