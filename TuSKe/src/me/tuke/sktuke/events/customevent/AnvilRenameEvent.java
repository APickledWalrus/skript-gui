package me.tuke.sktuke.events.customevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class AnvilRenameEvent extends Event implements Cancellable{
	   private static final HandlerList handlers = new HandlerList();
	    private Inventory inv;
	    private Player p;
	    private boolean cancelled;

	    public AnvilRenameEvent(Player p, Inventory inv) {
	        this.inv = inv;
	        this.p = p;
	    }

	    public Inventory getInventory() {
	        return inv;
	    }
		public Player getPlayer(){
			return p;
		}

	    public boolean isCancelled() {
	        return cancelled;
	    }

	    public void setCancelled(boolean cancel) {
	        cancelled = cancel;
	    }

	    public HandlerList getHandlers() {
	        return handlers;
	    }

	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
	}