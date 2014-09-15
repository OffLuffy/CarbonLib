package me.offluffy.carbonlib;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Convenience class with several miscellaneous purposes
 * @author OffLuffy
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedDeclaration"})
public final class MiscUtils {
    private static Permission perms;
    /**
     * Sets the Permission object used for perm checking
     * @param p The Permission object provided by Vault
     */
    public static void setPerms(Permission p) { perms = p; }
	/**
	 * Fetch the Permission object used for perm checking
	 * @return Returns the cached Permission object
	 */
	public static Permission getPerms() { return perms; }
    /**
     * Checks if the Player has any of the listed perms
     * @param player The Player to check
     * @param permissions The list of perms to check
     * @return Returns true if the player has any of the perms
     */
    public static boolean perm(Player player, String ... permissions) {
        for (String p : permissions)
            if (perms != null)
                if (perms.playerHas(player, p))
                    return true;
        return false;
    }
	/**
	 * Checks if the Player has any of the listed perms
	 * @param player The Player to check
	 * @param permissions The list of perms to check
	 * @return Returns true if the player has any of the perms
	 */
	public static boolean perm(World w, OfflinePlayer player, String ... permissions) {
		for (String p : permissions)
			if (perms != null)
				if (perms.playerHas(w.getName(), player, p))
					return true;
		return false;
	}
    /**
     * Checks if the CommandSender has any of the listed perms
     * @param sender The CommandSender to check
     * @param permissions The list of perms to check
     * @return Returns true if the CommandSender has any of the perms
     */
    public static boolean perm(CommandSender sender, String ... permissions) {
        for (String p : permissions) {
			if (perms != null) {
				if (perms.has(sender, p))
					return true;
			} else {
				Log.severe("Permissions is null!");
			}
		}
        return false;
    }
	/**
	 * Broadcasts a message to all online players with the given permission
	 * @param perm The permission node that players must have to view the message
	 * @param msg The message to broadcast
	 */
	public static void permBroadcast(String perm, String msg) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (perm(p, perm))
				p.sendMessage(msg);
	}
	/**
	 * Broadcasts a message to all online players with the given permission
	 * @param perm The permission node that players must have to view the message
	 * @param avoid A List of &lt;? extends Player&gt; whom to not send the message to
	 * @param msg The message to broadcast
	 */
	public static void permBroadcast(String perm, List<? extends Player> avoid, String msg) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (perm(p, perm) && !avoid.contains(p))
				p.sendMessage(msg);
	}
    /**
     * Checks a String query against a list of Strings
     * @param query The string to check
     * @param matches The list of Strings to check the query against
     * @return Returns true if the query matches any String from matches (case-insensitive)
     */
    public static boolean eq(String query, String ... matches) {
		query.replace("_", "").replace("-", "");
        for (String s : matches) {
			s = s.replace("_", "").replace("-", "");
			if (query.equalsIgnoreCase(s))
				return true;
		}
        return false;
    }
	/**
	 * Checks a String query against a list of Strings
	 * @param query The string to check
	 * @param matches The list of Strings to check the query against
	 * @return Returns true if the query matches any String from matches (case-insensitive)
	 */
	public static boolean eq(String query, List<String> matches) { return eq(query, matches.toArray(new String[matches.size()])); }

	/**
	 * Checks an Object's equality to a list of other Objects
	 * @param obj The Object to compare
	 * @param query An array of Objects to compare the obj against
	 * @return Returns true if 'obj' is equal to any Object in the 'query' array
	 */
	public static <T> boolean objEq(T obj, T ... query) {
		for (T q : query) { if (obj.equals(q)) { return true; } }
		return false;
	}
    /**
     * Check if a number is a valid Integer
     * @param query The query to check
     * @return true if the query is capable of being casted to an int, false otherwise
     */
    public static boolean isInteger(String query) {
        try {
            Integer.parseInt(query);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    /**
     * Check if a number is a valid Long
     * @param query The query to check
     * @return true if the query is capable of being casted to a long, false otherwise
     */
    public static boolean isLong(String query) {
        try {
            Long.parseLong(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	/**
	 * Check if a number is a valid Double
	 * @param query The query to check
	 * @return true if the query is capable of being casted to a double, false otherwise
	 */
	public static boolean isDouble(String query) {
		try {
			Double.parseDouble(query);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
    /**
     * Check if a number is a valid Boolean
     * @param query The query to check
     * @return true if the query is capable of being casted to a boolean, false otherwise
     */
    public static boolean isBoolean(String query) {
		return (isInteger(query) && Integer.parseInt(query) > 0) ||
				eq(query, "on", "off", "1", "0", "true", "false", "enabled", "disabled", "enable", "disable",
						"allow", "deny", "yes", "no", "y", "n", "agree", "disagree");
    }
    /**
     * Parses a boolean more generously than Java's Boolean.toBoolean() method
     * @param query The query to check
     * @return true if the query is parsed as true, false otherwise or if un-parsable
     */
    public static boolean toBoolean(String query) {
        return isInteger(query) && Integer.parseInt(query) > 0 ||
                isBoolean(query) && eq(query, "on", "1", "true", "enabled", "enable", "allow", "yes", "y", "agree");
    }
	/**
	 * Forces a value to be within the min or max value, inclusive.
	 * @param val The value to normalize
	 * @param min The value that val must be greater than or equal to
	 * @param max The value that val must be lesser than or equal to
	 * @return Returns the min if value is less than min, returns the max if value is greather than max, returns val otherwise
	 */
	public static int normalizeInt(int val, int min, int max) {
		if (min > max) {
			int temp = min;
			min = max;
			max = temp;
		}
		return ( (val < min) ? min : ( (val > max) ? max : val ) );
	}
	/**
	 * Forces a value to be within the min or max value, inclusive.
	 * @param val The value to normalize
	 * @param min The value that val must be greater than or equal to
	 * @param max The value that val must be lesser than or equal to
	 * @return Returns the min if value is less than min, returns the max if value is greather than max, returns val otherwise
	 */
	public static double normalizeDouble(double val, double min, double max) {
		if (min > max) {
			double temp = min;
			min = max;
			max = temp;
		}
		return ( ( val < min) ? min : ( (val > max) ? max : val ) );
	}
	/**
	 * Short-hand method to capitalize the first letter of a String
	 * @param word The String to be capitalized
	 * @return The String with the first letter capitalized
	 */
	public static String capFirst(String word) { return (word.isEmpty()?"":(word.charAt(0)+"").toUpperCase())+((word.length()>1)?word.substring(1).toLowerCase():""); }
	/**
	 * Replaces all instances of key values in 'rep' in 'subject' with the associated values in the HashMap
	 * @param subject The String to search and replace within
	 * @param rep Pairings of key Strings (what to search for) and value Strings (what to replace it with)
	 * @return Returns the String 'subject' with all replacements
	 */
	public static String massReplace(String subject, HashMap<String, String> rep) {
		if (subject != null && !subject.isEmpty() && rep != null && !rep.isEmpty())
			for (String s : rep.keySet())
				if (s != null && rep.get(s) != null)
					subject = subject.replace(s, rep.get(s));
		return subject;
	}

	/**
	 * Short-hand method of forming a HashMap&lt;String, String&gt; from an array of strings (every other string being a key, the string after being the value)
	 * @param st The array of Strings to form into a HashMap&lt;String, String&gt;
	 * @return Returns a HashMap&lt;String, String&gt; filled with the given Strings
	 */
	public static HashMap<String,String> quickHash(String ... st) {
		HashMap<String,String> ret = new HashMap<String, String>();
		for (int i = 0; i < st.length; i+=2)
			if (st.length > i+1)
				ret.put(st[i], st[i+1]);
		return ret;
	}

	/**
	 * Short-hand method of forming a List&lt;?&gt; from an array of items
	 * @param stuff The items being translated into a List
	 * @return Returns a List of the specifiec objects
	 */
	public static <T> List<T> quickList(T ... stuff) {
		List<T> ret = new ArrayList<T>();
		Collections.addAll(ret, stuff);
		return ret;
	}
	/**
	 * Generates a random number
	 * @param min The lowest number allowed
	 * @param max The highest number allowed
	 * @return Returns a random int between the specified boundaries, inclusive
	 */
	public static int rand(int min, int max) { return min + (int)(Math.random() * ((max - min) + 1)); }
	/**
	 * Attempts to parse a material name or number to a Material. Can use Essentials's item aliases if it's installed
	 * @param mat The name or number to search for
	 * @return Returns a Material object if found, null otherwise
	 */
	@SuppressWarnings("deprecation")
	public static Material getMaterial(String mat) {
		if (MiscUtils.isInteger(mat)) {
			return Material.getMaterial(Integer.parseInt(mat));
		} else {
			if (Material.getMaterial(mat) != null)
				return Material.getMaterial(mat);
			if (Material.getMaterial(mat.toUpperCase()) != null)
				return Material.getMaterial(mat.toUpperCase());
			if (Material.getMaterial(mat.replace(" ", "_")) != null)
				return Material.getMaterial(mat.replace(" ", "_"));
		}
		if (checkPlugin("Essentials", true)) {
			com.earth2me.essentials.Essentials ess = (com.earth2me.essentials.Essentials) getPlugin("Essentials", true);
			try { return ess.getItemDb().get(mat).getType(); } catch (Exception e) { return null; }
		}
		return null;
	}

	/**
	 * Attempts to parse a enchantment name or number to an Enchantment. Can use Essentials's enchant aliases if it's initialized
	 * @param ench The name or number to search for
	 * @return Returns an Enchantment object if found, null otherwise
	 */
	@SuppressWarnings("deprecation")
	public static Enchantment getEnchant(String ench) {
		if (Enchantment.getByName(ench.toUpperCase()) != null) return Enchantment.getByName(ench);
		if (isInteger(ench) && Enchantment.getById(Integer.parseInt(ench)) != null) return Enchantment.getById(Integer.parseInt(ench));
		if (checkPlugin("Essentials", true)) { try { return com.earth2me.essentials.Enchantments.getByName(ench); } catch (Exception e) { return null; } }
		return null;
	}
	/**
	 * Checks if a plugin is installed and enabled
	 * @param pluginName The name of the plugin to check
	 * @return Returns true if the plugin is found and enabled, false otherwse
	 */
	public static boolean checkPlugin(String pluginName, boolean requireEnabled) {
		if (Bukkit.getPluginManager().getPlugin(pluginName) != null)
			if (!requireEnabled || Bukkit.getPluginManager().isPluginEnabled(pluginName))
				return true;
		return false;
	}
	/**
	 * Attempts to fetch the plugin if it's installed and enabled
	 * @param pluginName The name of the plugin to check
	 * @param requireEnabled Whether to require the plugin to be enabled to return the Plugin
	 * @return Returns the Plugin if the plugin is installed and enabled, null otherwise
	 */
	public static Plugin getPlugin(String pluginName, boolean requireEnabled) {
		if (Bukkit.getPluginManager().getPlugin(pluginName) != null)
			if (!requireEnabled || Bukkit.getPluginManager().isPluginEnabled(pluginName))
				return Bukkit.getPluginManager().getPlugin(pluginName);
		return null;
	}
}
