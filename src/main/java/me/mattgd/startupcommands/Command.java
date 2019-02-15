package me.mattgd.startupcommands;

import org.bukkit.Bukkit;

/**
 * Object container for a command that is run on startup.
 * 
 * @author mattgd
 */
public class Command implements Runnable {

    /** Default delay for commands constructed without the delay parameter */
    static final int DEFAULT_DELAY = 2;
    /** Default notifyOnExec setting */
    static final boolean DEFAULT_NOTIFY_ON_EXEC = true;
	/** The command to execute */
	private String commandStr;
	/** The amount of time to delay the command after startup */
	private int delay;
	/** Boolean value for whether or not to notify the console on command execution */
	private boolean notifyOnExec;
	
	/**
	 * Constructs a new Command object with the specified command, delay, and notifyOnExec parameters.
	 * @param command the command the run
	 * @param delay the amount of seconds to wait after startup before running the command.
     * @param notifyOnExec the boolean value for whether or not to notify the console on command execution.
	 * @throws IllegalArgumentException if the command String is null
	 */
	public Command(String command, int delay, boolean notifyOnExec) {
        setCommand(command);
		setDelay(delay);
		this.notifyOnExec = notifyOnExec;
	}

    /**
     * Constructs a new Command object with the specified command and delay.
     * @param command the command the run
     * @param delay the amount of seconds to wait after startup before running the command.
     */
    public Command(String command, int delay) {
        this(command, delay, DEFAULT_NOTIFY_ON_EXEC);
    }
	
	/**
	 * Constructs a new Command object with the specified command and a default delay.
	 * @param command the command the run
	 */
	public Command(String command) {
		this(command, DEFAULT_DELAY, DEFAULT_NOTIFY_ON_EXEC);
	}
	
	/**
	 * Returns the command String.
	 * @return the command String.
	 */
	public String getCommand() {
		return commandStr;
	}

    /**
     * Sets the commandStr value.
     * @param cmdStr The commandStr value to set.
     * @throws IllegalArgumentException if cmdStr is null or empty.
     */
    void setCommand(String cmdStr) {
		if (cmdStr == null || cmdStr.isEmpty()) {
			throw new IllegalArgumentException("Command string cannot be null or empty.");
		}

        this.commandStr = cmdStr;
    }

	/**
	 * Returns the command delay in seconds.
	 * @return the command delay in seconds.
	 */
	int getDelay() {
		return delay;
	}

    /**
     * Sets the command delay in seconds.
     * @param delay The delay value to set.
     */
	void setDelay(int delay) {
        this.delay = delay < 0 ? 0 : delay;
    }

	/**
	 * Run the command.
	 */
	public void run() {
	    if (notifyOnExec) {
            Bukkit.getServer().getLogger().info("[StartupCommands] Executing command: " + commandStr);
        }

		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), getCommand());
	}

}
