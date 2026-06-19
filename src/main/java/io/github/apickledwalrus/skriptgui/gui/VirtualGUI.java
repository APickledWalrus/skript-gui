package io.github.apickledwalrus.skriptgui.gui;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class VirtualGUI extends GUI {

	private Component name;

	public VirtualGUI(Inventory inventory, boolean changeable, @Nullable Component name, @Nullable String layout) {
		super(inventory, changeable, layout);
		this.inventory = inventory;
		this.name = name == null ? inventory.getType().defaultTitle() : name;
	}

	@Override
	public void open(Player player) {
		player.openInventory(inventory);
	}

	@Override
	public Component getName() {
		return name;
	}

	@Override
	public void setName(@Nullable Component name) {
		changeInventory(inventory.getSize(), name);
	}

	@Override
	public void setSize(int size) {
		changeInventory(size, name);
	}

	private void changeInventory(int size, @Nullable Component name) {
		if (name == null) {
			name = inventory.getType().defaultTitle();
		} else if (size < 9) { // Minimum size
			size = 9;
		} else if (size > 54) { // Maximum size
			size = 54;
		} else if (size % 9 != 0) {
			return;
		}

		if (size == inventory.getSize() && name.equals(this.name)) { // Nothing is actually changing
			return;
		}

		Inventory newInventory;
		if (inventory.getType() == InventoryType.CHEST) {
			newInventory = Bukkit.getServer().createInventory(null, size, name);
		} else {
			newInventory = Bukkit.getServer().createInventory(null, inventory.getType(), name);
		}

		if (size >= inventory.getSize()) {
			newInventory.setContents(inventory.getContents());
		} else { // The inventory is shrinking
			for (int slot = 0; slot < size; slot++) {
				newInventory.setItem(slot, inventory.getItem(slot));
			}
		}

		eventHandler.pause(); // Don't process any events as we transfer data and players

		for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) {
			ItemStack cursor = viewer.getItemOnCursor();
			viewer.setItemOnCursor(null);
			viewer.openInventory(newInventory);
			viewer.setItemOnCursor(cursor);
		}
		SkriptGUI.getGUIManager().transferRegistration(this, newInventory);
		inventory = newInventory;
		this.name = name;

		eventHandler.resume(); // It is safe to resume operations
	}

}
