package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.util.Regex;

public class ExprRegexReplace extends SimpleExpression<String>{

	private Expression<?> regex;
	private Expression<String> with;
	private Expression<String> from;
	
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
		regex = arg[0];
		with = (Expression<String>) arg[1];
		from = (Expression<String>) arg[2];
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
		if (from != null){
			if (with != null && regex.getSingle(e) != null){
				final Regex reg = regex.getSingle(e) instanceof String ? new Regex((String)regex.getSingle(e)) : (Regex)regex.getSingle(e);
				try {
					from = from.replaceAll(reg.getRegex(), with);
				} catch (IndexOutOfBoundsException ibe){
					ExprParseRegexError.parserError = ibe.getMessage() + " in '" +reg.getRegex() + "' with replacement '" + with + "'";
				}
			}
			return new String[]{from};
		}
		return null;
	}

}
