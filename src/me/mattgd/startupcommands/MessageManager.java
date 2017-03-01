package me.mattgd.startupcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageManager {
	
	/** The MessageManager instance */
	private static MessageManager instance = new MessageManager();
	
	/**
	 * Returns the MessageManager instance.
	 * @return the MessageManager instance.
	 */
	public static MessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Send msg message to the specified sender s in YELLOW.
	 * @param s the message recipient
	 * @param msg the message
	 */
	public void info(CommandSender s, String msg) {
		msg(s, ChatColor.YELLOW, msg);
	}
	
	/**
	 * Send msg message to the specified sender s in RED.
	 * @param s the message recipient
	 * @param msg the message
	 */
	public void severe(CommandSender s, String msg) {
		msg(s, ChatColor.RED, convertColor(msg));
	}
	
	/**
	 * Send msg message to the specified sender s in GREEN.
	 * @param s the message recipient
	 * @param msg the message
	 */
	public void good(CommandSender s, String msg) {
		msg(s, ChatColor.GREEN, convertColor(msg));
	}
	
	/**
	 * Send msg message in ChatColor color to the specified sender s.
	 * @param s the message recipient
	 * @param msg the message
	 */
	private void msg(CommandSender s, ChatColor color, String msg) {
		s.sendMessage(color + convertColor(msg));
	}
	
	/***
	 * Convert alternate color code (ampersand) to valid
	 * color codes.
	 * @param msg the message to convert
	 * @return the converted message
	 */
	private String convertColor(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	/**
	 * Calculates the amount of dashes needed to fill the line,
	 * and returns a message title line with the title in textColor
	 * surrounded in dashes in dashColor.
	 * @param title the title to display
	 * @param textColor the color of the title text
	 * @param dashColor the color of the dashes
	 * @return the title header
	 */
	public String messageTitle(String title, ChatColor textColor, ChatColor dashColor) {
		int leadTrailLength = (int) (53 - title.length()) / 2;
		String dashes = "";
		for (int count = 0; count < leadTrailLength; count++) dashes += "-";
	
		title = dashColor + dashes + "[" + textColor + title + dashColor + "]" + dashes + "\n";
		return convertColor(title);
	}
	
	/**
	 * Returns a trail of dashes with the specified color
	 * @param color the ChatColor of the dashes
	 * @return a line of dashes as a message footer
	 */
	public String messageTrail(ChatColor color) {
		String msg = color + "\n";
		for (int i = 0; i < 53; i++) {
			msg += "-";
		}
		
		return convertColor(msg);
	}
	
	/**
	 * Compile arguments from start to end into a String message.
	 * @param args the Array of Strings to compile
	 * @param start the start index in the Array
	 * @param end the end index in the Array. The message does not include
	 * the String at this index (works like substring())
	 * @return a String of all of the String arguments from start to end
	 */
	public String assembleMessage(String[] args, int start, int end) {
		String message = "";
		
		for (int i = start; i < end; i++) {
			message += args[i] + " ";
		}
		
		message = message.substring(0, message.length() - 1); // Remove trailing space
		return message;
	}
	
}
