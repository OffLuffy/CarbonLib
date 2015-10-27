package net.teamcarbon.carbonlib.Misc;

/**
 * @author LazyLemons
 * https://bukkit.org/threads/get-server-tps.143410/
 */
@SuppressWarnings("unused")
public class LagMeter implements Runnable {
	public static int TICK_COUNT = 0;
	public static long[] TICKS = new long[600];
	public static double getTPS() { return getTPS(100); }
	public static double getTPS(int ticks) {
		ticks = Math.min(ticks, TICK_COUNT-1);
		int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];
		return ticks / (elapsed / 1000.0D);
	}
	public static long getElapsed(int tickID) {
		if (TICK_COUNT - tickID >= TICKS.length) {}
		long time = TICKS[(tickID % TICKS.length)];
		return System.currentTimeMillis() - time;
	}
	public void run() {
		TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
		TICK_COUNT++;
	}
}
