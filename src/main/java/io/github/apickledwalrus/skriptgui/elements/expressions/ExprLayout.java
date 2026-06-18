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

@Name("GUI Layout")
@Description("The layout of a GUI, which controls how its items are displayed.")
@Example("""
	set the layout of the player's gui to "xxxxxxxxx", "x-------x", and "xxxxxxxxx"
	""")
@Since("1.0.0, 1.3.0 (support outside of edit sections)")
public class ExprLayout extends SimplePropertyExpression<GUI, String> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			infoBuilder(ExprLayout.class, String.class, "[gui] (layout|shape)[s]", "guis", false)
				.supplier(ExprLayout::new)
				.build());
	}

	@Override
	public String convert(GUI gui) {
		return gui.getLayout();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, DELETE, RESET -> CollectionUtils.array(String[].class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		String layout = null;
		if (delta != null) {
			StringBuilder layoutBuilder = new StringBuilder();
			for (Object string : delta) {
				layoutBuilder.append((String) string);
			}
			layout = layoutBuilder.toString();
		}

		for (GUI gui : getExpr().getArray(event)) {
			gui.setLayout(layout);
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "layout";
	}

}
