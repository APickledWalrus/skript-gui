package com.github.tukenuke.tuske.register;

import ch.njol.skript.classes.Converter;
import ch.njol.skript.registrations.Converters;
import com.github.tukenuke.tuske.manager.gui.v2.GUIInventory;
import org.bukkit.inventory.Inventory;

/**
 * @author Tuke_Nuke on 08/04/2017
 */
public class TuSKeConverters {
	static {
		Converters.registerConverter(GUIInventory.class, Inventory.class, (Converter<GUIInventory, Inventory>)  GUIInventory::getInventory);
	}

}
