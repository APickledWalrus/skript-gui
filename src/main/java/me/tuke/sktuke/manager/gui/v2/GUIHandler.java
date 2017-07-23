package me.tuke.sktuke.manager.gui.v2;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Tuke_Nuke on 02/03/2017
 */
public class GUIHandler {
	private static GUIHandler ourInstance = new GUIHandler();

	public static GUIHandler getInstance() {
		return ourInstance;
	}

	private GUIHandler() {
	}

	private Map<String, GUIInventory> list = new HashMap<>();

	private WeakHashMap<Event, GUIInventory> eventGuis = new WeakHashMap<>();

	public GUIInventory getGUIEvent(Event e) {
		return e != null ? eventGuis.get(e) : null;
	}
	public void setGUIEvent(Event e, GUIInventory gui) {
		if (e != null && gui != null)
			eventGuis.put(e, gui);
		else if (e != null)
			eventGuis.remove(e);
	}
	public GUIInventory getGUI(Player p) {
		if (p != null) {
			GUIInventory gui = list.get("player:" + p.getUniqueId());
			if (gui == null && p.getOpenInventory() != null && p.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) {
				gui = new GUIInventory(p.getOpenInventory().getTopInventory());
				list.put("player:" + p.getUniqueId(), gui);
			}
			return gui;
		}
		return null;
	}

	public void setGUI(String id, GUIInventory gui){
		if (id != null && !id.isEmpty() && gui != null)
			list.put("id:" + id, gui.setID(id));
	}
	public GUIInventory removeGUI(String id) {
		return list.remove(id);
	}
	public GUIInventory getGUI(String id){
		return id != null && !id.isEmpty() ? list.get("id:" + id) : null;
	}
}
