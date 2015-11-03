package net.teamcarbon.carbonlib.Misc;

import net.teamcarbon.carbonlib.CarbonLib;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

@SuppressWarnings("unused")
public final class ConfigAccessor {
	private String fileName, defaultResource;
	private JavaPlugin plugin;
	private File configFile;
	private FileConfiguration yamlConf;
	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param fileName The path to the file being accessed
	 */
	public ConfigAccessor(JavaPlugin plugin, String fileName) { init(plugin, fileName, null, null); }
	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param file The file being accessed
	 */
	public ConfigAccessor(JavaPlugin plugin, File file) { init(plugin, null, file, null); }
	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param fileName The path to the file being accessed
	 * @param defResource The name of the resource (stored in the plugin jar) to save as default config if file doesn't exist
	 */
	public ConfigAccessor(JavaPlugin plugin, String fileName, String defResource) { init(plugin, fileName, null, defResource); }
	/**
	 * Convenience object to assist with handling FileConfigurations other than the plugin's primary config file
	 * @param plugin The JavaPlugin using the ConfigAccessor
	 * @param file The file being accessed
	 * @param defResource The name of the resource (stored in the plugin jar) to save as default config if file doesn't exist
	 */
	public ConfigAccessor(JavaPlugin plugin, File file, String defResource) { init(plugin, null, file, defResource); }

	private void init(JavaPlugin plugin, String fileName, File file, String resource) {
		if (plugin == null)
			throw new CarbonException(CarbonLib.inst, "ConfigAccessor construct error: Plugin cannot be null");
		if (plugin.getDataFolder() == null)
			throw new CarbonException(plugin, "ConfigAccessor construct error: Provided plugin's data folder is null");
		this.plugin = plugin;
		this.fileName = fileName == null ? file.getName() : fileName;
		this.configFile = file == null ? new File(plugin.getDataFolder(), fileName) : file;
		this.defaultResource = resource == null ? fileName : resource;
		reload();
	}

	/**
	 * Attempts to reload the FileConfiguration object associated with this instance of ConfigAccessor
	 * @see FileConfiguration
	 */
	public void reload() {
		if (!exists()) saveDefaultConfig(defaultResource);
		yamlConf = YamlConfiguration.loadConfiguration(configFile);
	}
	/**
	 * Returns the FileConfiguration object associated with this instance of ConfigAccessor
	 * @return Returns a FileConfiguration object stored in this object
	 * @see FileConfiguration
	 */
	public FileConfiguration config() {
		if (yamlConf == null)
			this.reload();
		return yamlConf;
	}
	/**
	 * Saves the current working config to the destination file
	 */
	public void save() {
		if (yamlConf != null && configFile != null) {
			try {
				config().save(configFile);
			} catch (IOException ex) {
				plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
			}
		} else {
			if (yamlConf == null)
				System.out.println("FileConfiguration object is null for " + fileName);
			if (configFile == null)
				System.out.println("Config file object is null for " + fileName);
		}
	}
	/**
	 * Saves a default copy of the file from the plugin jar to the destination file
	 */
	private void saveDefaultConfig(String resource) {
		//if (!exists()) this.plugin.saveResource(fileName, false);
		if (!exists()) {
			yamlConf = new YamlConfiguration();
			InputStream defConfigStream = plugin.getResource(resource);
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
				//YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				yamlConf.setDefaults(defConfig);
			}
			save();
		}
	}
	/**
	 * Indicates whether or not the config's destination file exists
	 * @return Returns true if the destination file exists, false otherwise
	 */
	public boolean exists() { return configFile.exists(); }
}
