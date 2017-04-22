package me.tuke.sktuke.expressions.regex;

import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Regex;

import java.util.regex.Pattern;

public class ExprRegexReplace extends SimpleExpression<String>{
	static {
		Registry.newSimple(ExprRegexReplace.class,
				"regex replace (all|every|first|) [pattern] %regex/string% with [group[s]] %string% in %string%");
	}

	private Expression<?> regex;
	private Expression<String> with;
	private Expression<String> from;
	private boolean isFirst = false;
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		regex = arg[0].getConvertedExpression(Object.class);
		with = (Expression<String>) arg[1];
		from = (Expression<String>) arg[2];
		isFirst = arg3.expr.toLowerCase().startsWith("regex replace first");
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		String with = this.with.getSingle(e);
		String from = this.from.getSingle(e);
		Pattern p = Regex.getInstance().getPattern(regex.getSingle(e));
		if (from != null && with != null && p != null)
			return new String[]{Regex.getInstance().regexReplace(p, with, from, isFirst)};
		return null;
	}

}
