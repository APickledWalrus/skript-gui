package io.github.apickledwalrus.skriptgui.gui;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class GUIEventHandler {

	private GUI gui;
	private boolean isStarted = false;

	public GUIEventHandler(GUI gui) {
		this.gui = gui;
	}

	public void onEvent(Event event) {

		if (event instanceof InventoryClickEvent && !((InventoryClickEvent) event).isCancelled()) {
			InventoryClickEvent e = (InventoryClickEvent) event;

			if (!isAllowedType(e.getClick()))
				return;

			Inventory clickedInv = e.getClickedInventory();
			if (clickedInv == null)
				return;

			Inventory oppositeInv = InventoryUtils.getOppositeInventory(e.getView(), clickedInv);
			if (oppositeInv == null || !clickedInv.equals(gui) && !oppositeInv.equals(gui))
				return;

			int slot = e.getSlot();

			switch (e.getAction()) {
				case MOVE_TO_OTHER_INVENTORY:
					if (oppositeInv.equals(gui)) {
						clickedInv = oppositeInv;
						slot = InventoryUtils.getSlotTo(oppositeInv, e.getCurrentItem());
					}
					break;
				case COLLECT_TO_CURSOR:
					clickedInv = gui;
					slot = InventoryUtils.getSlotTo(clickedInv, e.getCursor());
					break;
				case HOTBAR_SWAP:
				case HOTBAR_MOVE_AND_READD:
					if (gui.getType().equals(InventoryType.PLAYER)) {
						clickedInv = gui;
						slot = e.getHotbarButton();
					}
					break;
				default:
					break;
			}

			if (clickedInv.equals(gui))
				onClick(e, slot);

			return;
		}

		if (event instanceof InventoryOpenEvent) {
			InventoryOpenEvent e = (InventoryOpenEvent) event;
			if (e.getInventory().equals(gui)) {
				onOpen(e);
			}
			return;
		}

		if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (e.getInventory().equals(gui)) {
				if (e.getViewers().size() == 1) // Only stop the listener when there are no viewers remaining.
					Bukkit.getScheduler().runTaskLater(SkriptGUI.getInstance(), this::stop, 1L);
				onClose(e);
			}
			return;
		} 

		if (event instanceof InventoryDragEvent) {
			InventoryDragEvent e = (InventoryDragEvent) event;
			if (e.getInventory().equals(gui)) {
				for (int slot : e.getRawSlots()) {
					slot = e.getView().convertSlot(slot);
					onDrag(e, slot);
					if (e.isCancelled())
						break;
				}
			}
		}

	}

	public void setGUI(GUI gui) {
		this.gui = gui;
	}

	public void start() {
		if (!isStarted()) {
			isStarted = true;
			SkriptGUIEvent.getInstance().register(this);
		}
	}

	public void stop() { // TODO investigate comment
		// In Global GUIs, someone can try to open a GUI really fast, so let's make sure first.
		if (isStarted() && gui.getViewers().size() == 0) {
			SkriptGUIEvent.getInstance().unregister(this);
			isStarted = false;
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	private boolean isAllowedType(ClickType ct) {
		switch(ct) {
			case UNKNOWN:
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return false;
			default:
				return true;
		}
	}

	public void finalize() {
		gui.clear();
	}

	public abstract void onClick(InventoryClickEvent e, int slot);
	public abstract void onDrag(InventoryDragEvent e, int slot);
	public abstract void onOpen(InventoryOpenEvent e);
	public abstract void onClose(InventoryCloseEvent e);

}
