package me.tuke.sktuke.util;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
public class InventoryUtils {

	private static boolean hasClickMethod = ReflectionUtils.hasMethod(InventoryClickEvent.class, "getClickedInventory");

	/**
	 * KCaldron and other jar's doesn't have this method. Just a Util to check that.
	 * @param event - The {@link InventoryClickEvent}
	 * @return The clicked inventory.
	 */
	public static Inventory getClickedInventory(InventoryClickEvent event){
		if (hasClickMethod)
			return event.getClickedInventory();
		else if (event.getRawSlot() < 0)
			return null;
		else if ((event.getView().getTopInventory() != null) && (event.getRawSlot() < event.getView().getTopInventory().getSize()))
			return event.getView().getTopInventory();
		else
			return event.getView().getBottomInventory();
	}

	public static int getSlotTo(Inventory invTo, ItemStack i){
		if (i != null && invTo.first(i.getType()) >= 0)
			for (int x = invTo.first(i.getType()); x < invTo.getSize(); x++)
				if (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getData()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize())
					return x;

		return invTo.firstEmpty();
	}
	public static int getInvertedSlotTo(Inventory invTo, ItemStack i){
		for (int x = 8; x >= 0; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getDurability()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		for (int x = invTo.getSize() -1; x > 8; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getDurability()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		return -1;
	}

	public static Inventory newInventory(InventoryType type, Integer size, String name) {
		if (size == null)
			size = type.getDefaultSize();
		else
			size *= 9;
		if (name == null)
			name = type.getDefaultTitle();
		else if (name.length() > 32)
			name = name.substring(0, 32);
		switch (type) {
			case BEACON:
			case MERCHANT:
			case CRAFTING:
			case CREATIVE:
				return null;
			case CHEST:
				return Bukkit.getServer().createInventory(null, size, name);
			case DROPPER:
				type = InventoryType.DISPENSER;
			default:
				return Bukkit.getServer().createInventory(null, type, name);
		}
	}
}
