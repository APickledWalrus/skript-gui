package io.github.apickledwalrus.skriptgui.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;

import java.util.ArrayList;
import java.util.List;

public abstract class GUIEventHandler {

	/**
	 * To be used to control whether this event handler processes an event.
	 */
	private boolean paused;

	/**
	 * To be used to control whether this event handler processes an event for a specific player.
	 */
	private final List<Player> pausedFor = new ArrayList<>();

	/**
	 * Resumes handling of events for this handler.
	 */
	public void resume() {
		paused = false;
	}

	/**
	 * Resumes handling of events for this handler for the given player.
	 */
	public void resume(Player player) {
		pausedFor.remove(player);
	}

	/**
	 * Pauses handling of events for this handler.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Pauses handling of events for this handler for the given player.
	 */
	public void pause(Player player) {
		pausedFor.add(player);
	}

	/**
	 * @return Whether this event handler is processing events.
	 * True if not processing, false if processing.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param player The player to check for.
	 * @return Whether this event handler is processing events for the given player.
	 * If event processing is globally disabled, this method will reflect that.
	 * True if not processing, false if processing.
	 */
	public boolean isPaused(Player player) {
		return isPaused() || pausedFor.contains(player);
	}

	public abstract void onClick(InventoryClickEvent e);
	public abstract void onChange(InventoryClickEvent e);
	public abstract void onDrag(InventoryDragEvent e);
	public abstract void onOpen(InventoryOpenEvent e);
	public abstract void onClose(InventoryCloseEvent e);

	public void onChange(InventoryDragEvent e) {
		for (int slot : e.getInventorySlots()) {
			InventoryClickEvent clickEvent = new InventoryClickEvent(
					e.getView(),
					e.getView().getSlotType(slot),
					slot,
					ClickType.UNKNOWN,
					InventoryAction.UNKNOWN
			);
			onChange(clickEvent);
		}
	}

}
