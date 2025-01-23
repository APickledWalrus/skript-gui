package io.github.apickledwalrus.skriptgui.elements.expressions;

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
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
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
		register(ExprGUIProperties.class, Object.class, "[[skript-]gui] (0:name[s]|1:(size[s]|rows)|2:shape[s]|3:lock status[es])", "guiinventorys");
	}

	private enum Property {
		NAME,
		ROWS,
		SHAPE,
		LOCK_STATUS
	}

	private Property property;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		property = Property.values()[parseResult.mark];
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	@Nullable
	public Object convert(GUI gui) {
		switch (property) {
			case NAME:
				return gui.getName();
			case ROWS:
				return gui.getInventory().getSize() / 9; // We return rows
			case SHAPE:
				return gui.getRawShape();
			case LOCK_STATUS:
				return !gui.isRemovable(); // Not removable = locked
			default:
				return null;
		}
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		switch (mode) {
			case SET:
			case RESET:
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
			default:
				return null;
		}
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		if (gui == null) {
			return;
		}

		switch (property) {
			case NAME:
				String name;
				if (delta == null) {
					name = gui.getInventory().getType().getDefaultTitle();
				} else {
					name = (String) delta[0];
				}
				gui.setName(name);
				break;
			case ROWS:
				int size;
				if (delta == null) {
					size = gui.getInventory().getType().getDefaultSize();
				} else {
					size = ((Number) delta[0]).intValue() * 9;
				}
				gui.setSize(size);
				break;
			case SHAPE:
				if (delta == null) {
					gui.resetShape();
					break;
				}
				String[] newShape = new String[delta.length];
				for (int i = 0; i < delta.length; i++) {
					if (!(delta[i] instanceof String)) {
						return;
					}
					newShape[i] = (String) delta[i];
				}
				gui.setShape(newShape);
				break;
			case LOCK_STATUS:
				boolean value = false;
				if (delta != null) {
					value = (boolean) delta[0];
				}
				gui.setRemovable(value);
				break;
		}
	}

	@Override
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
				throw new IllegalArgumentException("Unknown property: " + property);
		}
	}

	@Override
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
				throw new IllegalArgumentException("Unknown property: " + property);
		}
	}

}
