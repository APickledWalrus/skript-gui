package io.github.apickledwalrus.skriptgui;

import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.inventory.Inventory;
import org.skriptlang.skript.lang.converter.Converters;

public class SkriptConverters {

	public SkriptConverters() {

		Converters.registerConverter(GUI.class, Inventory.class, GUI::getInventory);

	}

}
