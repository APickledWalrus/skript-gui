package me.tuke.sktuke.gui;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {
	private boolean toClose = false;
	private boolean toCall = false;
	private ClickType ct = null;
	private ItemStack item = null;
	private Runnable run = null;
	private Inventory inv = null;
	
	public GUI(){
		
	}
	public GUI(Runnable rn, ItemStack item, ClickType ct){
		this.item = item;
		run = rn;
		this.ct = ct;
	}
	
	public Runnable getRunnable(){
		return run;
	}
	public ClickType getClickType(){
		return ct;
	}
	public ItemStack getCursorItem(){
		return item;
	}
	public GUI toCallEvent(boolean value){
		toCall = value;
		return this;
	}
	public GUI toClose(boolean value){
		toClose = value;
		return this;
	}
	public GUI toOpenInventory(Inventory inv){
		this.inv = inv;
		toClose = true;
		return this;
	}
	public GUI withClickType(ClickType ct){
		this.ct = ct;
		return this;
	}
	public GUI withCursorItem(ItemStack item){
		this.item = item;
		return this;
	}
	public Inventory getInventory(){
		return inv;
	}
	public boolean toClose(){
		return toClose;
	}
	
	public boolean toCallEvent(){
		return toCall;
	}
	public boolean toRun(){
		return run != null;
	}
	public boolean runOnlyWith(ItemStack item){
		return (this.item != null) ? (item != null) ? this.item.getType().equals(item.getType()) && this.item.getData().equals(item.getData()) : false : true;
	}
	
	

}
