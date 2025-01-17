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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

@Name("Global GUI Identifiers")
@Description("A list of the identifiers of all registered global GUIs.")
@Examples({
		"command /guis:",
		"\ttrigger:",
		"\t\tloop all of the registered gui identifiers:",
		"\t\t\tsend loop-string"
})
@Since("1.2.1")
public class ExprGUIIdentifiers extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprGUIIdentifiers.class, String.class, ExpressionType.SIMPLE,
				"[all [[of] the]|the] (global|registered) gui id(s|entifiers)"
		);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return true;
	}

	@Override
	protected String[] get(Event event) {
		return stream(event).toArray(String[]::new);
	}

	@Override
	public Stream<? extends @NotNull String> stream(Event event) {
		return SkriptGUI.getGUIManager().getTrackedGUIs().stream()
				.map(GUI::getID)
				.filter(Objects::nonNull);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the registered gui identifiers";
	}

}
