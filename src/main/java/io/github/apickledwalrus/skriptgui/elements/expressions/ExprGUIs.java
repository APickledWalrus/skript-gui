package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("GUIs")
@Description("An expression to obtain all of the global/tracked GUIs.")
@Examples({
		"open a random gui out of all guis to all players"
})
@Since("1.3")
public class ExprGUIs extends SimpleExpression<GUI> {

	static {
		Skript.registerExpression(ExprGUIs.class, GUI.class, ExpressionType.SIMPLE,
				"[all [[of] the]|the] guis"
		);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return true;
	}

	@Override
	protected GUI[] get(Event event) {
		return SkriptGUI.getGUIManager().getTrackedGUIs().toArray(new GUI[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the guis";
	}

}
