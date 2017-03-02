package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Regex;

public class ExprRegexSplit extends SimpleExpression<String>{

	private Expression<String> str;
	private Expression<?> regex;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		str = (Expression<String>) arg[0];
		regex = arg[1].getConvertedExpression(Object.class);
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "regex split " + str + " with " + regex;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		String string = str.getSingle(e);
		final Regex reg = regex.getSingle(e) instanceof String ? new Regex((String)regex.getSingle(e)) : (Regex)regex.getSingle(e);
		if (string != null && reg != null && reg.isPatternParsed())
			return string.split(reg.getRegex());
		return null;
	}

}
