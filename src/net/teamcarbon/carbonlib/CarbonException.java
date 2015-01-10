package net.teamcarbon.carbonlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Convenience exception, only prints out lines of the stack trace that fit the scope specified
 */
@SuppressWarnings("UnusedDeclaration")
public final class CarbonException extends RuntimeException {
	private List<StackTraceElement> customElements = new ArrayList<StackTraceElement>();
	private String scope;
	private static String gScope, plugin;
	private static boolean globalScopeSet = false, nameSet = true;
	public CarbonException(String traceScope, String msg) {
		super(msg);
		Collections.addAll(customElements, getStackTrace());
		scope = traceScope;
		cleanStack();
	}
	public CarbonException(String msg) {
		super(msg);
		Collections.addAll(customElements, getStackTrace());
		if (globalScopeSet) {
			scope = gScope;
			cleanStack();
		}
	}
	public CarbonException(Exception e) {
		super(e.getMessage());
		Collections.addAll(customElements, e.getStackTrace());
		if (globalScopeSet) {
			scope = gScope;
			cleanStack();
		}
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
	 * Sets the scope which dictates which lines are printed from an exception
	 * @param scope The scope to restrict output to (i.e. me.teamcarbon)
	 */
	public static void setGlobalScope(String scope) {
		globalScopeSet = true;
		gScope = scope;
	}

	/**
	 * Sets the name the printStackTrace() method will use
	 * @param pluginName The name of your plugin
	 */
	public static void setPluginName(String pluginName) {
		nameSet = true;
		plugin = pluginName;
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
		System.err.println((nameSet?plugin + " " : "") + "Exception: " + getMessage());
		for (StackTraceElement ste : customElements)
			System.err.println("\t" + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")");
	}
}
