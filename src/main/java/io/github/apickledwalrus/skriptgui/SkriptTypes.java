package io.github.apickledwalrus.skriptgui;

import org.bukkit.event.inventory.InventoryType.SlotType;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumSerializer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.EnumUtils;

import io.github.apickledwalrus.skriptgui.gui.GUI;

public class SkriptTypes {

	public static void register() {

		Classes.registerClass(new ClassInfo<>(GUI.class, "guiinventory")
			.user("gui inventor(y|ies)?")
			.name("GUI")
			.description("Represents a skript-gui GUI")
			.examples("See the GUI creation section.")
			.since("1.0")
			.parser(new Parser<GUI>() {
				@Override
				public boolean canParse(ParseContext ctx) {
					return false;
				}

				@Override
				public String toString(GUI gui, int flags) {
					return gui.getInventory().getType().getDefaultTitle().toLowerCase()
							+ " gui named " + gui.getName() 
							+ " with " + gui.getInventory().getSize() / 9 + " rows"
							+ " and shape " + gui.getRawShape();
				}

				@Override
				public String toVariableNameString(GUI gui) {
					return toString(gui, 0);
				}

				@Override
				public String getVariableNamePattern() {
					return ".+";
				}
			})
		);

		if (Classes.getExactClassInfo(SlotType.class) == null) {
			EnumUtils<SlotType> slotTypes = new EnumUtils<>(SlotType.class, "slot types");
			Classes.registerClass(new ClassInfo<>(SlotType.class, "slottype")
				.user("slot types?")
				.name("Slot Types")
				.description("Represents the slot type in an Inventory Click Event.")
				.examples(slotTypes.getAllNames())
				.since("1.0.0")
				.parser(new Parser<SlotType>() {
					@Override
					public boolean canParse(ParseContext ctx) {
						return true;
					}

					@Nullable
					@Override
					public SlotType parse(String expr, ParseContext context) {
						return slotTypes.parse(expr);
					}

					@Override
					public String toString(SlotType type, int flags) {
						return slotTypes.toString(type, flags);
					}

					@Override
					public String toVariableNameString(SlotType type) {
						return "slottype:" + type.name();
					}

					@Override
					public String getVariableNamePattern() {
						return "\\S+";
					}
				})
				.serializer(new EnumSerializer<>(SlotType.class)
			));
		}

	}

}