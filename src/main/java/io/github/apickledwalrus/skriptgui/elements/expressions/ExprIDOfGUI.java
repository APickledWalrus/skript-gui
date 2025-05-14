package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("ID of GUI")
@Description({
		"An expression that returns the ID of a GUI if the GUI is a global GUI.",
		"This expression may be used to change the ID of a global GUI.",
		"It may also be used to register a GUI as a global GUI."
})
@Examples({
		"send \"%id of {gui}%\" to player",
		"set id of {gui} to \"new id\"",
})
@Since("1.3")
public class ExprIDOfGUI extends SimplePropertyExpression<GUI, String> {

	static {
		register(ExprIDOfGUI.class, String.class, "[[skript-]gui] id[entifier][s]", "guiinventorys");
	}

	@Override
	public String convert(GUI gui) {
		return gui.getID();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			return CollectionUtils.array(String.class);
		}
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null; // should not be null for SET
		String id = (String) delta[0];
		for (GUI gui : getExpr().getArray(event)) {
			gui.setID(id);
		}
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "identifier";
	}

}
