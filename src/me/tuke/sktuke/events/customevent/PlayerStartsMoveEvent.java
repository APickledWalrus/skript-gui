package me.tuke.sktuke.events.customevent;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerStartsMoveEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	private Player p;
	private Location l;
	
	public PlayerStartsMoveEvent(Player p, Location l) {
		this.p = p;
		this.l = l;
	}

	public Player getPlayer(){
		return p;
	}
	
	public Location getStartLocation(){
		return l;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}