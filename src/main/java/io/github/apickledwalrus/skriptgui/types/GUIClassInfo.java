package io.github.apickledwalrus.skriptgui.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import io.github.apickledwalrus.skriptgui.gui.GUI;

public class GUIClassInfo extends ClassInfo<GUI> {

	public GUIClassInfo() {
		super(GUI.class, "guiinventory");
		this.user("gui inventor(y|ies)?")
			.name("GUI")
			.description("Represents a skript-gui GUI")
			.examples("See the GUI creation section.")
			.since("1.0")
			.parser(new GUIParser());
	}

	private static final class GUIParser extends Parser<GUI> {

		@Override
		public boolean canParse(ParseContext context) {
			return false;
		}

		@Override
		public String toString(GUI gui, int flags) {
			return Classes.toString(gui.getInventory().getType()) +
				" gui named " + gui.getName() +
				" with " + gui.getInventory().getSize() / 9 + " rows" +
				" and shape \"" + gui.getRawShape() + "\"";
		}

		@Override
		public String toVariableNameString(GUI gui) {
			return toString(gui, 0);
		}

	}

}
