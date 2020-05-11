package io.github.apickledwalrus.skriptgui.elements.expressions;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

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
import io.github.apickledwalrus.skriptgui.util.EffectSection;

@Name("GUI Properties")
@Description("Different properties of the GUI. They can be modified.")
@Examples({"edit gui last gui:",
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
	private int shapeMode;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (!EffectSection.isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't change or get the GUI properties outside of a GUI creation section.");
			return false;
		}
		pattern = matchedPattern;
		shapeMode = parseResult.mark;
		return true;
	}

	@Override
	@Nullable
	protected Object[] get(Event e) {
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
		if (gui != null) {
			switch (pattern) {
				case 0: return new String[]{gui.getName()};
				case 1: return new Number[]{gui.getInventory().getSize()};
				case 2: return new String[]{gui.getRawShape()};
				case 3: return new Boolean[]{!gui.getStealable()};
			}
		}
		return new Object[]{};
	}

	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
			switch (pattern) {
				case 0: return CollectionUtils.array(String.class);
				case 1: return CollectionUtils.array(Number.class);
				case 2: return CollectionUtils.array(String[].class);
				case 3: return CollectionUtils.array(Boolean.class);
			}
		}
		return null;
	}

	public void change(final Event e, Object[] delta, ChangeMode mode) {
		if (delta == null || delta.length < 1 || (mode != ChangeMode.SET && mode != ChangeMode.RESET))
			return;
		GUI gui = SkriptGUI.getGUIManager().getGUIEvent(e);
		switch (pattern) {
			case 0: gui.setName((String) delta[0]);
			case 1: gui.setSize(((Number) delta[0]).intValue());
			case 2: gui.setShape(false, shapeMode == 2, (String[]) delta[0]);
			case 3: gui.setStealable(!(Boolean) delta[0]);
		}
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends Object> getReturnType() {
		switch (pattern) {
			case 1: return String.class;
			case 2: return Number.class;
			case 3: return String.class;
			case 4: return Boolean.class;
			default: return Object.class;
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		switch (pattern) {
			case 0: return "the gui inventory name";
			case 1: return "the total number of gui rows";
			case 2: return "the gui shape of " + (shapeMode == 2 ? "actions" : "items");
			case 3: return "the gui lock status";
			default: return "gui properties";
		}
	}

}
