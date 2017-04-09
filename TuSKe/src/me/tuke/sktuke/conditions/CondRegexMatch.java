package me.tuke.sktuke.conditions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Regex;

public class CondRegexMatch extends Condition{
	static {
		NewRegister.newCondition(CondRegexMatch.class, "%string% [regex] matches %string%", "%string% [regex] does(n't| not) match %string%");
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
		if (str.getSingle(e) == null || regex.getSingle(e) == null)
			return false;
		final Regex reg = regex.getSingle(e) instanceof String ? new Regex((String)regex.getSingle(e)) : (Regex)regex.getSingle(e);
		if (!reg.isPatternParsed())
			return false;
		return str.check(e, new Checker<String>() {

			@Override
			public boolean check(String arg) {
				return arg.matches(reg.getRegex());
			}}, isNegated());
	}

}
