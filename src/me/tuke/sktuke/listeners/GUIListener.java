package me.tuke.sktuke.listeners;

import ch.njol.skript.Skript;
import me.tuke.sktuke.TuSKe;
import me.tuke.sktuke.manager.gui.v2.GUIInventory;
import me.tuke.sktuke.manager.gui.v2.SkriptGUIEvent;
import me.tuke.sktuke.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

/**
 * @author Tuke_Nuke on 27/05/2017
 */
public class GUIListener {
	private GUIInventory gui;
	private boolean isStarted = false;

	public GUIListener(GUIInventory gui) {
		this.gui = gui;
	}

	public void onEvent(Event event) {
		if (event instanceof InventoryClickEvent && !((InventoryClickEvent) event).isCancelled()) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (isAllowedType(e.getClick())){
				Inventory click = InventoryUtils.getClickedInventory(e);
				if (click != null) {
					Inventory op = InventoryUtils.getOpositiveInventory(e.getView(), click);
					if (op == null || !click.equals(gui.getInventory()) && !op.equals(gui.getInventory()))
						return;
					int slot = e.getSlot();
					switch (e.getAction()) {
						case MOVE_TO_OTHER_INVENTORY:
							if (gui.getInventory().equals(op)) {
								click = op;
								slot = InventoryUtils.getSlotTo(op, e.getCurrentItem());
							}
							break;
						case COLLECT_TO_CURSOR:
							click = gui.getInventory();
							slot = InventoryUtils.getSlotTo(click, e.getCursor());
							break;
						case HOTBAR_SWAP:
						case HOTBAR_MOVE_AND_READD:
							if (gui.getInventory().getType().equals(InventoryType.PLAYER)) {
								slot = e.getHotbarButton();
								click = gui.getInventory();
							}
							break;

					}
					if (click.equals(gui.getInventory())) {
						Consumer<InventoryClickEvent> run = gui.getSlot(slot);
						e.setCancelled(run != null || gui.isSlotsLocked());
						if (run != null && slot == e.getSlot() && click.equals(InventoryUtils.getClickedInventory(e))) {
							run.accept(e);
						}
					}
				}
			}
		} else if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (e.getInventory().equals(gui.getInventory())){
				if (e.getViewers().size() == 1) //Only clear when the last one close.
					Bukkit.getScheduler().runTask(TuSKe.getInstance(), this::stop);
				if (gui.hasOnClose()){
					try {
						gui.getOnClose().accept(e);
					} catch (Exception ex){
						if (TuSKe.debug())
							Skript.exception(ex, "A error occurred while closing a Gui");
					}
				}
				//	gui.clear();
			}

		} else if (event instanceof InventoryDragEvent) {
			//event.getRawSlot() < event.getView().getTopInventory().getSize()
			if (((InventoryDragEvent) event).getInventory().equals(gui.getInventory()))
				for (Integer slot : ((InventoryDragEvent) event).getRawSlots())
					if (slot < ((InventoryDragEvent) event).getInventory().getSize()) {
						slot = ((InventoryDragEvent) event).getView().convertSlot(slot);
						if (gui.getSlot(slot) != null) {
							((InventoryDragEvent) event).setCancelled(true);
							break;
						}
					}
		}
	}
	public boolean isStarted() {
		return isStarted;
	}
	public void stop() {
		if (isStarted()) {
			SkriptGUIEvent.getInstance().unregister(this);
			isStarted = false;
		}
	}
	public void start() {
		if (!isStarted()) {
			isStarted = true;
			SkriptGUIEvent.getInstance().register(this);
		}
	}
	private boolean isAllowedType(ClickType ct){
		if (ct != null)
			switch(ct){
				case UNKNOWN:
				case WINDOW_BORDER_RIGHT:
				case WINDOW_BORDER_LEFT:
				case CREATIVE:
					return false;
				default:
					break;
			}
		return true;
	}

	public void finalize() {
		gui.clear();
	}
}
