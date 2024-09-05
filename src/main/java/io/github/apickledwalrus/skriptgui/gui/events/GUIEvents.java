package io.github.apickledwalrus.skriptgui.gui.events;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.gui.GUIEventHandler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event) {
		// Process this event if it's cancelled ONLY if the clicker is in Spectator Mode
		if (event.getWhoClicked().getGameMode() != GameMode.SPECTATOR && event.isCancelled()) {
			return;
		}

		// Don't handle this event if it's from an unsupported click type
		switch (event.getClick()) {
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return;
		}

		// No inventory was clicked
		Inventory clickedInventory = event.getClickedInventory();
		if (clickedInventory == null) {
			return;
		}

		// Don't handle this event if there isn't a matching GUI for it
		GUI gui = SkriptGUI.getGUIManager().getGUI(event.getInventory());
		if (gui == null) {
			return;
		}
		GUIEventHandler eventHandler = gui.getEventHandler();

		// Don't process unknown clicks for safety reasons - cancel them to prevent unwanted GUI changes
		if (event.getClick() == ClickType.UNKNOWN) {
			event.setCancelled(true);
			return;
		}

		// Don't handle this event if the clicked inventory is the bottom inventory, as we want users to be able to interact with their inventory
		// However, there are some cases where interaction with the bottom inventory may cause changes to the top inventory
		// Because of this, we will cancel the event for some click types
		if (clickedInventory.equals(event.getView().getBottomInventory())) {
			switch (event.getClick()) {
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
					ItemStack clicked = event.getCurrentItem();
					if (clicked != null) {
						Inventory guiInventory = gui.getInventory();

						int size = guiInventory.getSize();
						int totalAmount = clicked.getAmount();

						for (int slot = 0; slot < size; slot++) {
							if (totalAmount <= 0) {
								return;
							}

							ItemStack item = guiInventory.getItem(slot);
							if (item != null && item.getType() != Material.AIR && item.isSimilar(clicked) && item.getAmount() < item.getMaxStackSize()) {
								InventoryClickEvent clickEvent = setClickedSlot(event, slot);

								if (!gui.isRemovable(gui.convert(slot))) {
									event.setCancelled(true);
									return;
                                } else {
									eventHandler.onChange(clickEvent);
									totalAmount -= item.getMaxStackSize() - item.getAmount();
                                }
                            }
						}

						int firstEmpty = guiInventory.firstEmpty();
						if (firstEmpty != -1 && gui.isRemovable(gui.convert(firstEmpty))) { // Safe to be moved into the GUI
							InventoryClickEvent clickEvent = setClickedSlot(event, firstEmpty);
							eventHandler.onChange(clickEvent);
							return;
						}

					}

					event.setCancelled(true);
					return;
				case DOUBLE_CLICK:
					// Only cancel if this will cause a change to the GUI itself
					// We are checking if our GUI contains an item that could be merged with the event item
					// If that item is mergeable but it isn't stealable, we will cancel the event now
					handleDoubleClick(gui, event);
					return;
				default:
					return;
			}
		} else {
			// Call onChange if a slot is changed due to interactions within the gui itself
			if (event.getClick() == ClickType.DOUBLE_CLICK) {
				if (!gui.isRemovable(gui.convert(event.getSlot()))) { // Doesn't change the slots
					event.setCancelled(true);
					return;
				}

				handleDoubleClick(gui, event);
			}
		}

		gui.getEventHandler().onClick(event);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent e) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(e.getInventory());
		if (gui != null) {
			// Check if any slots in the actual GUI were changed. We don't care if only the player's inventory was changed.
			int lastSlotIndex = gui.getInventory().getSize() - 1;
			for (int slot : e.getRawSlots()) {
				if (slot <= lastSlotIndex) { // A slot in the actual GUI was interacted with
					gui.getEventHandler().onDrag(e);
					break;
				}
			}
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

	private void handleDoubleClick(GUI gui, InventoryClickEvent event) {
		GUIEventHandler eventHandler = gui.getEventHandler();

		Inventory guiInventory = gui.getInventory();
		int size = guiInventory.getSize();
		ItemStack cursor = event.getCursor();

		if (cursor == null || event.getCurrentItem() != null)
			return;

		int totalAmount = cursor.getAmount();
		List<InventoryClickEvent> clickEvents = new ArrayList<>();
		for (int slot = 0; slot < size; slot++) {
			ItemStack item = guiInventory.getItem(slot);
			if (item != null && item.isSimilar(cursor)) {
				if (!gui.isRemovable(gui.convert(slot))) {
					event.setCancelled(true);
					return;
				}

				if (totalAmount < cursor.getMaxStackSize()) {
					InventoryClickEvent clickEvent = setClickedSlot(event, slot);
					clickEvents.add(clickEvent);
					totalAmount += item.getAmount();
				}
			}
		}
		for (InventoryClickEvent clickEvent : clickEvents) {
			eventHandler.onChange(clickEvent);
		}
	}

	private static InventoryClickEvent setClickedSlot(InventoryClickEvent event, int slot) {
		return new InventoryClickEvent(
				event.getView(),
				event.getSlotType(),
				slot,
				event.getClick(),
				event.getAction()
		);
	}

}
