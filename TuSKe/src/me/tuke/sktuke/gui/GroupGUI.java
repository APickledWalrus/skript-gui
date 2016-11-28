package me.tuke.sktuke.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;

public class GroupGUI{
	
	private Expression<?>[] items = new Expression<?>[54];
	private Map<Integer, Set<GUI>> slots = new HashMap<Integer, Set<GUI>>();
	
	public Map<Integer, Set<GUI>> getGUIs(){
		return slots;
	}
	public ItemStack[] getArrayItems(Event e){
		ItemStack[] i = new ItemStack[items.length];
		for (int x = 0; x < items.length; x++)
			if (items[x] != null)
				i[x] = (ItemStack) items[x].getSingle(e);
		return i;
	}
	public void newSlot(int slot, Expression<ItemStack> item, GUI gui){
		items[slot] = item;
		if (slots.containsKey(slot))
			slots.get(slot).add(gui);
		else{
			Set<GUI> guis = new HashSet<GUI>();
			guis.add(gui);
			slots.put(slot, guis);
		}
		
	}
}
