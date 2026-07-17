package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Example;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import io.github.apickledwalrus.skriptgui.SkriptGUI;
import io.github.apickledwalrus.skriptgui.gui.GUI;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Last Created GUI")
@Description("Obtains the last created/edited GUI in a trigger.")
@Example("""
	open the last created gui for player
	""")
@Since("1.0.0")
public class ExprLastGUI extends SimpleExpression<GUI> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprLastGUI.class, GUI.class)
				.supplier(ExprLastGUI::new)
				.addPattern("[the] (last[ly] [created|edited]|created|edited) gui")
				.build());
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		return true;
	}

	@Override
	protected GUI[] get(Event event) {
		GUI gui = SkriptGUI.getGUIManager().getGUI(event);
		return gui != null ? new GUI[]{gui} : new GUI[0];
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
		return "the created gui";
	}

}
