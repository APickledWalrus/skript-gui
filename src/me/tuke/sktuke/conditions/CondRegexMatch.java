package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Regex;

import java.util.regex.Pattern;

public class CondRegexMatch extends Condition{
	static {
		Registry.newCondition(CondRegexMatch.class,
				"%strings% [regex] matches %string%",
				"%strings% [regex] does(n't| not) match %string%");
	}
	private Expression<String> str;
	private Expression<?> regex;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		str = (Expression<String>) arg[0];
		regex = arg[1].getConvertedExpression(Object.class);
		setNegated(arg1 == 1);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return str + " regex match " + regex;
	}

	@Override
	public boolean check(Event e) {
		final Pattern p = Regex.getInstance().getPattern(regex.getSingle(e));
		if (p == null)
			return false;
		return str.check(e, from -> p.matcher(from).matches(), isNegated());
	}

}
