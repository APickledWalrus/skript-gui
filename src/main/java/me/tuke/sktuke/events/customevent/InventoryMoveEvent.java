package me.tuke.sktuke.events.customevent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryMoveEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	private Player p;
	private ItemStack i;
	private Inventory invTo;
	private Inventory invFrom;
	private String clickType;
	private int slotFrom;
	private int slotTo;
	private boolean cancel = false;
	
	public InventoryMoveEvent(Player p, ItemStack i, String clickType, Inventory invFrom, Inventory invTo, int slotFrom, int slotTo) {
		this.p = p;
		this.invFrom = invFrom;
		this.invTo = invTo;
		this.slotFrom = slotFrom;
		this.slotTo = slotTo;
		this.i = i;
		this.clickType = clickType;
	}

	public Player getPlayer(){
		return p;
	}
	
	public ItemStack getItem(){
		return i;
	}
	public String getClickType(){
		return clickType;
	}
	public int getSlotFrom(){
		return slotFrom;
	}
	public int getSlotTo(){
		return slotTo;
	}
	public Inventory getInventoryFrom(){
		return invFrom;
	}
	public Inventory getInventoryTo(){
		return invTo;
	}
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean value) {
		cancel = value;
		
	}
}