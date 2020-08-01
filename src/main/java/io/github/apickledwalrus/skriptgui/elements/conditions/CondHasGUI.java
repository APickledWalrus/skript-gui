package io.github.apickledwalrus.skriptgui.elements.conditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

import io.github.apickledwalrus.skriptgui.SkriptGUI;

@Name("Has GUI")
@Description("Checks whether the given player(s) has/have a GUI open.")
@Examples({"command /guiviewers: # Returns a list of all players with a GUI open.",
			"\tset {_viewers::*} to all players where [input has a gui]",
			"\tsend \"GUI Viewers: %{_viewers::*}%\" to player"
})
@Since("1.0.0")
public class CondHasGUI extends Condition {

	static {
		Skript.registerCondition(CondHasGUI.class,
				"%players% (has|have) a gui [open]",
				"%players% (doesn't|does not|do not|don't) have a gui [open]"
		);
	}

	private Expression<Player> players;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		setNegated(matchedPattern % 2 != 0);
		return true;
	}

	@Override
	public boolean check(Event e) {
		return players.check(e, p -> SkriptGUI.getGUIManager().hasGUI(p), isNegated());
	}

	@Override
	public String toString(Event e, boolean debug) {
		return players.toString(e, debug) + (!isNegated() ? " has/have " : " do not/don't have ") + " a gui open";
	}

}
