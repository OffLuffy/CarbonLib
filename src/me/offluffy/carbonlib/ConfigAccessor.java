package me.offluffy.carbonlib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public final class ConfigAccessor {
	private final String fileName;
	private final JavaPlugin plugin;
	private File configFile;
	private FileConfiguration fileConfiguration;
	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param fileName The path to the file being accessed
	 */
	public ConfigAccessor(JavaPlugin plugin, String fileName) {
		if (plugin == null)
			throw new CarbonException("Plugin cannot be null");
		if (!plugin.isInitialized())
			throw new CarbonException("Plugin must be initialized");
		if (plugin.getDataFolder() == null)
			throw new IllegalStateException();
		this.plugin = plugin;
		this.fileName = fileName;
		this.configFile = new File(plugin.getDataFolder(), fileName);
		saveDefaultConfig();
		reloadConfig();
	}

	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param file The file being accessed
	 */
	public ConfigAccessor(JavaPlugin plugin, File file) {
		if (plugin == null)
			throw new CarbonException("Plugin cannot be null");
		if (!plugin.isInitialized())
			throw new CarbonException("Plugin must be initialized");
		this.plugin = plugin;
		this.fileName = file.getName();
		if (plugin.getDataFolder() == null)
			throw new IllegalStateException();
		this.configFile = file;
		saveDefaultConfig();
		reloadConfig();
	}
	/**
	 * Attempts to reload the FileConfiguration object associated with this instance of ConfigAccessor
	 * @see FileConfiguration
	 */
	public void reloadConfig() {
		if (exists())
			fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
		else {
			fileConfiguration = new YamlConfiguration();
			InputStream defConfigStream = plugin.getResource(fileName);
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				fileConfiguration.setDefaults(defConfig);
			}
		}
	}
	/**
	 * Returns the FileConfiguration object associated with this instance of ConfigAccessor
	 * @return A FileConfiguration object stored in this object
	 * @see FileConfiguration
	 */
	public FileConfiguration getConfig() {
		if (fileConfiguration == null)
			this.reloadConfig();
		return fileConfiguration;
	}
	/**
	 * Saves the current working config to the destination file
	 */
	public void saveConfig() {
		if (fileConfiguration != null && configFile != null) {
			try {
				getConfig().save(configFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not saveInvConfig config to " + configFile, ex);
			}
		} else {
			if (fileConfiguration == null)
				System.out.println("FileConfiguration object is null for " + fileName);
			if (configFile == null)
				System.out.println("Config file object is null for " + fileName);
		}
	}
	/**
	 * Saves a default copy of the file from the plugin jar to the destination file
	 */
	public void saveDefaultConfig() { if (!exists()) this.plugin.saveResource(fileName, false); }
	/**
	 * Indicates whether or not the config's destination file exists
	 * @return true if the destination file exists, false otherwise
	 */
	public boolean exists() { return configFile.exists(); }
}
