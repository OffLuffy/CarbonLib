package net.teamcarbon.carbonlib.Misc;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Convenience exception, only prints out lines of the stack trace that fit the scope specified
 */
@SuppressWarnings("UnusedDeclaration")
public final class CarbonException extends RuntimeException {

	private static HashMap<Plugin, String> pluginScopes = new HashMap<Plugin, String>();
	private List<StackTraceElement> customElements = new ArrayList<StackTraceElement>();
	private Plugin plugin;

	public CarbonException(Plugin plugin, String msg) {
		super(msg);
		if (plugin != null) { this.plugin = plugin; }
		Collections.addAll(customElements, getStackTrace());
		if (pluginScopes.containsKey(plugin))
			cleanStack(pluginScopes.get(plugin));
	}
	public CarbonException(Plugin plugin, Exception e) {
		super(e.getMessage());
		if (plugin != null) { this.plugin = plugin; }
		Collections.addAll(customElements, e.getStackTrace());
		if (pluginScopes.containsKey(plugin))
			cleanStack(pluginScopes.get(plugin));
	}
	public CarbonException(Plugin plugin, String traceScope, String msg) {
		super(msg);
		if (plugin != null) { this.plugin = plugin; }
		Collections.addAll(customElements, getStackTrace());
		cleanStack(traceScope);
	}
	public CarbonException(Plugin plugin, String traceScope, Exception e) {
		super(e.getMessage());
		if (plugin != null) { this.plugin = plugin; }
		Collections.addAll(customElements, e.getStackTrace());
		cleanStack(traceScope);
	}

	/**
	 * Removes StackTraceElements from the parent list which do not conform to the scope
	 */
	private void cleanStack(String scope) {
		if (scope == null || scope.isEmpty()) return;
		List<StackTraceElement> clean = new ArrayList<StackTraceElement>();
		for (StackTraceElement ste : customElements) if(ste.getClassName().contains(scope)) clean.add(ste);
		customElements.clear();
		customElements.addAll(clean);
	}

	/**
	 * Fetches the list of StackTraceElements maintained by this object
	 * @return List of StackTraceElements
	 */
	public List<StackTraceElement> getStackTraceList() { return customElements; }

	@Override
	public void printStackTrace() {
		System.err.println(((plugin != null) ? plugin.getName() + " " : "") + "Exception: " + getMessage());
		for (StackTraceElement ste : customElements)
			System.err.println("\t" + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
	}

	public static void setGlobalPluginScope(Plugin plugin, String scope) { pluginScopes.put(plugin, scope); }
	public static void removeGlobalPluginScope(Plugin plugin) { if (pluginScopes.containsKey(plugin)) pluginScopes.remove(plugin); }
}
