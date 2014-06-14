package me.offluffy.carbonlib;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Convenience object with several miscellaneous purposes
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedDeclaration"})
public final class MiscUtils {
    private static Permission perms;

    /**
     * Sets the Permission object used for perm checking
     * @param p The Permission object provided by Vault
     */
    public static void setPerms(Permission p) {
        perms = p;
    }
    /**
     * Checks if the Player has any of the listed perms
     * @param player The Player to check
     * @param permissions The list of perms to check
     * @return Returns true if the player has any of the perms
     */
    public static boolean perm(Player player, String ... permissions) {
        for (String p : permissions)
            if (perms != null)
                if (perms.has(player, p))
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
        for (String p : permissions)
            if (perms != null)
                if (perms.has(sender, p))
                    return true;
        return false;
    }
    /**
     * Checks a String query against a list of Strings
     * @param query The string to check
     * @param matches The list of Strings to check the query against
     * @return Returns true if the query matches any String from matches (case-insensitive)
     */
    public static boolean eq(String query, String ... matches) {
        for (String s : matches)
            if (query.equalsIgnoreCase(s))
                return true;
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
     * Check if a number is a valid Boolean
     * @param query The query to check
     * @return true if the query is capable of being casted to a boolean, false otherwise
     */
    public static boolean isBoolean(String query) {
        try {
            Boolean.parseBoolean(query);
            return true;
        } catch (Exception e) {
            return isInteger(query) && Integer.parseInt(query) > 0 ||
                    eq(query, "on", "off", "1", "0", "true", "false", "enabled", "disabled", "enable", "disable", "allow", "deny");
        }
    }
    /**
     * Parses a boolean more generously than Java's Boolean.toBoolean() method
     * @param query The query to check
     * @return true if the query is parsed as true, false otherwise or if un-parsable
     */
    public static boolean toBoolean(String query) {
        return isInteger(query) && Integer.parseInt(query) > 0 ||
                isBoolean(query) && eq(query, "on", "1", "true", "enabled", "enable", "allow");
    }
}
