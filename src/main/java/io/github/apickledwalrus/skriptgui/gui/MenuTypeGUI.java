package io.github.apickledwalrus.skriptgui.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Nullable;

public class MenuTypeGUI extends GUI {

	private final InventoryView inventoryView;

	public MenuTypeGUI(InventoryView inventoryView, boolean changeable, @Nullable String layout) {
		super(inventoryView.getTopInventory(), changeable, layout);
		this.inventoryView = inventoryView;
	}

	@Override
	public void open(Player player) {
		player.openInventory(inventoryView);
	}

	@Override
	public Component getName() {
		return inventoryView.title();
	}

	@Override
	public void setName(@Nullable Component name) {
		// TODO support
	}

	@Override
	public void setSize(int size) {
		// TODO support
	}

}
