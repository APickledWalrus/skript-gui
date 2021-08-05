package io.github.apickledwalrus.skriptgui.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class GUIEventHandler {

	public abstract void onClick(InventoryClickEvent e);
	public abstract void onDrag(InventoryDragEvent e);
	public abstract void onOpen(InventoryOpenEvent e);
	public abstract void onClose(InventoryCloseEvent e);

}
