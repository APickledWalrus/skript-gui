package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;

import java.util.regex.PatternSyntaxException;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprRegexSplit extends SimpleExpression<String>{

	private Expression<String> str;
	private Expression<String> regex;
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
		regex = (Expression<String>) arg[1];
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
		String pattern = regex.getSingle(e);
		if (string != null && pattern != null)
			try {
			return string.split(pattern);
			} catch (PatternSyntaxException pse){
				return new String[]{string};
			}
		return null;
	}

}
