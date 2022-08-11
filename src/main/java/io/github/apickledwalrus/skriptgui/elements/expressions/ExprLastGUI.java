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
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.eclipse.jdt.annotation.Nullable;

@Name("Last GUI/GUI from ID")
@Description("It is used to return the last created/edited gui or a gui from a string id.")
@Examples({
		"open the created gui for player",
		"open the gui with the id \"globalGUI\" for player"
})
@Since("1.0.0")
public class ExprLastGUI extends SimpleExpression<GUI> {

	static {
		Skript.registerExpression(ExprLastGUI.class, GUI.class, ExpressionType.SIMPLE,
				"[the] (last[ly] [(created|edited)]|(created|edited)) gui",
				"[the] gui [with [the] id[entifier]] %string%"
		);
	}

	@Nullable
	private Expression<String> id;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (matchedPattern == 1) {
			id = (Expression<String>) exprs[0];
		}
		return true;
	}

	@Override
	protected GUI[] get(Event e) {
		if (id != null) {
			String id = this.id.getSingle(e);
			return id != null ? new GUI[]{SkriptGUI.getGUIManager().getGUI(id)} : new GUI[0];
		}
		return new GUI[]{SkriptGUI.getGUIManager().getGUI(e)};
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.DELETE && id != null) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}

	@Override
	public void change(Event e, Object @Nullable [] delta, ChangeMode mode) {
		if (id != null) {
			String id = this.id.getSingle(e);
			if (id != null) {
				GUI gui = SkriptGUI.getGUIManager().getGUI(id);
				if (gui != null) {
					gui.setID(null);
				}
			}
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return id == null ? "the last gui" : "the gui with the id " + id.toString(e, debug);
	}

}
