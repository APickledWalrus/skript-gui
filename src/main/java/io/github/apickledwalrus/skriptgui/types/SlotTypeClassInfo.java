package io.github.apickledwalrus.skriptgui.types;

import ch.njol.skript.classes.EnumClassInfo;
import org.bukkit.event.inventory.InventoryType.SlotType;

public class SlotTypeClassInfo extends EnumClassInfo<SlotType> {

	public SlotTypeClassInfo() {
		super(SlotType.class, "slottype", "slot types");
		this.user("slot types?")
			.name("Slot Types")
			.description("Represents the slot type in an Inventory Click Event.")
			.since("1.0.0");
	}

}
