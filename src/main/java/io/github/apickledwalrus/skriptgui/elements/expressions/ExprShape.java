package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("GUI Shape")
@Description("The shape of a GUI, which controls how its items are laid out.")
@Example("""
	set the shape of the player's gui to "xxxxxxxxx", "x-------x", and "xxxxxxxxx"
	""")
@Since("1.0.0, 1.3.0 (support outside of edit sections)")
public class ExprShape extends SimplePropertyExpression<GUI, String> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			infoBuilder(ExprShape.class, String.class, "[gui] shape[s]", "guiinventories", false)
				.supplier(ExprShape::new)
				.build());
	}

	@Override
	public String convert(GUI gui) {
		return gui.getRawShape();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case ADD, DELETE, RESET -> CollectionUtils.array(String[].class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		if (delta == null) {
			for (GUI gui : getExpr().getArray(event)) {
				gui.resetShape();
			}
			return;
		}

		String[] newShape = new String[delta.length];
		for (int i = 0; i < delta.length; i++) {
			newShape[i] = (String) delta[i];
		}
		for (GUI gui : getExpr().getArray(event)) {
			gui.setShape(newShape);
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "shape";
	}

}
