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
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("ID of GUI")
@Description("""
	Obtains the ID of a global GUI.
	This expression may also be used to change the ID of a global GUI.
	It also allows applying an ID to a non-global GUI, thereby making it global.
	""")
@Example("""
	send "Your GUI ID: %id of gui of player%" to player
	""")
@Example("""
	set the id of the player's gui to "My New ID"
	""")
@Since("1.3.0")
public class ExprId extends SimplePropertyExpression<GUI, String> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			infoBuilder(ExprId.class, String.class, "[gui] id[entifier][s]", "guiinventorys", false)
				.supplier(ExprId::new)
				.build());
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
