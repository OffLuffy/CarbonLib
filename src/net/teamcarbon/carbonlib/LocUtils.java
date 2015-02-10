package net.teamcarbon.carbonlib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;

@SuppressWarnings("UnusedDeclaration")
public final class LocUtils {

	public static HashMap<Player, Location> lastLoc = new HashMap<Player, Location>();

	/**
	 * Removes all entries from the cache of player locations used to simplify movement listeners
	 */
	public static void resetLastLocs() { if (lastLoc == null) lastLoc = new HashMap<Player, Location>(); else lastLoc.clear(); }

	/**
	 * Indicates if the point specified is within the boundaries indicated with the lesser and greater corners
	 * @param point The Location to check if being inside the boundaries
	 * @param lesserCorner The lower boundary Location
	 * @param greaterCorner The greater boundary Location
	 * @return Returns true if the point is within the specified boundary Locations
	 */
	public static boolean isInside(Location point, Location lesserCorner, Location greaterCorner) {
		return (point.getWorld().equals(lesserCorner.getWorld())
				&& point.getX() > lesserCorner.getX() && point.getX() < greaterCorner.getX()
				&& point.getY() > lesserCorner.getY() && point.getY() < greaterCorner.getY()
				&& point.getZ() > lesserCorner.getZ() && point.getZ() < greaterCorner.getZ());
	}

	/**
	 * Checks to see if the two Locations specified have the same XYZ coordinates (including decimals)
	 * @param l1 The first Location to compare against the second
	 * @param l2 The second Location to compare against the first
	 * @return Returns true if the World, x, y, and z locations are exactly the same between both Locations
	 */
	public static boolean isSameLoc(Location l1, Location l2) {
		return l1.getWorld().equals(l2.getWorld())
				&& l1.getX() == l2.getX() && l1.getY() == l2.getY() && l1.getZ() == l2.getZ();
	}

	/**
	 * Checks to see if the two Locations specified have the same XYZ block coordinates (truncates decimals)
	 * @param l1 The first Location to comapre against the second
	 * @param l2 The second Location to compare against the first
	 * @return Return strue if the World, x, y, and z block locations are the same
	 */
	public static boolean isSameBlockLoc(Location l1, Location l2) {
		return l1.getWorld().equals(l2.getWorld())
				&& l1.getBlockX() == l2.getBlockX()
				&& l1.getBlockY() == l2.getBlockY()
				&& l1.getBlockZ() == l2.getBlockZ();
	}

	/**
	 * Attempts to fetch a Location point within the boundaries specified
	 * @param lesserBounds The lower boundary
	 * @param greaterBounds The greater boundary
	 * @param playerLoc A Location (inside or outside the boundaries) to re-adjust to be within then boundaries
	 * @return Returns a Location that resides within the boundary Locations, or the same Location if it is already
	 */
	public static Location getInsideLocation(Location lesserBounds, Location greaterBounds, Location playerLoc) {
		Location loc = playerLoc.clone();
		if (playerLoc.getBlockX() > greaterBounds.getBlockX()) loc.setX(greaterBounds.getBlockX() - 1);
		if (playerLoc.getBlockX() < lesserBounds.getBlockX()) loc.setX(lesserBounds.getBlockX() + 1);
		if (playerLoc.getBlockY() > greaterBounds.getBlockY()) loc.setY(greaterBounds.getBlockY() - 1);
		if (playerLoc.getBlockY() < lesserBounds.getBlockY()) loc.setY(lesserBounds.getBlockY() + 1);
		if (playerLoc.getBlockZ() > greaterBounds.getBlockZ()) loc.setZ(greaterBounds.getBlockZ() - 1);
		if (playerLoc.getBlockZ() < lesserBounds.getBlockZ()) loc.setZ(lesserBounds.getBlockZ() + 1);
		return loc;
	}

	/**
	 * Formats a Location in a String format for easier storage. Can be parsed with the fromStr() method
	 * @param loc The Location to format as String
	 * @param blockCoords Whether or not to treat the Location like Block coordinates (truncates decimals)
	 * @return Returns a String of the given Location. Values that fail to parse are replaced with defaults.
	 * This should only return null if the Location fails to format (there are no worlds on the server).
	 */
	public static String toStr(Location loc, boolean blockCoords) {
		World w = loc!=null?loc.getWorld():null;
		if (w == null) { try { w = Bukkit.getWorlds().get(0); } catch (Exception e) {} }
		if (w == null) {
			CarbonLib.log.warn("Failed to format Location! There doesn't appear to be any worlds on this server");
			return null;
		}
		double x = 0, y = 64, z = 0;
		float yaw = 0, pitch = 0;
		if (loc != null) {
			x = blockCoords?loc.getBlockX():loc.getX();
			y = blockCoords?loc.getBlockX():loc.getY();
			z = blockCoords?loc.getBlockX():loc.getZ();
			yaw = loc.getYaw();
			pitch = loc.getPitch();
		} else { CarbonLib.log.debug("Location being formattec is null. Using default values."); }
		return w.getName() + "," + x + "," + y + "," + z + "," + yaw + "," + pitch;
	}

	/**
	 * Attempts to parse a Location from a String following the format from the toStr() method
	 * @param locStr The String representing the Location
	 * @return Returns a Location. If values fail to parse, they are replaced with default values,
	 * or null if it fails to parse (which should only happen if there's somehow no worlds)
	 */
	public static Location fromStr(String locStr) {
		String[] s = locStr.split(",");
		World w = Bukkit.getWorld(s[0]);
		if (w == null) { try { w = Bukkit.getWorlds().get(0); } catch (Exception e) {} }
		if (w == null) {
			CarbonLib.log.warn("Failed to parse Location! There doesn't appear to be any worlds on this server");
			return null;
		}
		double x = 0, y = 64, z = 0;
		float yaw = 0, pitch = 0;
		try { x = Double.parseDouble(s[1]); } catch (Exception e) {}
		try { y = Double.parseDouble(s[2]); } catch (Exception e) {}
		try { z = Double.parseDouble(s[3]); } catch (Exception e) {}
		try { yaw = Float.parseFloat(s[4]); } catch (Exception e) {}
		try { pitch = Float.parseFloat(s[5]); } catch (Exception e) {}
		return new Location(w, x, y, z, yaw, pitch);
	}

	/**
	 * Calculates the distance between the two locations. This calculation ignores World differences, uses only x,y,z.
	 * @param l1 The first Location
	 * @param l2 The second Location
	 * @return Returns a double indicating the distance between the two points specified, or -1 if either Location is null
	 */
	public static double distance(Location l1, Location l2) {
		if (l1 == null || l2 == null) return -1;
		double dx = l1.getX() - l2.getX();
		double dy = l1.getY() - l2.getY();
		double dz = l1.getZ() - l2.getZ();
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
}
