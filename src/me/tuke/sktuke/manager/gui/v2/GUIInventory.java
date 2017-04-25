package me.tuke.sktuke.manager.gui.v2;

import java.util.*;
import java.util.function.Consumer;

import me.tuke.sktuke.TuSKe;
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

	private String rawShape;
	private Map<Character, Consumer<InventoryClickEvent>> slots = new HashMap<>();
	private Map<Character, ItemStack> items = new HashMap<>();
	private Consumer<InventoryCloseEvent> onClose;
	private Inventory inv;
	private SkriptGUIEvent event;
	private boolean isLocked = false;

	public GUIInventory(Inventory inv){
		this.inv = inv;
	}

	public GUIInventory shape(String... shapes){
		StringBuilder sb = new StringBuilder();
		for (String shape : shapes)
			sb.append(shape);
		while (sb.length() < inv.getSize())
			sb.append(' ');
		rawShape = sb.toString();
		return this;
	}
	public GUIInventory shapeDefault(){
		StringBuilder sb = new StringBuilder();
		for (char c = 'A'; c < inv.getSize() + 'A'; c++)
			sb.append(c);
		rawShape = sb.toString();
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
	public GUIInventory changeProperties(String newName, Integer newSize, String newRawShape, int shapeMode){
		ItemStack[] copy = inv.getContents();
		if (newRawShape != null) {
			if (shapeMode < 2) {
				ItemStack[] newItems = copy.clone();
				int length = newSize == null ? newItems.length : 9 * newSize.intValue();
				copy = new ItemStack[length];
				int x = 0;
				Map<Character, ItemStack> items = new HashMap<>();
				for (char ch1 : rawShape.toCharArray())
					if (x < newItems.length)
						items.put(ch1, newItems[x++]);

				x = 0;
				for (char ch : newRawShape.toCharArray()) {
					ItemStack item = items.get(ch);
					if (item != null && x < copy.length) {
						copy[x] = item;
					}
					x++;
				}

			}
			if (shapeMode % 2 == 0) {
				/*Map<Character, Consumer<InventoryClickEvent>> newSlotsAction = new HashMap<>();
				for (char ch : newRawShape.toCharArray()) {
					Consumer<InventoryClickEvent> action = slots.get(ch);
					if (action != null)
						newSlotsAction.put(ch, action);
				}
				slots = newSlotsAction;*/
				rawShape = newRawShape;
			}
		}
		if (newName != null && newSize != null) {
			List<HumanEntity> viewers = new ArrayList<>(inv.getViewers());
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
		//If the inventory is a virtual inventory, it will set them later
		//If it is a inventory from somwhere (like a block) or has someone already viewing it
		//It will set the slot them.
		if (event == null && (inv.getHolder() == null || inv.getViewers().size() == 0)) {
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
		if (ch !=  null)
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
