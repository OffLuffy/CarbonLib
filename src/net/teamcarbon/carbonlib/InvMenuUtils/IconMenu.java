package net.teamcarbon.carbonlib.InvMenuUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

/**
 * Class to simplify handling inventory-based menus<br />
 * Source from http://bukkit.org/threads/icon-menu.108342/
 * @author nisovin (original poster), vemacs (modified source), OffLuffy (modified again here)
 */

@SuppressWarnings("unused")
public class IconMenu implements Listener {

	private String name = "Menu";
	private int rows;
	private OptionClickEventHandler handler;
	private Plugin plugin;
	private Player player;

	private String[] optionNames;
	private ItemStack[] optionIcons;

	public IconMenu(String name, int rows, OptionClickEventHandler handler, Plugin plugin) {
		this.name = name;
		this.rows = rows*9;
		this.handler = handler;
		this.plugin = plugin;
		this.optionNames = new String[rows*9];
		this.optionIcons = new ItemStack[rows*9];
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public IconMenu setOption(int position, ItemStack icon, String name, String... info) {
		optionNames[position] = name;
		optionIcons[position] = setItemNameAndLore(icon, name, info);
		return this;
	}

	public void setSpecificTo(Player player) { this.player = player; }

	public boolean isSpecific() { return player != null; }

	public void open(Player player) {
		Inventory inventory = Bukkit.createInventory(player, rows*9, name);
		for (int i = 0; i < optionIcons.length; i++) {
			if (optionIcons[i] != null) {
				inventory.setItem(i, optionIcons[i]);
			}
		}
		player.openInventory(inventory);
	}

	public void destroy() {
		HandlerList.unregisterAll(this);
		handler = null;
		plugin = null;
		optionNames = null;
		optionIcons = null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(name) && (player == null || event.getWhoClicked() == player)) {
			event.setCancelled(true);
			if (event.getClick() != ClickType.LEFT)
				return;
			int slot = event.getRawSlot();
			if (slot >= 0 && slot < rows*9 && optionNames[slot] != null) {
				Plugin plugin = this.plugin;
				OptionClickEvent e = new OptionClickEvent((Player) event.getWhoClicked(), slot, optionNames[slot], optionIcons[slot]);
				handler.onOptionClick(e);
				((Player) event.getWhoClicked()).updateInventory();
				if (e.willClose()) {
					final Player p = (Player) event.getWhoClicked();
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							p.closeInventory();
						}
					});
				}
				if (e.willDestroy()) {
					destroy();
				}
			}
		}
	}

	private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		item.setItemMeta(im);
		return item;
	}

}
