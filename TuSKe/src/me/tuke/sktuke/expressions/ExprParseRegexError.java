package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.doc.Examples;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprParseRegexError extends SimpleExpression<String>{

	public static String parserError = null;
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "last regex parser error";
	}

	@Override
	@Nullable
	protected String[] get(Event arg0) {
		return new String[]{parserError};
	}

}
