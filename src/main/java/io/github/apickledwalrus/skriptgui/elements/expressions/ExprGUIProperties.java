package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.elements.sections.SecCreateGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("GUI Properties")
@Description("Different properties of a GUI. They can be modified.")
@Examples({
		"edit gui last gui:",
		"\tset the name of the edited gui to \"New GUI Name!\"",
		"\tset the rows of the edited gui to 3 # Sets the number of rows to 3 (if possible)",
		"\tset the shape of the edited gui to \"xxxxxxxxx\", \"x-------x\", and \"xxxxxxxxx\"",
		"\tset the lock status of the edited gui to false # Players can take items from this GUI now"
})
@Since("1.0.0, 1.3 (rework, support outside of edit sections)")
public class ExprGUIProperties extends SimplePropertyExpression<GUI, Object> {

	static {
		register(ExprGUIProperties.class, Object.class, "(name[s]|(size[s]|rows)|shape[s]|lock status[es])", "guiinventorys");
	}

	private Property property;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentSection(SecCreateGUI.class)) {
			Skript.error("You can't change or get the GUI properties outside of a GUI creation or editing section.");
			return false;
		}

		switch (matchedPattern) {
			case 1:
			case 2:
			case 3:
				property = Property.NAME;
				break;
			case 4:
			case 5:
			case 6:
				property = Property.ROWS;
				break;
			case 7:
			case 8:
			case 9:
				property = Property.SHAPE;
				break;
			case 10:
			case 11:
			case 12:
				property = Property.LOCK_STATUS;
				break;
		}

		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@NotNull
	protected String getPropertyName() {
		switch (property) {
			case NAME:
				return "name";
			case ROWS:
				return "size";
			case SHAPE:
				return "shape";
			case LOCK_STATUS:
				return "lock status";
			default:
				return "property";
		}
	}

	@Override
	public Object convert(GUI gui) {
		switch (property) {
			case NAME:
				return gui.getName();
			case ROWS:
				return gui.getInventory().getSize() / 9; // We return rows
			case SHAPE:
				return gui.getRawShape();
			case LOCK_STATUS:
				return !gui.isStealable(); // Not stealable = locked
		}
		return null;
	}

	@Override
	@Nullable
	@SuppressWarnings("NullableProblems")
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET) {
			switch (property) {
				case NAME:
					return CollectionUtils.array(String.class);
				case ROWS:
					return CollectionUtils.array(Number.class);
				case SHAPE:
					return CollectionUtils.array(String[].class);
				case LOCK_STATUS:
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
					switch (property) {
						case NAME:
							gui.setName((String) delta[0]);
							break;
						case ROWS:
							gui.setSize(((Number) delta[0]).intValue() * 9);
							break;
						case SHAPE:
							gui.setShape((String[]) delta);
							break;
						case LOCK_STATUS:
							gui.setStealableItems(!(Boolean) delta[0]);
							break;
					}
					break;
				case RESET:
					switch (property) {
						case NAME:
							gui.setName(gui.getInventory().getType().getDefaultTitle());
							break;
						case ROWS:
							gui.setSize(gui.getInventory().getType().getDefaultSize());
							break;
						case SHAPE:
							gui.resetShape();
							break;
						case LOCK_STATUS:
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
	@NotNull
	public Class<?> getReturnType() {
		switch (property) {
			case NAME:
			case SHAPE:
				return String.class;
			case ROWS:
				return Number.class;
			case LOCK_STATUS:
				return Boolean.class;
			default:
				return Object.class;
		}
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		switch (property) {
			case NAME:
				return "the name of " + getExpr().toString(e, debug);
			case ROWS:
				return "the rows of " + getExpr().toString(e, debug);
			case SHAPE:
				return "the shape of " + getExpr().toString(e, debug);
			case LOCK_STATUS:
				return "the lock status of "  + getExpr().toString(e, debug);
			default:
				return "gui properties";
		}
	}

	private enum Property {
		NAME,
		ROWS,
		SHAPE,
		LOCK_STATUS
	}

}
