package net.teamcarbon.carbonlib.Misc;

import net.milkbowl.vault.permission.Permission;
import net.teamcarbon.carbonlib.CarbonLib;
import net.teamcarbon.carbonlib.Misc.Messages.Clr;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static org.bukkit.Material.*;

/**
 * Convenience class with several miscellaneous purposes
 * @author OffLuffy
 */
@SuppressWarnings({"UnusedDeclaration"})
public final class MiscUtils {

	// Materials that allow a player to walk through them
	private static final Material[] passThroughMats = new Material[]{
			AIR, SAPLING, POWERED_RAIL, DETECTOR_RAIL, LONG_GRASS, DEAD_BUSH, YELLOW_FLOWER, RED_ROSE, BROWN_MUSHROOM,
			RED_MUSHROOM, TORCH, REDSTONE_WIRE, SEEDS, SIGN_POST, WOODEN_DOOR, LADDER, RAILS, WALL_SIGN, LEVER, STONE_PLATE,
			IRON_DOOR_BLOCK, WOOD_PLATE, REDSTONE_TORCH_OFF, REDSTONE_TORCH_ON, STONE_BUTTON, SNOW, SUGAR_CANE_BLOCK,
			DIODE_BLOCK_OFF, DIODE_BLOCK_ON, PUMPKIN_STEM, MELON_STEM, VINE, FENCE_GATE, WATER_LILY, NETHER_WARTS,
			CARPET, ACTIVATOR_RAIL
	};
	// Of the Materials that are pass-through, ones that allow the player to stand on them and covers whole square
	// TODO Check if these can be negated from passThroughMats later (depending on use)
	private static final Material[] supportingMats = new Material[]{
			POWERED_RAIL, RAILS, SNOW, CARPET, ACTIVATOR_RAIL, DETECTOR_RAIL
	};
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
            if (perms != null && player != null && p != null)
                if (perms.playerHas(player, p))
                    return true;
        return false;
    }
	/**
	 * Checks if the Player has any of the listed perms
	 * @param w The World to check permissions in
	 * @param player The Player to check
	 * @param permissions The list of perms to check
	 * @return Returns true if the player has any of the perms
	 */
	public static boolean perm(World w, OfflinePlayer player, String ... permissions) {
		for (String p : permissions)
			if (perms != null && player != null && p != null)
				if (perms.playerHas(w.getName(), player, p))
					return true;
		return false;
	}
	/**
	 * Checks if the Player has any of the listed perms, assuming the default world if the player isn't online
	 * @param player The Player to check
	 * @param permissions The list of perms to check
	 * @return Returns true if the player has any of the perms, false if not or if the World is somehow null
	 */
	public static boolean perm(OfflinePlayer player, String ... permissions) { return perm(player, null, permissions); }
	/**
	 * Checks if the Player has any of the listed perms, assuming the default world if the player isn't online
	 * @param player The Player to check
	 * @param world The World to fetch the Player's permissions for
	 * @param permissions The list of perms to check
	 * @return Returns true if the player has any of the perms, false if not or if the World is somehow null
	 */
	public static boolean perm(OfflinePlayer player, World world, String ... permissions) {
		if (world == null) world =(player.isOnline() ? ((Player) player).getWorld() : Bukkit.getWorlds().get(0));
		return world != null && perm(world, player, permissions);
	}
    /**
     * Checks if the CommandSender has any of the listed perms
     * @param sender The CommandSender to check
     * @param permissions The list of perms to check
     * @return Returns true if the CommandSender has any of the perms
     */
    public static boolean perm(CommandSender sender, String ... permissions) {
        for (String p : permissions) {
			if (perms != null && sender != null && p != null) {
				if (perms.has(sender, p))
					return true;
			} else {
				CarbonLib.log.severe("Permissions is null!");
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
	 * Sends a message to the specified Player if they have the specified permission
	 * @param pl The Player to send the message to if they have permission
	 * @param msg The Message to send to the Player if they have permission
	 * @param perm The permission node to check before sending the message
	 */
	public static void permSend(Player pl, String perm, String msg) { if (perm(pl, perm)) pl.sendMessage(msg); }
	/**
	 * Sends a message to the specified CommandSender if they have the specified permission
	 * @param s The CommandSender to send the message to if they have permission
	 * @param msg The Message to send to the CommandSender if they have permission
	 * @param perm The permission node to check before sending the message
	 */
	public static void permSend(CommandSender s, String perm, String msg) { if (perm(s, perm)) s.sendMessage(msg); }
    /**
     * Checks a String query against a list of Strings
     * @param query The string to check
     * @param matches The list of Strings to check the query against
     * @return Returns true if the query matches any String from matches (case-insensitive)
     */
    public static boolean eq(String query, String ... matches) {
		query = query.replace("_", "").replace("-", "");
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
	 * Checks if the query starts with any of the provided Strings in matches (case-insensitive)
	 * @param query The String to check
	 * @param matches The Strings to check if 'query' starts with
	 * @return Returns true if query starts with (case-insensitive) any of the matches, false otherwise
	 */
	public static boolean starts(String query, String ... matches) { for (String s : matches) if (query.toLowerCase().startsWith(s.toLowerCase())) return true; return false; }
	/**
	 * Checks an Object's equality to a list of other Objects. Uses == for Enums.
	 * @param obj The Object to compare
	 * @param query An array of Objects to compare the obj against
	 * @return Returns true if 'obj' is equal to any Object in the 'query' array
	 */
	public static <T> boolean objEq(T obj, T ... query) {
		for (T q : query) { if ((obj.getClass().isEnum() && obj == q) || obj.equals(q)) { return true; } }
		return false;
	}
	/**
	 * Checks if an object is exactly equal (==) to any other objects in 'query'
	 * @param obj The initial object to compare to the list of objects
	 * @param query The list of objects to be compared to the initial object
	 * @return Returns true if 'obj' is exactly equal (== comparison) to any object in 'query'
	 */
	public static <T> boolean exactEq(T obj, T ... query) {
		for (T b : query) { if (obj == b) return true; }
		return false;
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
	 * Attempts to parse a material name or number to a Material. Can use Essentials's item aliases if it's installed
	 * @param mat The name or number to search for
	 * @return Returns a Material object if found, null otherwise
	 */
	@SuppressWarnings({"deprecation"})
	public static Material getMaterial(String mat) {
		if (TypeUtils.isInteger(mat)) {
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
			try {
				return ess == null ? null : ess.getItemDb().get(mat).getType();
			} catch (Exception e) { return null; }
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
		if (TypeUtils.isInteger(ench) && Enchantment.getById(Integer.parseInt(ench)) != null) return Enchantment.getById(Integer.parseInt(ench));
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
	/*/**
	 * Attempts to remove a recipe
	 * @param r The Recipe to remove
	 */
	/*public static void removeRecipe(Recipe r) {
		if (CraftingManager.getInstance().getRecipes().contains(r))
			CraftingManager.getInstance().getRecipes().remove(r);
	}*/
	/*/**
	 * Attempts to remove a list of recipes <i>This doesn't work yet</i>
	 * @param r The List of Recipes to remove
	 */
	/*public static void removeRecipes(List<Recipe> r) {
		Iterator<Recipe> it = Bukkit.recipeIterator();
		while (it.hasNext()) {
			Recipe itr = it.next();
			if (itr != null && r.contains(itr))
				it.remove();
		}
	}*/

	/**
	 * Attempts to fetch an OfflinePlayer based on a String name or UUID
	 * @param query The username or UUID to search for
	 * @param matchOffline Whether or not to attempt to search through offline players
	 * @return Returns an OfflinePlayer if found, false otherwise
	 */
	public static OfflinePlayer getPlayer(String query, boolean matchOffline) {
		// TODO Improve this?
		for (Player p : Bukkit.getOnlinePlayers()) {
			try { if (p.getName().equalsIgnoreCase(query) || p.getUniqueId().equals(UUID.fromString(query))) return p;
			} catch (Exception e) { /*(new CarbonException(e)).printStackTrace();*/ }
		}
		if (matchOffline) {
			for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
				try {
					if (p.getName().equalsIgnoreCase(query) || p.getUniqueId().equals(UUID.fromString(query)))
						return p;
				} catch (Exception e) { /*(new CarbonException(e)).printStackTrace();*/ }
			}
		}
		return null;
	}
	/**
	 * Attempts to fetch an OfflinePlayer based on a String name or UUID
	 * @param id The UUID to search for
	 * @param matchOffline Whether or not to attempt to search through offline players
	 * @return Returns an OfflinePlayer if found, false otherwise
	 */
	public static OfflinePlayer getPlayer(UUID id, boolean matchOffline) {
		if (matchOffline) { return Bukkit.getPlayer(id); } else {
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (pl.getUniqueId().equals(id)) return pl;
			}
		}
		return null;
	}
	/**
	 * Teleports an Entity to a Location while maintaining passengers and pre-loads chunks
	 * @param ent The Entity to teleport
	 * @param loc The Location to teleport the entity to
	 */
	public static void teleport(Entity ent, Location loc) {
		if (ent.isDead()) return;
		final List<Entity> passengers = new ArrayList<Entity>();
		loadChunks(loc, 3);
		if (ent.getPassenger() != null) {
			Entity v = ent;
			while (v.getPassenger() != null) {
				passengers.add(v.getPassenger());
				v = v.getPassenger();
				v.eject();
			}
		}
		if (ent.isInsideVehicle()) ent.getVehicle().eject();
		ent.teleport(loc);
		if (!passengers.isEmpty()) {
			Entity last = ent;
			for (Entity e : passengers) {
				if (e == null) continue;
				e.teleport(loc);
				last.setPassenger(e);
				last = e;
			}
		}
	}
	/**
	 * Loads chunks in the given radius around the given Location
	 * @param location The Location around which to load chunks
	 * @param radius The radius around the Location in which to load chunks
	 */
	public static void loadChunks(Location location, final int radius) {
		for (int cx = (int)(location.getX() / 16.0) - radius; cx <= (int)(location.getX() / 16.0) + radius; cx++) {
			for (int cz = (int)(location.getZ() / 16.0) - radius; cz <= (int)(location.getZ() / 16.0) + radius; cz++) {
				location.getWorld().getChunkAt(cx, cz);
			}
		}
	}
	/**
	 * Attempts to add a String to a String list in a FileConfiguration (Doesn't save the file!)
	 * @param fc The FileConfiguration to modify
	 * @param path The path to the String list
	 * @param value The value to add to the String list (Uses Object's toString() method)
	 */
	public static boolean addToStringList(FileConfiguration fc, String path, Object value) {
		boolean added = false;
		List<String> list = fc.contains(path)?fc.getStringList(path):new ArrayList<String>();
		if (!list.contains(value.toString())) { list.add(value.toString()); added = true; }
		fc.set(path, list);
		return added;
	}
	/**
	 * Attempts to remove a String to a String list in a FileConfiguration (Doesn't save the file!)
	 * @param fc The FileConfiguration to modify
	 * @param path The path to the String list
	 * @param value The value to remove from the String list (Uses Object's toString() method)
	 */
	public static boolean removeFromStringList(FileConfiguration fc, String path, Object value) {
		boolean removed = false;
		List<String> list = fc.contains(path)?fc.getStringList(path):new ArrayList<String>();
		if (list.contains(value.toString())) { list.remove(value.toString()); removed = true; }
		fc.set(path, list);
		return removed;
	}

	/**
	 * Attempts to parse out each color/format code individually based on the permissions of the user
	 * @param msg The message to parse colors in
	 * @param p The Player whose permissions will be checked
	 * @param perm The permission node that will be appended with specific nodes like .color.red or .format.bold
	 * @return Returns the message that's parsed, doesn't parse codes the user doesn't have permission for
	 */
	public static String permColorParse(String msg, Player p, String perm) {
		// Add the trailing dot if there isn't one (since I'll be adding code-specific perm nodes on later)
		if (!perm.endsWith(".")) perm += ".";
		// If they have all perms, replace everything and return it
		if (perm(p, perm+"color.*") && perm(p, perm+"format.*")) return Clr.trans(msg);
		// Otherwise loop through each code and check for individual codes
		for (ChatColor cc : ChatColor.values()) {
			// If a color code, check if user has perm to change all colors, if not then check color.colorname
			if (cc.isColor() && (perm(p, perm+"color.*") || perm(p, perm+"color."+cc.name().toLowerCase())))
				msg = msg.replaceAll("&"+cc.getChar(), "\u00A7"+cc.getChar());
			// If a format code, check if user has perm to change all formats, if not then check format.formatname
			if (cc.isFormat() && (perm(p, perm+"format.*") || perm(p, perm+"format."+cc.name().toLowerCase())))
				msg = msg.replaceAll("&"+cc.getChar(), "\u00A7"+cc.getChar());
		}
		return msg;
	}

	/**
	 * Concatenates the array of Strings togther with the specified delimiter between each String.
	 * This method will use Strings from the array from index 'fromIndex' to index 'toIndex'
	 * @param array The array of Objects to concatenate together
	 * @param delimiter The String to place between each String that's combined
	 * @param fromIndex The index of the array to start from (inclusive)
	 * @param toIndex The index of the array to end (inclusive)
	 * @return Returns the concatenated Strings as a single String. Returns an empty string if array is null
	 */
	public static String stringFromSubArray(String delimiter, int fromIndex, int toIndex, Object... array) {
		if (array == null) return "";
		if (fromIndex < 0) { fromIndex = 0; }
		if (toIndex > array.length-1) { toIndex = array.length-1; }
		String newString = "";
		boolean first = true;
		for (int i = fromIndex; i <= toIndex; i++) {
			String word = array[i] == null ? "" : array[i].toString();
			if (array[i] instanceof String) word = (String) array[i];
			else if (array[i] instanceof Player) word = ((Player) array[i]).getName();
			else if (array[i] instanceof World) word = ((World) array[i]).getName();
			newString += (first ? "" : delimiter) + word;
			first = false;
		}
		return newString;
	}

	/**
	 * Concatenates the array of Strings togther with the specified delimiter between each String.
	 * This method will use String froms the array from index 'fromIndex' to the end of the array
	 * @param array The array of Strings to concatenate together
	 * @param delimiter The String to place between each String that's combined
	 * @param fromIndex The index of the array to start from (inclusive)
	 * @return Returns the concatenated Strings as a single String. Returns an empty string if array is null
	 */
	public static String stringFromSubArray(String delimiter, int fromIndex, Object... array) {
		return stringFromSubArray(delimiter, fromIndex, array.length - 1, array);
	}

	/**
	 * Concatenates the array of Strings togther with the specified delimiter between each String.
	 * This method will use all the Strings from the array
	 * @param array The array of Strings tos concatenate together
	 * @param delimiter The String to place between each String that's combined
	 * @return Returns the concatenated Strings as a single String. Returns an empty string if array is null
	 */
	public static String stringFromArray(String delimiter, Object... array) {
		return stringFromSubArray(delimiter, 0, array.length - 1, array);
	}

	/**
	 * Checks if the Material allows a user to pass through it
	 * @param mat The Material to check
	 * @return Returns true if the Material allows a user to pass through it, false otherwise
	 */
	public static boolean isHollow(Material mat) {
		return Arrays.asList(passThroughMats).contains(mat);
	}

	/**
	 * Checks if the Material allws a user to stand on it
	 * @param mat The Material to check
	 * @return Returns true if the Material allows the user to stand on it, false otherwise
	 */
	public static boolean isSupporting(Material mat) {
		return !isHollow(mat) || Arrays.asList(supportingMats).contains(mat);
	}
}
