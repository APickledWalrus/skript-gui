package io.github.apickledwalrus.skriptgui.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class GUIManager {

	/**
	 * A map for tracking all GUIs based on their Inventory.
	 * Used mainly during event processing (see {@link io.github.apickledwalrus.skriptgui.gui.events.GUIEvents}).
	 */
	private final Map<Inventory, GUI> guis = new HashMap<>();

	/**
	 * A map to track the GUI involved in an event.
	 * Used for tracking GUIs within Skript {@link ch.njol.skript.lang.Trigger}s.
	 */
	private final WeakHashMap<Event, GUI> eventGUIs = new WeakHashMap<>();

	/**
	 * Registers a GUI with the manager. This enables event processing for the given GUI.
	 * @param gui The GUI to register.
	 */
	public void register(GUI gui) {
		guis.put(gui.getInventory(), gui);
	}

	/**
	 * Unregisters a GUI from the manager. This disables event processing for the given GUI.
	 * This method will also clear the GUI and remove its viewers.
	 * @param gui The GUI to unregister.
	 */
	public void unregister(GUI gui) {
		new ArrayList<>(gui.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
		gui.clear();
		guis.remove(gui.getInventory());
		// Just remove them from the event GUIs list now
		eventGUIs.values().removeIf(eventGUI -> eventGUI == gui);
	}

	/**
	 * Transfers the Inventory reference tied to the provided GUI's registration.
	 * This method is preferred over {@link #register(GUI)} and then {@link #unregister(GUI)},
	 * as it ensures no information (such as Event associations) may be lost.
	 * @param gui The GUI to modify the registration of.
	 * @param newInventory The new Inventory to associate with the provided GUI.
	 */
	public void transferRegistration(GUI gui, Inventory newInventory) {
		guis.remove(gui.getInventory());
		guis.put(newInventory, gui);
	}

	/**
	 * @return A list of tracked GUIs.
	 */
	public Collection<GUI> getTrackedGUIs() {
		return guis.values();
	}

	/**
	 * @param event The event to get the GUI from.
	 * @return The GUI involved with the given event.
	 */
	public @Nullable GUI getGUI(Event event) {
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
	public @Nullable GUI getGUI(Player player) {
		for (GUI gui : getTrackedGUIs()) {
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
		for (GUI gui : getTrackedGUIs()) {
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
	public @Nullable GUI getGUI(String id) {
		for (GUI gui : getTrackedGUIs()) {
			if (id.equals(gui.getID())) {
				return gui;
			}
		}
		return null;
	}

	/**
	 * @param inventory The inventory of the GUI to get.
	 * @return The GUI with this inventory, or null if a GUI with this inventory doesn't exist.
	 */
	public @Nullable GUI getGUI(Inventory inventory) {
		return guis.get(inventory);
	}

}
