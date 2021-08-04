package io.github.apickledwalrus.skriptgui.util;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

public class InventoryUtils {

	/**
	 * Returns the opposite inventory from the given {@link InventoryView} and {@link Inventory}.
	 * For example, if the given inventory is the TopInventory, then the BottomInventory will be returned.
	 * @param view The {@link InventoryView} to be used for comparison.
	 * @param inv The {@link Inventory} to get the opposite of.
	 * @return The opposite inventory
	 */
	@Nullable
	public static Inventory getOppositeInventory(InventoryView view, Inventory inv) {
		if (view.getTopInventory().equals(inv)) { // Opposite of top is bottom.
			return view.getBottomInventory();
		}

		if (view.getBottomInventory().equals(inv)) { // Opposite of bottom is top.
			return view.getTopInventory();
		}

		return null;
	}

	/**
	 * The slot of the given {@link Inventory} that given {@link ItemStack} was moved to.
	 * @param invTo The inventory the {@link ItemStack} is was moved to.
	 * @param item The {@link ItemStack} being moved.
	 * @return The slot the item is being moved to, or the first empty slot if it wasn't found.
	 */
	public static int getSlotTo(Inventory invTo, @Nullable ItemStack item) {
		if (item == null || invTo.first(item.getType()) < 0) {
			return invTo.firstEmpty();
		}

		for (int i = invTo.first(item.getType()); i < invTo.getSize(); i++) {
			if (item.equals(invTo.getItem(i))) {
				return i;
			}
		}

		return invTo.firstEmpty();
	}

	/**
	 * @param type The {@link InventoryType} for the {@link Inventory}
	 * @param size The size for the {@link Inventory} (in number of rows)
	 * @param name The name for the {@link Inventory}
	 * @return The created {@link Inventory}
	 */
	public static Inventory newInventory(InventoryType type, @Nullable Integer size, @Nullable String name) {
		size = size == null ? type.getDefaultSize() : size;
		name = name == null ? type.getDefaultTitle() : name;

		if (type == InventoryType.CHEST) {
			return Bukkit.getServer().createInventory(null, size, name);
		}

		return Bukkit.getServer().createInventory(null, type, name);
	}

}
