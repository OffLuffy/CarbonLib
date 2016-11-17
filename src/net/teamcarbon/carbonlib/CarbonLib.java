package net.teamcarbon.carbonlib;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.teamcarbon.carbonlib.Misc.CarbonException;
import net.teamcarbon.carbonlib.Misc.Log;
import net.teamcarbon.carbonlib.Misc.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class CarbonLib extends JavaPlugin {

	public static Log log;
	public static CarbonLib inst;

	private static List<CarbonPlugin> carbonPlugins = new ArrayList<CarbonPlugin>();

	private static Permission perm;
	private static Economy econ;
	private static Chat chat;

	@Override
	public void onEnable() {
		inst = this;
		CarbonException.setGlobalPluginScope(this, "net.teamcarbon");
		log = new Log(this, "enable-debug-messages");

		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				log.debug("=====[ CarbonLib: Hooked Plugins ]=====");
				for (CarbonPlugin p : carbonPlugins) { log.debug(p.namever()); }
				log.debug("=======================================");
			}
		}, 5L);
	}

	public static void notifyHook(String pluginName) { log.info("Hooked to plugin: " + pluginName); }
	public static void notifyHook(JavaPlugin plugin) { notifyHook(plugin.getName()); }
	public static void addPlugin(CarbonPlugin plugin) {
		if (!carbonPlugins.contains(plugin)) carbonPlugins.add(plugin);
	}

	public static Permission fetchPerm() { return perm == null ? (setupPerm() ? perm : null) : perm; }
	public static Economy fetchEcon() { return econ == null ? (setupEcon() ? econ : null) : econ; }
	public static Chat fetchChat() { return chat == null ? (setupChat() ? chat : null) : chat; }

	private static boolean setupPerm() {
		RegisteredServiceProvider<Permission> pp = Bukkit.getServicesManager().getRegistration(Permission.class);
		if (pp != null) perm = pp.getProvider();
		MiscUtils.setPerms(perm);
		return perm != null;
	}
	private static boolean setupEcon() {
		RegisteredServiceProvider<Economy> ep = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (ep != null) econ = ep.getProvider();
		return econ != null;
	}
	private static boolean setupChat() {
		RegisteredServiceProvider<Chat> cp = Bukkit.getServicesManager().getRegistration(Chat.class);
		if (cp != null) chat = cp.getProvider();
		return chat != null;
	}

	public static List<CarbonPlugin> getCarbonPlugins() { return new ArrayList<>(carbonPlugins); }

}