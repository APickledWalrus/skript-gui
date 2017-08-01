package com.github.tukenuke.tuske.expressions.regex;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.util.Regex;

import java.util.regex.Pattern;

public class ExprRegexSplit extends SimpleExpression<String>{
	static {
		Registry.newSimple(ExprRegexSplit.class, "regex split %string% (with|using) [pattern] %regex/string%");
	}

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
		final Pattern p = Regex.getInstance().getPattern(regex.getSingle(e));
		if (string != null && p != null)
			return Regex.getInstance().regexSplit(string, p);
		return null;
	}

}
