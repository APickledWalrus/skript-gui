package io.github.apickledwalrus.skriptgui;

import ch.njol.skript.registrations.Converters;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.inventory.Inventory;

public class SkriptConverters {

	public SkriptConverters() {

		Converters.registerConverter(GUI.class, Inventory.class, GUI::getInventory);

	}

}
