package net.teamcarbon.carbonlib;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convenience exception, only prints out lines of the stack trace that fit the scope specified
 */
@SuppressWarnings("UnusedDeclaration")
public final class CarbonException extends RuntimeException {
	private List<StackTraceElement> customElements = new ArrayList<StackTraceElement>();
	private String scope, pluginName;
	private static boolean nameSet = true;

	public CarbonException(Plugin plugin, String msg) {
		super(msg);
		Collections.addAll(customElements, getStackTrace());
		if (plugin != null) {
			nameSet = true;
			pluginName = plugin.getName();
		}
	}
	public CarbonException(Plugin plugin, Exception e) {
		super(e.getMessage());
		Collections.addAll(customElements, e.getStackTrace());
		if (plugin != null) {
			nameSet = true;
			pluginName = plugin.getName();
		}
	}
	public CarbonException(Plugin plugin, String traceScope, String msg) {
		super(msg);
		if (plugin != null) {
			nameSet = true;
			pluginName = plugin.getName();
		}
		Collections.addAll(customElements, getStackTrace());
		scope = traceScope;
		cleanStack();
	}
	public CarbonException(Plugin plugin, String traceScope, Exception e) {
		super(e.getMessage());
		if (plugin != null) {
			nameSet = true;
			pluginName = plugin.getName();
		}
		Collections.addAll(customElements, e.getStackTrace());
		scope = traceScope;
		cleanStack();
	}

	/**
	 * Removes StackTraceElements from the parent list which do not conform to the scope
	 */
	private void cleanStack() {
		List<StackTraceElement> clean = new ArrayList<StackTraceElement>();
		for (StackTraceElement ste : customElements) {
			if(ste.getClassName().contains(scope)) {
				clean.add(ste);
			}
		}
		customElements.clear();
		customElements.addAll(clean);
	}

	/**
	 * Fetches the list of StackTraceElements maintained by this object
	 * @return List of StackTraceElements
	 */
	public List<StackTraceElement> getStackTraceList() {
		return customElements;
	}

	@Override
	public void printStackTrace() {
		System.err.println((nameSet?pluginName + " " : "") + "Exception: " + getMessage());
		for (StackTraceElement ste : customElements)
			System.err.println("\t" + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
	}
}
