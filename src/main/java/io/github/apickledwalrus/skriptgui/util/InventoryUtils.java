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
	 * @return
	 */
	@Nullable
	public static Inventory getOppositeInventory(InventoryView view, Inventory inv) {
		if (view == null || inv == null)
			return null;

		if (view.getTopInventory().equals(inv)) // Opposite of top is bottom.
			return view.getBottomInventory();
		if (view.getBottomInventory().equals(inv)) // Opposite of bottom is top.
			return view.getTopInventory();

		return null;
	}

	/**
	 * The slot of the given {@link Inventory} that given {@link ItemStack} was moved to.
	 * @param invTo The inventory the {@link ItemStack} is was moved to.
	 * @param item The {@link ItemStack} being moved.
	 * @return The slot the item is being moved to, or the first empty slot if it wasn't found.
	 */
	public static int getSlotTo(Inventory invTo, ItemStack item) {
		if (item == null || invTo.first(item.getType()) < 0)
			return invTo.firstEmpty();

		for (int i = invTo.first(item.getType()); i < invTo.getSize(); i++) {
			if (isSimilar(item, invTo.getItem(i)))
				return i;
		}

		return invTo.firstEmpty();
	}

	public static int getInvertedSlotTo(Inventory invTo, ItemStack item) {
		for (int i = 8; i >= 0; i--) {
			if (isSimilar(item, invTo.getItem(i)))
				return i;
		}

		for (int i = invTo.getSize() - 1; i > 8; i--) {
			if (isSimilar(item, invTo.getItem(i)))
				return i;
		}

		return -1;
	}

	/**
	 * @param type The {@link InventoryType} for the {@link Inventory}
	 * @param size The size for the {@link Inventory} (in number of rows)
	 * @param name The name for the {@link Inventory}
	 * @return The created {@link Inventory}
	 */
	public static Inventory newInventory(InventoryType type, @Nullable Integer size, @Nullable String name) {
		size = (size == null || size == 0) ? type.getDefaultSize() : size * 9;

		if (name == null) {
			name = type.getDefaultTitle();
		}

		if (type == InventoryType.CHEST)
			return Bukkit.getServer().createInventory(null, size, name);
		return Bukkit.getServer().createInventory(null, type, name);
	}

	public static boolean isSimilar(ItemStack one, ItemStack two) {
		if (one == null || two == null)
			return false;

		boolean sameMaterial = one.getType() == two.getType();
		boolean sameAmount = one.getAmount() == two.getAmount();
		boolean sameMeta = Bukkit.getItemFactory().equals(one.getItemMeta(), two.getItemMeta());
		boolean sameEnchantments = one.getEnchantments().equals(two.getEnchantments());

		return sameMaterial && sameAmount && sameMeta && sameEnchantments;
	}

}
