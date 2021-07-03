package io.github.apickledwalrus.skriptgui.gui;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
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

			Inventory guiInventory = gui.getInventory();

			Inventory oppositeInv = InventoryUtils.getOppositeInventory(e.getView(), clickedInv);
			if (oppositeInv == null || !clickedInv.equals(guiInventory) && !oppositeInv.equals(guiInventory))
				return;

			int slot = e.getSlot();

			switch (e.getAction()) {
				case MOVE_TO_OTHER_INVENTORY:
					if (oppositeInv.equals(guiInventory)) {
						clickedInv = oppositeInv;
						slot = InventoryUtils.getSlotTo(oppositeInv, e.getCurrentItem());
					}
					break;
				case COLLECT_TO_CURSOR:
					clickedInv = guiInventory;
					slot = InventoryUtils.getSlotTo(clickedInv, e.getCursor());
					break;
				case HOTBAR_SWAP:
				case HOTBAR_MOVE_AND_READD:
					if (gui.getInventory().getType().equals(InventoryType.PLAYER)) {
						clickedInv = guiInventory;
						slot = e.getHotbarButton();
					}
					break;
				default:
					break;
			}

			if (clickedInv.equals(guiInventory))
				onClick(e, slot);

			return;
		}

		if (event instanceof InventoryOpenEvent) {
			InventoryOpenEvent e = (InventoryOpenEvent) event;
			if (e.getInventory().equals(gui.getInventory())) {
				onOpen(e);
			}
			return;
		}

		if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (e.getInventory().equals(gui.getInventory())) {
				onClose(e);
			}
			return;
		} 

		if (event instanceof InventoryDragEvent) {
			InventoryDragEvent e = (InventoryDragEvent) event;
			if (e.getInventory().equals(gui.getInventory())) {
				for (int slot : e.getRawSlots()) {
					slot = e.getView().convertSlot(slot);
					onDrag(e, slot);
					if (e.isCancelled())
						break;
				}
			}
			return;
		}

		if (SkriptGUIEvent.HAS_RECIPE_EVENT && event instanceof PlayerRecipeBookClickEvent) {
			PlayerRecipeBookClickEvent e = (PlayerRecipeBookClickEvent) event;
			if (e.getPlayer().getOpenInventory().getTopInventory().equals(gui.getInventory())) {
				// We don't want this event going through (see https://github.com/APickledWalrus/skript-gui/issues/33)
				((PlayerRecipeBookClickEvent) event).setCancelled(true);
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

	public void stop() {
		SkriptGUIEvent.getInstance().unregister(this);
		isStarted = false;
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
