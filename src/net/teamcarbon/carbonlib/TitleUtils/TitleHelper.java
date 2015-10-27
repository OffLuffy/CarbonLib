package net.teamcarbon.carbonlib.TitleUtils;

import net.minecraft.server.v1_8_R3.*;
import net.teamcarbon.carbonlib.FormatUtils.FormattedMessage;
import net.teamcarbon.carbonlib.Misc.LagMeter;
import net.teamcarbon.carbonlib.Misc.LocUtils;
import net.teamcarbon.carbonlib.Misc.Messages.Clr;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.Locale;

@SuppressWarnings("unused")
public class TitleHelper implements Listener {
	private static final int[] DEF_TIMES = {10, 40, 10};

	// Title API
	public static void sendTitle(Player player, int[] timings, String title, String subtitle) {
		sendRawTitle(player, timings == null ? DEF_TIMES : timings, jsonWrap(title), jsonWrap(subtitle));
	}
	public static void sendTitle(Player player, int[] timings, FormattedMessage title, FormattedMessage subtitle) {
		sendRawTitle(player, timings == null ? DEF_TIMES : timings, title.toJSONString(), subtitle.toJSONString());
	}
	private static void sendRawTitle(Player player, int[] timings, String title, String subtitle) {
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(repVars(title, player)));
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(repVars(subtitle, player)));
		int[] ft = fixTimings(timings);
		sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, ft[0], ft[1], ft[2]));
		sendPacket(player, titlePacket, subtitlePacket);
	}
	private static int[] fixTimings(int[] timings) {
		int[] newTimings = new int[DEF_TIMES.length];
		for (int i = 0; i < DEF_TIMES.length; i++)
			newTimings[i] = i > timings.length-1 ? DEF_TIMES[i] : timings[i];
		return newTimings;
	}

	// Action Bar API
	public static void sendActionBar(Player player, String message) {
		sendRawActionBar(player, jsonWrap(message));
	}
	public static void sendActionBar(Player player, FormattedMessage message) {
		sendRawActionBar(player, message.toJSONString());
	}
	public static void sendRawActionBar(Player player, String message){
		IChatBaseComponent actionMessage = IChatBaseComponent.ChatSerializer.a(repVars(Clr.trans(message), player));
		sendPacket(player, new PacketPlayOutChat(actionMessage, (byte) 2));
	}

	// Tab Title/Footer API
	public static void sendTabTitle(Player player, String header, String footer) {
		sendRawTabTitle(player, jsonWrap(header), jsonWrap(footer));
	}
	public static void sendTabTitle(Player player, FormattedMessage header, FormattedMessage footer) {
		sendRawTabTitle(player, header.toJSONString(), footer.toJSONString());
	}
	public static void sendRawTabTitle(Player player, String header, String footer) {
		IChatBaseComponent tabTitle = IChatBaseComponent.ChatSerializer.a(repVars(header, player));
		IChatBaseComponent tabFoot = IChatBaseComponent.ChatSerializer.a(repVars(footer, player));
		PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabTitle);
		try {
			Field field = headerPacket.getClass().getDeclaredField("b");
			field.setAccessible(true);
			field.set(headerPacket, tabFoot);
		} catch (Exception e) {}
		sendPacket(player, headerPacket);
	}

	// Private Methods
	private static void sendPacket(Player pl, Packet ... packets) { for (Packet p : packets) ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(p); }
	private static String repVars(String msg, Player pl) {
		msg = (msg == null || msg.isEmpty()) ? "" : Clr.trans(msg);

		// User Identity
		if (msg.contains("{NAME}")) msg = msg.replace("{NAME}", pl.getName());
		if (msg.contains("{DISPNAME}")) msg = msg.replace("{DISPNAME}", pl.getDisplayName());
		if (msg.contains("{UUID}")) msg = msg.replace("{UUID}", pl.getUniqueId().toString());
		if (msg.contains("{IP}")){
			String ip = pl.getAddress() == null ? "" : pl.getAddress().toString();
			if (!ip.isEmpty()) {
				if (ip.contains("/")) ip = ip.replace("/", ""); // Remove leading slash
				if (ip.contains(":")) ip = ip.substring(0, ip.indexOf(":")); // Remove port and port delimiter
			}
			msg = msg.replace("{IP}", ip);
		}

		// User Stats
		if (msg.contains("{HEALTH}")) msg = msg.replace("{HEALTH}", sformat("%.0f", pl.getHealth()));
		if (msg.contains("{MAXHEALTH}")) msg = msg.replace("{MAXHEALTH}", sformat("%.0f", pl.getMaxHealth()));
		if (msg.contains("{LEVEL}")) msg = msg.replace("{LEVEL}", sformat("%d", pl.getLevel()));
		if (msg.contains("{DAYSLIVED}")) msg = msg.replace("{DAYSLIVED}", sformat("%d", pl.getTicksLived() / 24000));

		// Server Info
		if (msg.contains("{TIME}")) msg = msg.replace("{TIME}", ticksToTime(pl.getWorld().getTime()));
		if (msg.contains("{TPS}")) msg = msg.replace("{TPS}", sformat("%.1f", LagMeter.getTPS(20)));

		// Location Info
		if (msg.contains("{WORLD}")) msg = msg.replace("{WORLD}", pl.getWorld().getName());
		if (msg.contains("{X}")) msg = msg.replace("{X}", sformat("%d", pl.getLocation().getBlockX()));
		if (msg.contains("{Y}")) msg = msg.replace("{Y}", sformat("%d", pl.getLocation().getBlockY()));
		if (msg.contains("{Z}")) msg = msg.replace("{Z}", sformat("%d", pl.getLocation().getBlockZ()));
		if (msg.contains("{PITCH}")) msg = msg.replace("{PITCH}", sformat("%.0f", pl.getLocation().getPitch()));
		if (msg.contains("{YAW}")) msg = msg.replace("{YAW}", sformat("%.0f", pl.getLocation().getYaw()));

		// Directional Info
		if (msg.contains("{FACING}")) msg = msg.replace("{FACING}", LocUtils.getCardinalDir(pl, false, false, false));
		if (msg.contains("{FACING_SHORT}")) msg = msg.replace("{FACING_SHORT}", LocUtils.getCardinalDir(pl, true, false, false));
		if (msg.contains("{FACING_SEC}")) msg = msg.replace("{FACING_SEC}", LocUtils.getCardinalDir(pl, false, false, true));
		if (msg.contains("{FACING_SHORT_SEC}")) msg = msg.replace("{FACING_SHORT_SEC}", LocUtils.getCardinalDir(pl, true, false, true));
		if (msg.contains("{COMPASS}")) msg = msg.replace("{COMPASS}", LocUtils.getCardinalDir(pl, false, true, false));
		if (msg.contains("{COMPASS_SHORT}")) msg = msg.replace("{COMPASS_SHORT}", LocUtils.getCardinalDir(pl, true, true, false));
		if (msg.contains("{COMPASS_SEC}")) msg = msg.replace("{COMPASS_SEC}", LocUtils.getCardinalDir(pl, false, true, true));
		if (msg.contains("{COMPASS_SHORT_SEC}")) msg = msg.replace("{COMPASS_SHORT_SEC}", LocUtils.getCardinalDir(pl, true, true, true));

		return msg;
	}
	private static String ticksToTime(long ticks) {
		ticks += 6000; // Offset 0 ticks to = 6AM
		int hours = (int)(ticks / 1000), minutes = (int)((ticks % 1000) / 16.66);
		return (hours > 12 ? hours > 24 ? hours - 24 : hours-12 : hours) + ":"
				+ (minutes < 10 ? "0" : "") + minutes + (hours >= 12 && hours < 24 ? " PM" : " AM");
	}
	private static String sformat(String pat, Object ... objects) { return String.format(Locale.ENGLISH, pat, objects); }
	private static String jsonWrap(String msg) { return "{\"text\": \"" + (msg == null ? "" : msg) + "\"}"; }
}