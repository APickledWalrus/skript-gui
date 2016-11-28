package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;
import org.bukkit.util.ChatPaginator;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprSplitCharacter extends SimpleExpression<String>{

	private Expression<String> str;
	private Expression<Number> id;
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
		id = (Expression<Number>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		if (str.getSingle(e) == null || id.getSingle(e) == null)
			return null;
		return ChatPaginator.wordWrap(str.getSingle(e), id.getSingle(e).intValue());
	}

}
