package io.github.apickledwalrus.skriptgui.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import io.github.apickledwalrus.skriptgui.gui.GUI.ShapeMode;
import io.github.apickledwalrus.skriptgui.util.EffectSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("GUI Properties")
@Description("Different properties of the GUI. They can be modified.")
@Examples({
		"edit gui last gui:",
		"\tset the gui-inventory-name to \"New GUI Name!\"",
		"\tset the gui-size to 3 # Sets the number of rows to 3 (if possible)",
		"\tset the gui-shape to \"xxxxxxxxx\", \"x-------x\", and \"xxxxxxxxx\"",
		"\tset the gui-lock-status to false # Players can take items from this GUI now"
})
@Since("1.0.0")
public class ExprGUIProperties extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprGUIProperties.class, Object.class, ExpressionType.SIMPLE,
				"[the] gui(-| )[inventory(-| )]name",
				"[the] [total] [(number|amount) of] gui(-| )(size|rows)",
				"[the] gui(-| )shape [of (1¦items|2¦actions)]",
				"[the] gui(-| )lock(-| )status"
		);
	}

	private int pattern;
	private ShapeMode shapeMode;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!EffectSection.isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't change or get the GUI properties outside of a GUI creation or editing section.");
			return false;
		}

		pattern = matchedPattern;

		if (parseResult.mark == 1) {
			shapeMode = ShapeMode.ITEMS;
		} else if (parseResult.mark == 2) {
			shapeMode = ShapeMode.ACTIONS;
		} else {
			shapeMode = ShapeMode.BOTH;
		}

		return true;
	}

	@Override
	protected Object[] get(Event e) {
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
		if (gui != null) {
			switch (pattern) {
				case 0:
					return new String[]{gui.getName()};
				case 1:
					return new Number[]{gui.getInventory().getSize()};
				case 2:
					return new String[]{gui.getRawShape()};
				case 3:
					return new Boolean[]{!gui.isStealable()};
			}
		}
		return new Object[]{};
	}

	@Override
	@Nullable
	@SuppressWarnings("NullableProblems")
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
			switch (pattern) {
				case 0:
					return CollectionUtils.array(String.class);
				case 1:
					return CollectionUtils.array(Number.class);
				case 2:
					return CollectionUtils.array(String[].class);
				case 3:
					return CollectionUtils.array(Boolean.class);
			}
		}
		return null;
	}

	@Override
	public void change(final Event e, Object @Nullable [] delta, ChangeMode mode) {
		if (delta == null || (mode != ChangeMode.SET && mode != ChangeMode.RESET)) {
			return;
		}
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
		if (gui != null) {
			switch (mode) {
				case SET:
					switch (pattern) {
						case 0:
							gui.setName((String) delta[0]);
							break;
						case 1:
							gui.setSize(((Number) delta[0]).intValue());
							break;
						case 2:
							gui.setShape(shapeMode, (String[]) delta);
							break;
						case 3:
							gui.setStealableItems(!(Boolean) delta[0]);
							break;
					}
					break;
				case RESET:
					switch (pattern) {
						case 0:
							gui.setName(gui.getInventory().getType().getDefaultTitle());
							break;
						case 1:
							gui.setSize(gui.getInventory().getType().getDefaultSize());
							break;
						case 2:
							gui.resetShape();
							break;
						case 3:
							gui.setStealableItems(false);
							break;
					}
					break;
				default:
					assert false;
			}
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	@NotNull
	public Class<?> getReturnType() {
		switch (pattern) {
			case 0:
			case 2:
				return String.class;
			case 1:
				return Number.class;
			case 3:
				return Boolean.class;
			default:
				return Object.class;
		}
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		switch (pattern) {
			case 0:
				return "the gui inventory name";
			case 1:
				return "the total number of gui rows";
			case 2:
				return "the gui shape of " + shapeMode.name().toLowerCase();
			case 3:
				return "the gui lock status";
			default:
				return "gui properties";
		}
	}

}
