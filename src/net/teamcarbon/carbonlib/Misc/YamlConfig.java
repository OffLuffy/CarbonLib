package net.teamcarbon.carbonlib.Misc;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

@SuppressWarnings("unused")
public class YamlConfig extends YamlConfiguration {
	private JavaPlugin plugin;
	private File file;
	private String resource;
	private HashMap<String, Boolean> ignoredUpdateSections = new HashMap<>();

	public YamlConfig(JavaPlugin plugin, File file) { this(plugin, file, null); }
	public YamlConfig(JavaPlugin plugin, File file, String resource) {
		this.plugin = plugin;
		this.file = file;
		this.resource = resource == null || resource.isEmpty() ? file.getName() : resource;
		createFile();
	}

	/**
	 * Saves the current working config to the destination file
	 */
	public void save() { try { super.save(file); } catch (Exception e) { CarbonException.print(plugin, e); } }

	/**
	 * Attempts to reload the FileConfiguration object associated with this instance of ConfigAccessor
	 * @see FileConfiguration
	 */
	public void reload() {
		if (!exists()) createFile();
		try { load(resource); } catch(Exception ignore) {}
	}

	/**
	 * Indicates whether or not the config's destination file exists
	 * @return Returns true if the destination file exists, false otherwise
	 */
	public boolean exists() { return file.exists(); }

	/**
	 * Iterates over the file and inserts new sections or values not present when compared to the source file while
	 * ignoring specified sections
	 * @see #addIgnoredSection(String,boolean)
	 */
	@SuppressWarnings("deprecation")
	public void update() {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(plugin.getResource(resource));
		for (String s : fc.getKeys(true)) {
			if (!contains(s)) {
				if (ignoredUpdateSections.containsKey(s)) {
					if (!ignoredUpdateSections.get(s)) continue;
					set(s, new MemoryConfiguration());
				}
			}
		}
	}

	/**
	 * Adds a key that will be ignored when updating. If contentsOnly is true, the update
	 * will make sure the section exists, but not anything inside it.
	 * @param key A path to a section to ignore
	 * @param contentsOnly Whether to ignore only the contents inside a section
	 */
	public void addIgnoredSection(String key, boolean contentsOnly) { ignoredUpdateSections.put(key, contentsOnly); }

	private void createFile() {
		try {
			if (!file.exists()) {
				InputStream in = plugin.getResource(resource);
				if (in != null) {
					FileUtils.copyInputStreamToFile(in, file);
					load(file);
				} else { save(file); }
			} else { load(file); save(file); }
		} catch (Exception e) { e.printStackTrace(); }
	}
}
