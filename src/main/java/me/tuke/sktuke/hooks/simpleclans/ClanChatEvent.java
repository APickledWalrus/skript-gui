package me.tuke.sktuke.hooks.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Tuke_Nuke on 27/04/2017
 */
public class ClanChatEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private ClanPlayer sender;
	private String message;

	public ClanChatEvent(ClanPlayer player, String msg) {
		sender = player;
		message = msg;
	}

	public ClanPlayer getClanPlayer() {
		return sender;
	}
	public String getMessage() {
		return message;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
