package io.github.apickledwalrus.skriptgui.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class GUIManager {

	public GUIManager() {}

	private final Map<String, GUI> globalGUIs = new HashMap<>();

	private final WeakHashMap<Event, GUI> eventGUIs = new WeakHashMap<>();

	private final Map<UUID, GUI> players = new HashMap<>();

	public GUI getGUIEvent(Event e) {
		return eventGUIs.get(e);
	}

	/**
	 * If the given GUI is null, then the event will be removed from the Event GUIs list.
	 * @param e The event for the GUI to be paired with.
	 * @param gui The GUI for the event to be paired with.
	 */
	public void setGUIEvent(Event e, @Nullable GUI gui) {
		if (gui != null) {
			eventGUIs.put(e, gui);
		} else {
			eventGUIs.remove(e);
		}
	}

	@Nullable
	public GUI getGlobalGUI(String id) {
		return id != null ? globalGUIs.get(id) : null;
	}

	public void addGlobalGUI(String id, GUI gui) {
		if (id != null && gui != null)
			globalGUIs.put(id, gui);
	}

	@Nullable
	public GUI removeGlobalGUI(String id) {
		return globalGUIs.remove(id);
	}

	public void setGUI(Player p, GUI gui) {
		players.put(p.getUniqueId(), gui);
	}

	@Nullable
	public GUI getGUI(Player p) {
		return players.get(p.getUniqueId());
	}

	public void removeGUI(Player p) {
		players.remove(p.getUniqueId());
	}

	public boolean hasGUI(Player p) {
		return players.containsKey(p.getUniqueId());
	}

}
