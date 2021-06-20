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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Last GUI/GUI from id")
@Description("It is used to return the last created gui or a gui from a string id.")
@Examples({
		"open gui last gui for player",
		"open gui (gui with id \"globalGUI\") for player"
})
@Since("1.0.0")
public class ExprLastGUI extends SimpleExpression<GUI> {

	static {
		Skript.registerExpression(ExprLastGUI.class, GUI.class, ExpressionType.SIMPLE,
				"[the] last[ly] [created] gui",
				"[the] gui [with [the] id[entifier]] %string%"
		);
	}

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
			return id != null ? new GUI[]{SkriptGUI.getGUIManager().getGlobalGUI(id)} : new GUI[0];
		}
		return new GUI[]{SkriptGUI.getGUIManager().getGUIEvent(e)};
	}

	@Override
	public Class<?>[] acceptChange(final ChangeMode mode) {
		return (mode == ChangeMode.DELETE && id != null) ? CollectionUtils.array(Object.class) : null;
	}

	@Override
	public void change(final Event e, Object @Nullable [] delta, ChangeMode mode){
		String id = this.id.getSingle(e);
		if (id != null) {
			GUI gui = SkriptGUI.getGUIManager().getGlobalGUI(id);
			if (gui != null) {
				gui.setID(null);
				gui.clear();
			}
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	@NotNull
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	@NotNull
	public String toString(@Nullable Event e, boolean debug) {
		return id == null ? "last gui" : "gui with id" + id.toString(e, debug);
	}

}
