package io.github.apickledwalrus.skriptgui.elements.expressions;

import ch.njol.skript.doc.Example;
import io.github.apickledwalrus.skriptgui.gui.GUIManager;
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

import java.util.Objects;

@Name("GUI with ID")
@Description("Obtains a global GUI based on its ID.")
@Example("""
	open the gui with the id "globalGUI" for player
	""")
@Since("1.0.0")
public class ExprGUIWithId extends SimpleExpression<GUI> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION,
			SyntaxInfo.Expression.builder(ExprLastGUI.class, GUI.class)
				.supplier(ExprLastGUI::new)
				.addPattern("[the] gui[s] [with [the] id[entifier][s]] %strings%")
				.build());
	}

	private Expression<String> ids;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		//noinspection unchecked
		ids = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected GUI[] get(Event event) {
		GUIManager manager = SkriptGUI.getGUIManager();
		return ids.stream(event)
			.map(manager::getGUI)
			.filter(Objects::nonNull)
			.toArray(GUI[]::new);
	}

	@Override
	public boolean isSingle() {
		return ids.isSingle();
	}

	@Override
	public Class<? extends GUI> getReturnType() {
		return GUI.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (ids.isSingle()) {
			return "the guis with the identifiers " + ids.getSingle(event);
		}
		return "the gui with the identifier " + ids.toString(event, debug);
	}

}
