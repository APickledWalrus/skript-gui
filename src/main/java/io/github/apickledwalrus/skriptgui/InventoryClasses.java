package io.github.apickledwalrus.skriptgui;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.EnumSerializer;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.EnumUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryClasses {

	public InventoryClasses() {

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
				@NotNull
				public String toString(GUI gui, int flags) {
					return gui.getType().getDefaultTitle().toLowerCase()
							+ " gui named " + gui.getName() 
							+ " with " + gui.getSize() / 9 + " rows"
							+ " and shape " + gui.getRawShape();
				}

				@Override
				@NotNull
				public String toVariableNameString(GUI gui) {
					return toString(gui, 0);
				}

				@Override
				@NotNull
				public String getVariableNamePattern() {
					return ".+";
				}
			})
		);

		if (Classes.getExactClassInfo(SlotType.class) == null) {
			@SuppressWarnings("NullableProblems") EnumUtils<SlotType> slotTypes = new EnumUtils<>(SlotType.class, "slot types");
			Classes.registerClass(new ClassInfo<>(SlotType.class, "slottype")
				.user("slot types?")
				.name("Slot Types")
				.description("Represents the slot type in an Inventory Click Event.")
				.examples(slotTypes.getAllNames())
				.since("1.0.0")
				.parser(new Parser<SlotType>() {
					@Override
					@Nullable
					public SlotType parse(String expr, ParseContext context) {
						return slotTypes.parse(expr);
					}

					@Override
					public boolean canParse(ParseContext ctx) {
						return true;
					}

					@Override
					@NotNull
					public String toString(SlotType type, int flags) {
						return slotTypes.toString(type, flags);
					}

					@Override
					@NotNull
					public String toVariableNameString(SlotType type) {
						return "slottype:" + type.name();
					}

					@Override
					@NotNull
					public String getVariableNamePattern() {
						return "slottype:\\S+";
					}
				})
				.serializer(new EnumSerializer<>(SlotType.class)
			));
		}

	}

}
