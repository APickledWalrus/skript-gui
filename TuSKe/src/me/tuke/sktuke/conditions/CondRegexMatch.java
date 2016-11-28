package me.tuke.sktuke.conditions;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondRegexMatch extends Condition{
	private Expression<String> str;
	private Expression<String> regex;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		str = (Expression<String>) arg[0];
		regex = (Expression<String>) arg[1];
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return str + " regex match " + regex;
	}

	@Override
	public boolean check(Event e) {
		if (str.getSingle(e) == null || regex.getSingle(e) == null)
			return false;
		boolean r = false;
		try {
			r = str.getSingle(e).matches(regex.getSingle(e));
		} catch (Exception ee) {
			return false;
		}
		if (isNegated())
			r = !r;
		return r;
	}

}
