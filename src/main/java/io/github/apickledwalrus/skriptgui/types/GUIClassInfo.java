package io.github.apickledwalrus.skriptgui.types;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.lang.properties.Property;
import org.skriptlang.skript.lang.properties.handlers.base.ExpressionPropertyHandler;

@SuppressWarnings("UnstableApiUsage")
public class GUIClassInfo extends ClassInfo<GUI> {

	public GUIClassInfo(SkriptAddon source) {
		super(GUI.class, "gui");
		this.user("gui(?:s| inventor(?:y|ies))?")
			.name("GUI")
			.description("Represents a skript-gui GUI")
			.examples("See the GUI creation section.")
			.since("1.0.0")
			.parser(new GUIParser())
			.changer(new GUIChanger())
			.property(Property.NAME,
				"A GUI's name. Can be set, cleared, or reset.",
				source,
				new GUINameHandler())
			.property(Property.SIZE,
				"A GUI's size, which represents the number of slots it has. Can be set or reset.",
				source,
				new GUISizeHandler());
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
				" and layout \"" + gui.getLayout() + "\"";
		}

		@Override
		public String toVariableNameString(GUI gui) {
			return toString(gui, 0);
		}

	}

	private static final class GUIChanger implements Changer<GUI> {

		@Override
		public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
			if (mode == ChangeMode.DELETE) {
				return CollectionUtils.array();
			}
			return null;
		}

		@Override
		public void change(GUI[] guis, Object @Nullable [] delta, ChangeMode mode) {
			for (GUI gui : guis) {
				gui.setID(null);
			}
		}

	}

	private static final class GUINameHandler implements ExpressionPropertyHandler<GUI, Component> {

		@Override
		public Component convert(GUI gui) {
			return gui.getName();
		}

		@Override
		public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
			return switch (mode) {
				case SET, RESET, DELETE -> new Class[]{Component.class};
				default -> null;
			};
		}

		@Override
		public void change(GUI gui, Object @Nullable [] delta, ChangeMode mode) {
			assert mode != ChangeMode.SET || delta != null;
			Component name = switch (mode) {
				case SET -> (Component) delta[0];
				case DELETE -> Component.empty();
				case RESET -> gui.getInventory().getType().defaultTitle();
				default -> throw new IllegalArgumentException("Unsupported change mode for GUI rename");
			};
			gui.setName(name);
		}

		@Override
		public @NotNull Class<Component> returnType() {
			return Component.class;
		}

	}

	private static final class GUISizeHandler implements ExpressionPropertyHandler<GUI, Integer> {

		@Override
		public Integer convert(GUI gui) {
			return gui.getInventory().getSize();
		}

		@Override
		public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
			if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
				return new Class[]{Integer.class};
			}
			return null;
		}

		@Override
		public void change(GUI gui, Object @Nullable [] delta, ChangeMode mode) {
			int size = delta == null ? gui.getInventory().getType().getDefaultSize() : (int) delta[0];
			gui.setSize(size);
		}

		@Override
		public @NotNull Class<Integer> returnType() {
			return Integer.class;
		}

	}

}
