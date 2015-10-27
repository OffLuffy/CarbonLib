package net.teamcarbon.carbonlib;

import net.teamcarbon.carbonlib.Misc.CarbonException;
import net.teamcarbon.carbonlib.Misc.Log;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnusedDeclaration")
public class CarbonLib extends JavaPlugin {

	public static Log log;
	public static CarbonLib inst;

	@Override
	public void onEnable() {
		inst = this;
		CarbonException.setGlobalPluginScope(this, "net.teamcarbon");
		log = new Log(this, null);
	}

	public static void notifyHook(String pluginName) { log.info("Hooked to plugin: " + pluginName); }
	public static void notifyHook(JavaPlugin plugin) { notifyHook(plugin.getName()); }

}