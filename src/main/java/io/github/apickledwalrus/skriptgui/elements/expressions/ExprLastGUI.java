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
import org.jetbrains.annotations.Nullable;

@Name("Last GUI/GUI from ID")
@Description("It is used to return the last created/edited gui or a gui from a string id.")
@Examples({
		"open the created gui for player",
		"open the gui with the id \"globalGUI\" for player"
})
@Since("1.0.0")
public class ExprLastGUI extends SimpleExpression<GUI> {

	static {
		Skript.registerExpression(ExprLastGUI.class, GUI.class, ExpressionType.COMBINED,
				"[the] (last[ly] [created|edited]|(created|edited)) gui",
				"[the] gui [with [the] id[entifier]] %string%"
		);
	}

	private @Nullable Expression<String> id;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		if (matchedPattern == 1) {
			//noinspection unchecked
			id = (Expression<String>) exprs[0];
		}
		return true;
	}

	@Override
	protected GUI[] get(Event event) {
		GUI gui;
		if (id != null) {
			String id = this.id.getSingle(event);
			gui = id != null ? SkriptGUI.getGUIManager().getGUI(id) : null;
		} else {
			gui = SkriptGUI.getGUIManager().getGUI(event);
		}
		return gui != null ? new GUI[]{gui} : new GUI[0];
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
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert id != null;
		String id = this.id.getSingle(event);
		if (id == null) {
			return;
		}

		GUI gui = SkriptGUI.getGUIManager().getGUI(id);
		if (gui != null) {
			gui.setID(null);
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
	public String toString(@Nullable Event event, boolean debug) {
		if (id != null) {
			return "the gui with the id " + id.toString(event, debug);
		}
		return "the last created gui";
	}

}
