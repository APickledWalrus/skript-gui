package io.github.apickledwalrus.skriptgui.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class GUIManager {

	public GUIManager() {}

	private Map<String, GUI> globalGUIs = new HashMap<>();

	private WeakHashMap<Event, GUI> eventGUIs = new WeakHashMap<>();

	private Map<UUID, GUI> players = new HashMap<>();

	public GUI getGUIEvent(Event e) {
		return e != null ? eventGUIs.get(e) : null;
	}

	/**
	 * If the given GUI is null, then the event will be removed from the Event GUIs list.
	 * @param e The event for the GUI to be paired with.
	 * @param gui The GUI for the event to be paired with.
	 */
	public void setGUIEvent(Event e, GUI gui) {
		if (e != null && gui != null) {
			eventGUIs.put(e, gui);
		} else if (e != null) {
			eventGUIs.remove(e);
		}
	}

	public GUI getGlobalGUI(String id) {
		if (id == null)
			return null;
		GUI gui = globalGUIs.get(id);
		return gui;
	}

	public void addGlobalGUI(String id, GUI gui) {
		if (id != null && gui != null)
			globalGUIs.put(id, gui);
	}

	public GUI removeGlobalGUI(String id) {
		return globalGUIs.remove(id);
	}

	public void setGUI(Player p, GUI gui) {
		players.put(p.getUniqueId(), gui);
	}

	public void removeGUI(Player p) {
		players.remove(p.getUniqueId());
	}

	public boolean hasGUI(Player p) {
		return players.containsKey(p.getUniqueId());
	}

}
