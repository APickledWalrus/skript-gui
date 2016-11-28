package me.tuke.sktuke.events.customevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DivorceEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private Player p;

	public DivorceEvent(Player Player){
		this.p = Player;
		
	}

	public Player getPlayer(){
		return this.p;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean c) {
		cancel = c;
		
	}
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }

}