package io.github.apickledwalrus.skriptgui.gui.events;

import ch.njol.skript.SkriptEventHandler;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class GUIEvents implements Listener {

	public GUIEvents() {
		// We still want these events to be processed by Skript
		SkriptEventHandler.listenCancelled.add(InventoryClickEvent.class);
		SkriptEventHandler.listenCancelled.add(InventoryDragEvent.class);
		SkriptEventHandler.listenCancelled.add(InventoryOpenEvent.class);
		SkriptEventHandler.listenCancelled.add(InventoryCloseEvent.class);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		// Don't handle this event if it's from an unsupported click type
		switch (event.getClick()) {
			case UNKNOWN:
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return;
		}

		// Don't handle this event if the clicked inventory is the bottom inventory
		Inventory clickedInventory = event.getClickedInventory();
		if (clickedInventory == null || clickedInventory.equals(event.getView().getBottomInventory())) {
			return;
		}

		// Don't handle this event if there isn't a matching GUI for it
		GUI gui = SkriptGUI.getGUIManager().getGUI(event.getInventory());
		if (gui == null) {
			return;
		}

		gui.getEventHandler().onClick(event);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent e) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(e.getInventory());
		if (gui != null) {
			gui.getEventHandler().onDrag(e);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent e) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(e.getInventory());
		if (gui != null) {
			gui.getEventHandler().onOpen(e);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent e) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(e.getInventory());
		if (gui != null) {
			gui.getEventHandler().onClose(e);
		}
	}

}
