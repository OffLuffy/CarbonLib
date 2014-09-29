package org.teamcarbon.carbonlib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnusedDeclaration")
public final class Log {

	private static JavaPlugin pl;
	private static String debugPath;

	/**
	 * Convenience class to enable easy logging and colored ouput
	 * @param pl A JavaPlugin object necessary to fetch names and configs
	 * @param debugPath The config path of a boolean value indicating if debug logging is enabled
	 */
	public Log(JavaPlugin pl, String debugPath) {
		Log.pl = pl;
		Log.debugPath = debugPath;
	}

	/**
	 * Represents a preset message severity level
	 */
	public enum Level {
		INFO('a'), WARN('6'), SEVERE('4'), DEBUG('d');
		private char c = 'f';
		Level(char color) {
			if ((c+"").matches("^[0-9a-fA-Fk-oK-O]$"))
				c = color;
		}
		public String c() { return ChatColor.getByChar(c)+""; }
	}
	/**
	 * Log a message with INFO level (Green text)
	 * @param msg The message to log
	 * @see Log.Level
	 */
	public static void info(String msg) { log(msg, Level.INFO); }
	/**
	 * Log a message with WARN level (Orange text)
	 * @param msg The message to log
	 * @see Log.Level
	 */
	public static void warn(String msg) { log(msg, Level.WARN); }
	/**
	 * Log a message with SEVERE level (Red text)
	 * @param msg The message to log
	 * @see Log.Level
	 */
	public static void severe(String msg) { log(msg, Level.SEVERE); }
	/**
	 * Log a message with DEBUG level (Magenta text), only prints if dubug is enabled in config
	 * @param msg The message to log
	 * @see Log.Level
	 */
	public static void debug(String msg) { if (isDebugEnabled()) log(msg, Level.DEBUG); }
	/**
	 * Log a message with no log level or color
	 * @param msg The message to log
	 */
	public static void log(String msg) {
		String name = "";
		if (pl != null)
			name = "[" + pl.getDescription().getName() + "] ";
		Bukkit.getConsoleSender().sendMessage(name + msg);
	}
	/**
	 * Log a message with the specified log level
	 * @param msg The Message to log
	 * @param level The Level which the message will be logged
	 * @see Log.Level
	 */
	private static void log(String msg, Level level) {
		String name = "";
		if (pl != null)
			name = pl.getDescription().getName() + " : ";
		Bukkit.getConsoleSender().sendMessage(level.c() + "[" + name + level.name() + "] " + msg);
	}
	/**
	 * @return Returns true if the plugin is not null and the specified debug config path returns true, false otherwise
	 */
	public static boolean isDebugEnabled() { return (pl != null) && pl.getConfig().getBoolean(debugPath, false); }
}
