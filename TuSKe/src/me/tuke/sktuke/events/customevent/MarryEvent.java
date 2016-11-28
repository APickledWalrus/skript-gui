package me.tuke.sktuke.events.customevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MarryEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private Player p1;
	private Player p2;
	private Player priest;

	public MarryEvent(Player Player1, Player Player2){
		this.p1 = Player1;
		this.p2 = Player2;
		
	}
	public MarryEvent(Player Player1, Player Player2, Player Priest){
		this.p1 = Player1;
		this.p2 = Player2;
		this.priest = Priest;
		
	}

	public Player getPlayer1(){
		return this.p1;
	}
	public Player getPlayer2(){
		return this.p2;
	}
	public Player getPriest(){
		return this.priest;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean c) {
		cancel = c;
		
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
