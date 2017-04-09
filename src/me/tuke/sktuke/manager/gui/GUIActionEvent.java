package me.tuke.sktuke.manager.gui;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIActionEvent extends Event implements Cancellable{
	private static HandlerList handlers = new HandlerList();
	private final InventoryClickEvent e;
	private boolean cancelled = false;
	
	public GUIActionEvent(final InventoryClickEvent e){
		this.e = e;
	}
	
	public final InventoryClickEvent getClickEvent(){
		return e;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean value) {
		cancelled = value;
		
	}

}
