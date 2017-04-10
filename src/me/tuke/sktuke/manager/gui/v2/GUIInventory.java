package me.tuke.sktuke.manager.gui.v2;

import java.util.*;
import java.util.function.Consumer;

import me.tuke.sktuke.expressions.gui.ExprVirtualInv;
import me.tuke.sktuke.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Tuke_Nuke on 16/02/2017
 */
public class GUIInventory {

	private static final Consumer<InventoryClickEvent> nullConsumer = e -> {};

	//It will copy the variables from main event to here
	private String rawShape;
	//private Map<Character, Entry<ItemStack, RunnableEvent<InventoryClickEvent>>> slots = new HashMap<>();
	private Map<Character, Consumer<InventoryClickEvent>> slots = new HashMap<>();
	private Map<Character, ItemStack> items = new HashMap<>();
	private Consumer<InventoryCloseEvent> onClose;
	private Inventory inv;
	private SkriptGUIEvent event;
	private boolean isLocked = false;

	public GUIInventory(Inventory inv){
		this.inv = inv;
	}
	
	public GUIInventory setInventory(Inventory inv){
		if (this.inv.getContents().length > inv.getSize()){
			ItemStack[] newArray = new ItemStack[inv.getSize()];
			ItemStack[] oldArray = this.inv.getContents();
			for (int x = 0; x < newArray.length; x++){
				if (x < inv.getSize())
					newArray[x] = oldArray[x];
			}
			inv.setContents(newArray);			
		} else {
			inv.setContents(this.inv.getContents());
		}
		this.inv = inv;
		return this;		
	}
	
	public GUIInventory shape(String... shapes){
		rawShape = "";
		for (String shape : shapes)
			rawShape = rawShape + shape;
		while (rawShape.length() < inv.getSize())
			rawShape += " ";
		//TuSKe.debug("Shape: -" + rawShape+ "-" );
		return this;
	}
	public GUIInventory shapeDefault(){
		rawShape = "";
		for (char c = 'A'; c < inv.getSize() + 'A'; c++)
			rawShape = rawShape + c;
		return this;
	}
	public String getRawShape(){
		return rawShape;
	}
	public GUIInventory lockSlots() {
		isLocked = !isLocked;
		return this;
	}
	public boolean isSlotsLocked(){
		return isLocked;
	}
	public GUIInventory setItem(Object slot, ItemStack item){
		return setItem(slot, item, nullConsumer);
	}
	public GUIInventory setItem(Object slot, final ItemStack item, Consumer<InventoryClickEvent> con){
		//TuSKe.debug(slot, slot.getClass());
		char ch = 0;
		if (slot instanceof Number)
			ch = convertSlot(((Number) slot).intValue());
		else if (slot instanceof String && !((String) slot).isEmpty())
			ch = ((String) slot).charAt(0);
		else if (slot instanceof Character)
			ch = (Character) slot;
		else { // It will get the next free slot
			ch = nextSlot();
			if (ch == 0) //Ops, couldn't find any free slots
				return this;
		}
		if (ch == '+' && rawShape.contains("+")) {
			char ch2 = 'A';
			while (rawShape.indexOf(ch2) >= 0)
				ch2++;
			rawShape = rawShape.replaceFirst("\\+", ""+ ch2);
			ch = ch2;
		}
		slots.put(ch, con);
		setItem(ch, item);
		return this;
	}
	public GUIInventory changeProperties(String newName, Integer newSize, String newRawShape){
		ItemStack[] copy = inv.getContents();
		if (newRawShape != null) {
			ItemStack[] newItems = copy.clone();
			copy = new ItemStack[newItems.length];
			int x = 0;
			for (char ch1 : newRawShape.toCharArray()) {
				int slot = rawShape.indexOf(ch1);
				if (slot >= 0 && slot < newItems.length && x < copy.length)
					copy[x] = newItems[slot];
				else
					break;
				x++;
			}

			rawShape = newRawShape;
		}
		if (newName != null && newSize != null) {
			List<HumanEntity> viewers = inv.getViewers();
			inv = InventoryUtils.newInventory(inv.getType(), newSize, newName);
			inv.setContents(copy);
			viewers.stream().forEach(human -> {
				ItemStack cursor = human.getItemOnCursor();
				human.setItemOnCursor(null);
				human.openInventory(inv);
				human.setItemOnCursor(cursor);
			});
		} else
			inv.setContents(copy);
		return this;
	}
	private void setItem(char ch, ItemStack item){
		//If the inventory wasn't open yet, it will set it later
		if (event == null) {
			items.put(ch, item);
			return;
		}
		//If it was, it will set now.
		int x = -1;
		for (char ch1 : rawShape.toCharArray()){
			if (++x < inv.getSize() && ch1 == ch)
				inv.setItem(x, item);
		}
	}
	public Consumer<InventoryClickEvent> getSlot(Integer slot){
		return slot != null ? getSlot(convertSlot(slot)) : null;
	}
	public Consumer<InventoryClickEvent> getSlot(Character ch){
		if (ch !=  null && slots.containsKey(ch))
			return slots.get(ch);
		return null;
	}
	public GUIInventory clearSlots(char... chars){
		for (char ch1 : chars){
			int x = -1;
			for (char ch2 : rawShape.toCharArray())	
				if (++x < inv.getSize() && ch1 == ch2)
					inv.clear(x);
			slots.remove(ch1);
		}
		return this;
	}
	public GUIInventory clear(){
		int x = -1;
		for (char ch : rawShape.toCharArray()) {
			if (++x < inv.getSize() && slots.containsKey(ch))
				inv.clear(x);
		}
		slots.clear();
		return this;
	}
	
	public GUIInventory onClose(Consumer<InventoryCloseEvent> run){
		onClose = run;
		return this;
	}
	public Consumer<InventoryCloseEvent> getOnClose(){
		return onClose;
	}
	public boolean hasOnClose(){
		return onClose != null;
	}
	public Inventory getInventory(){
		if (event == null) {
			setListener(new SkriptGUIEvent(this));
			int x = 0;
			for (char ch : rawShape.toCharArray()) {
				ItemStack item = items.get(ch);
				if (item != null && item.getType() != Material.AIR) {
					inv.setItem(x, item);
				}
				x++;
			}
			//It won't be necessary anymore, so just cleaning it
			items.clear();
		}
		return inv;
	}
	public void setListener(SkriptGUIEvent e){
		if ((event == null && e != null) || (event != null && e == null))
			event = e; // It starts or stop the listener
	}

	public char convertSlot(int slot){
		if (slot < rawShape.length())
			return rawShape.charAt(slot);
		return ' ';
	}
	public char convertSlot(String rawShape, int slot){
		if (slot < rawShape.length())
			return rawShape.charAt(slot);
		return ' ';
	}
	public char nextSlot() {
		for (char ch2 : rawShape.toCharArray()) {
			if (!slots.containsKey(ch2)) {
				return ch2;
			}
		}
		return 0;
	}
}
