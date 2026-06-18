package io.github.apickledwalrus.skriptgui.gui;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
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

public final class GUIEvents implements Listener {

	private static InventoryClickEvent getClickEventWithSlot(InventoryClickEvent clickEvent, int slot) {
		return new InventoryClickEvent(
			clickEvent.getView(),
			clickEvent.getSlotType(),
			slot,
			clickEvent.getClick(),
			clickEvent.getAction()
		);
	}

	private static void handleDoubleClick(GUI gui, InventoryClickEvent clickEvent) {
		if (clickEvent.getCurrentItem() != null) {
			return;
		}

		Inventory inventory = gui.getInventory();
		ItemStack cursor = clickEvent.getCursor();
		int totalAmount = cursor.getAmount();
		int maxAmount = cursor.getMaxStackSize();
		int size = inventory.getSize();
		List<Integer> changedSlots = new ArrayList<>();
		for (int slot = 0; slot < size; slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item == null || !item.isSimilar(cursor)) { // not a candidate for merging
				continue;
			}

			if (!gui.isChangeable(gui.convert(slot))) { // would result in merging of an unchangeable slot
				clickEvent.setCancelled(true);
				return;
			}

			changedSlots.add(slot);
			totalAmount += item.getAmount();
			if (totalAmount >= maxAmount) { // no other slots will be changed
				break;
			}
		}

		GUIEventHandler eventHandler = gui.getEventHandler();
		for (int slot : changedSlots) {
			eventHandler.onChange(getClickEventWithSlot(clickEvent, slot));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent clickEvent) {
		// Process this event if it's canceled ONLY if the clicker is in Spectator Mode
		if (clickEvent.getWhoClicked().getGameMode() != GameMode.SPECTATOR && clickEvent.isCancelled()) {
			return;
		}

		// Don't handle this event if it's from an unsupported click type
		switch (clickEvent.getClick()) {
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return;
		}

		// No inventory was clicked
		Inventory clickedInventory = clickEvent.getClickedInventory();
		if (clickedInventory == null) {
			return;
		}

		// Don't handle this event if there isn't a matching GUI for it
		GUI gui = SkriptGUI.getGUIManager().getGUI(clickEvent.getInventory());
		if (gui == null) {
			return;
		}
		GUIEventHandler eventHandler = gui.getEventHandler();

		// Don't process unknown clicks for safety reasons - cancel them to prevent unwanted GUI changes
		if (clickEvent.getClick() == ClickType.UNKNOWN) {
			clickEvent.setCancelled(true);
			return;
		}

		// Don't handle this event if the clicked inventory is the bottom inventory, as we want users to be able to interact with their inventory
		// However, there are some cases where interaction with the bottom inventory may cause changes to the top inventory
		// Because of this, we will cancel the event for some click types
		if (clickedInventory.equals(clickEvent.getView().getBottomInventory())) {
			switch (clickEvent.getClick()) {
				case SHIFT_LEFT:
				case SHIFT_RIGHT:
					ItemStack clicked = clickEvent.getCurrentItem();
					if (clicked == null) {
						clickEvent.setCancelled(true);
						return;
					}

					Inventory guiInventory = gui.getInventory();

					int size = guiInventory.getSize();
					int totalAmount = clicked.getAmount();

					List<Integer> changedSlots = new ArrayList<>();
					for (int slot = 0; slot < size; slot++) {
						ItemStack item = guiInventory.getItem(slot);
						if (item != null && item.getType() != Material.AIR && item.isSimilar(clicked) && item.getAmount() < item.getMaxStackSize()) {
							if (!gui.isChangeable(gui.convert(slot))) { // Would result in a non-changeable slot being changed, thus block
								clickEvent.setCancelled(true);
								return;
							}
							// slot will have some amount distributed to it
							changedSlots.add(slot);
							totalAmount -= item.getMaxStackSize() - item.getAmount();
						}
						if (totalAmount <= 0) {
							break;
						}
					}

					if (totalAmount > 0) {
						int firstEmpty = guiInventory.firstEmpty();
						if (firstEmpty != -1) {
							if (!gui.isChangeable(gui.convert(firstEmpty))) { // slot would be illegally modified
								clickEvent.setCancelled(true);
								return;
							}
							// the rest of the item can go in this slot
							changedSlots.add(firstEmpty);
						}
					}

					for (int slot : changedSlots) {
						eventHandler.onChange(getClickEventWithSlot(clickEvent, slot));
					}
					return;
				case DOUBLE_CLICK:
					handleDoubleClick(gui, clickEvent);
					return;
				default:
					return;
			}
		} else if (clickEvent.getClick() == ClickType.DOUBLE_CLICK && gui.isChangeable(gui.convert(clickEvent.getSlot()))) {
			// a double click (merge operation) can only occur if the source slot can be modified
			handleDoubleClick(gui, clickEvent);
		}

		gui.getEventHandler().onClick(clickEvent);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent dragEvent) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(dragEvent.getInventory());
		if (gui == null) {
			return;
		}

		// check whether any slots in the actual GUI were changed
		// we don't care if only the player's inventory was changed
		int guiEnd = gui.getInventory().getSize();
		for (int slot : dragEvent.getRawSlots()) {
			if (slot < guiEnd) { // a slot in the actual GUI was interacted with
				gui.getEventHandler().onDrag(dragEvent);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent openEvent) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(openEvent.getInventory());
		if (gui != null) {
			gui.getEventHandler().onOpen(openEvent);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent closeEvent) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(closeEvent.getInventory());
		if (gui != null) {
			gui.getEventHandler().onClose(closeEvent);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRecipeBookClick(PlayerRecipeBookClickEvent recipeEvent) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(recipeEvent.getPlayer().getOpenInventory().getTopInventory());
		if (gui != null) {
			recipeEvent.setCancelled(true);
		}
	}

}
