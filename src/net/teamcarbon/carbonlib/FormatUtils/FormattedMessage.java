package net.teamcarbon.carbonlib.FormatUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonWriter;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_10_R1.CraftStatistic;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;

@SuppressWarnings("UnusedDeclaration")
public class FormattedMessage {

	private final List<MessagePart> messageParts;
	private String jsonString;
	private boolean dirty;

	public FormattedMessage(final String firstPartText) {
		messageParts = new ArrayList<>();
		messageParts.add(new MessagePart(firstPartText));
		jsonString = null;
		dirty = false;
	}

	public FormattedMessage color(final ChatColor color) {
		if (!color.isColor()) {
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		latest().color = color;
		dirty = true;
		return this;
	}

	public FormattedMessage style(final ChatColor... styles) {
		for (final ChatColor style : styles) {
			if (!style.isFormat()) {
				throw new IllegalArgumentException(style.name() + " is not a style");
			}
		}
		latest().styles = styles;
		dirty = true;
		return this;
	}

	public FormattedMessage file(final String path) {
		onClick("open_file", path);
		return this;
	}

	public FormattedMessage link(final String url) {
		onClick("open_url", url);
		return this;
	}

	public FormattedMessage suggest(final String command) {
		onClick("suggest_command", command);
		return this;
	}

	public FormattedMessage command(final String command) {
		onClick("run_command", command);
		return this;
	}

	public FormattedMessage achievementTooltip(final String name) {
		onHover("show_achievement", "achievement." + name);
		return this;
	}

	public FormattedMessage achievementTooltip(final Achievement which) {
		net.minecraft.server.v1_10_R1.Achievement nms = CraftStatistic.getNMSAchievement(which);
		return achievementTooltip(nms.name);
	}

	public FormattedMessage statisticTooltip(final Statistic which) {
		Type type = which.getType();
		if (type != Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
		}
		net.minecraft.server.v1_10_R1.Statistic nms = CraftStatistic.getNMSStatistic(which);
		return achievementTooltip(nms.name);
	}

	public FormattedMessage statisticTooltip(final Statistic which, Material item) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if ((type == Type.BLOCK && item.isBlock()) || type == Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		net.minecraft.server.v1_10_R1.Statistic nms = CraftStatistic.getMaterialStatistic(which, item);
		return achievementTooltip(nms.name);
	}

	public FormattedMessage statisticTooltip(final Statistic which, EntityType entity) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if (type != Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		net.minecraft.server.v1_10_R1.Statistic nms = CraftStatistic.getEntityStatistic(which, entity);
		return achievementTooltip(nms.name);
	}

	public FormattedMessage itemTooltip(final String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}

	public FormattedMessage itemTooltip(final ItemStack itemStack) {
		return itemTooltip(CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString());
	}

	public FormattedMessage tooltip(final String text) {
		final String[] lines = text.split("\\n");
		if (lines.length <= 1) {
			onHover("show_text", text);
		} else {
			itemTooltip(makeMultilineTooltip(lines));
		}
		return this;
	}

	public FormattedMessage then(final Object obj) {
		messageParts.add(new MessagePart(obj.toString()));
		dirty = true;
		return this;
	}

	public String toJSONString() {
		if (!dirty && jsonString != null) {
			return jsonString;
		}
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			if (messageParts.size() == 1) {
				latest().writeJson(json);
			} else {
				json.beginObject().name("text").value("").name("extra").beginArray();
				for (final MessagePart part : messageParts) {
					part.writeJson(json);
				}
				json.endArray().endObject();
				json.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("invalid message");
		}
		jsonString = string.toString();
		dirty = false;
		return jsonString;
	}

	@Override
	public String toString() {
		String out = "";
		for (MessagePart mp : messageParts) {
			if (mp.color != null)
				out += mp.color;
			if (mp.styles != null)
				for (ChatColor cc : mp.styles)
					out += cc;
			if (mp.text != null)
				out += mp.text;
		}
		return out;
	}

	public void send(Player player){
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(toJSONString())));
	}

	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}

	private String makeMultilineTooltip(final String[] lines) {
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			json.beginObject().name("id").value(1);
			json.name("tag").beginObject().name("display").beginObject();
			json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
			json.name("Lore").beginArray();
			for (int i = 1; i < lines.length; i++) {
				final String line = lines[i];
				json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
			}
			json.endArray().endObject().endObject().endObject();
			json.close();
		} catch (Exception e) {
			throw new RuntimeException("invalid tooltip");
		}
		return string.toString();
	}

	private void onClick(final String name, final String data) {
		final MessagePart latest = latest();
		latest.clickActionName = name;
		latest.clickActionData = data;
		dirty = true;
	}

	private void onHover(final String name, final String data) {
		final MessagePart latest = latest();
		latest.hoverActionName = name;
		latest.hoverActionData = data;
		dirty = true;
	}

}