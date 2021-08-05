package io.github.apickledwalrus.skriptgui.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class GUIManager {

	/**
	 * A list of all tracked GUIs.
	 * This list is used for event processing.
	 */
	private final List<GUI> guis = new ArrayList<>();

	/**
	 * A map to track the GUI involved in an event.
	 */
	private final WeakHashMap<Event, GUI> eventGUIs = new WeakHashMap<>();

	/**
	 * Registers a GUI with the manager. This enables event processing for the given GUI.
	 * @param gui The GUI to register.
	 */
	public void register(GUI gui) {
		guis.add(gui);
	}

	/**
	 * Unregisters a GUI from the manager. This disables event processing for the given GUI.
	 * @param gui The GUI to unregister.
	 */
	public void unregister(GUI gui) {
		guis.remove(gui);
		// Just remove them from the event GUIs list now
		eventGUIs.values().removeIf(eventGUI -> eventGUI == gui);
	}

	/**
	 * @return A list of tracked GUIs.
	 */
	public List<GUI> getTrackedGUIs() {
		return guis;
	}

	/**
	 * @param event The event to get the GUI from.
	 * @return The GUI involved with the given event.
	 */
	@Nullable
	public GUI getGUI(Event event) {
		return eventGUIs.get(event);
	}

	/**
	 * Sets the GUI to be tracked as part of an event.
	 * If the GUI parameter is null, this event will be removed from the tracked events map.
	 * @param event The event the given GUI is involved with.
	 * @param gui The GUI of the given event.
	 */
	public void setGUI(Event event, @Nullable GUI gui) {
		if (gui != null) {
			eventGUIs.put(event, gui);
		} else {
			eventGUIs.remove(event);
		}
	}

	/**
	 * @param player The player to get the GUI from.
	 * @return The open GUI of the player, or null if this player doesn't have a GUI open.
	 */
	@Nullable
	public GUI getGUI(Player player) {
		for (GUI gui : guis) {
			if (gui.getInventory().getViewers().contains(player)) {
				return gui;
			}
		}
		return null;
	}

	/**
	 * @param player The player to check.
	 * @return Whether the player has a GUI open.
	 */
	public boolean hasGUI(Player player) {
		for (GUI gui : guis) {
			if (gui.getInventory().getViewers().contains(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param id The ID of the GUI to get. This parameter is case-sensitive.
	 * @return The GUI with the given ID, or null if a GUI with this ID doesn't exist.
	 */
	@Nullable
	public GUI getGUI(String id) {
		for (GUI gui : guis) {
			if (gui.getID() != null && gui.getID().equals(id)) {
				return gui;
			}
		}
		return null;
	}

	/**
	 * @param inventory The inventory of the GUI to get.
	 * @return The GUI with this inventory, or null if a GUI with this inventory doesn't exist.
	 */
	@Nullable
	public GUI getGUI(Inventory inventory) {
		for (GUI gui : guis) {
			if (gui.getInventory().equals(inventory)) {
				return gui;
			}
		}
		return null;
	}

}