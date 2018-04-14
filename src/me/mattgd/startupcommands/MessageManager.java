package me.mattgd.startupcommands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageManager {
	
	/** Message title/trail dash length */
	private static final int DASH_LENGTH = 53;
	/** The MessageManager instance */
	private static MessageManager instance = new MessageManager();
	
	/**
	 * Returns the MessageManager instance.
	 * @return the MessageManager instance.
	 */
	static MessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Send msg message to the specified sender s in YELLOW.
	 * @param s the message recipient
	 * @param msg the message
	 */
	void info(CommandSender s, String msg) {
		msg(s, ChatColor.YELLOW, msg);
	}
	
	/**
	 * Send msg message to the specified sender s in RED.
	 * @param s the message recipient
	 * @param msg the message
	 */
	void severe(CommandSender s, String msg) {
		msg(s, ChatColor.RED, convertColor(msg));
	}
	
	/**
	 * Send msg message to the specified sender s in GREEN.
	 * @param s the message recipient
	 * @param msg the message
	 */
	void good(CommandSender s, String msg) {
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
	String messageTitle(String title, ChatColor textColor, ChatColor dashColor) {
		int leadTrailLength = (DASH_LENGTH - title.length()) / 2;
		StringBuilder dashes = new StringBuilder();
		
		for (int i = 0; i < leadTrailLength; i++) {
			dashes.append("-");
		}
	
		title = dashColor + dashes.toString() + "[" + textColor + title + dashColor + "]" + dashes.toString();
		return title;
	}
	
	/**
	 * Returns a trail of dashes with the specified color
	 * @param color the ChatColor of the dashes
	 * @return a line of dashes as a message footer
	 */
	String messageTrail(ChatColor color) {
		StringBuilder trail = new StringBuilder(color + "\n");
		
		for (int i = 0; i < DASH_LENGTH; i++) {
			trail.append("-");
		}
		
		return convertColor(trail.toString());
	}
	
	/**
	 * Compile arguments from start to end into a String message.
	 * @param args the Array of Strings to compile
	 * @param start the start index in the Array
	 * @param end the end index in the Array. The message does not include
	 * the String at this index (works like substring())
	 * @return a String of all of the String arguments from start to end
	 */
	String assembleMessage(String[] args, int start, int end) {
		args = Arrays.copyOfRange(args, start, end);
		return String.join(" ", args);
	}
	
}
