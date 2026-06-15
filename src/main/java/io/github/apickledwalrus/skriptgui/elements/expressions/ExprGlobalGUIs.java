package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("All Global GUIs")
@Description("Obtains all of the global/tracked GUIs.")
@Example("""
	open a random gui out of all guis to all players
	""")
@Since("1.3.0")
public class ExprGlobalGUIs extends SimpleExpression<GUI> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprGlobalGUIs.class, GUI.class)
			.supplier(ExprGlobalGUIs::new)
			.addPattern("[all [[of] the]|the] global guis")
			.build());
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
		return "the global guis";
	}

}
