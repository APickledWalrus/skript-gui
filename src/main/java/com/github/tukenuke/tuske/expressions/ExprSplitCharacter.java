package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.event.Event;
import org.bukkit.util.ChatPaginator;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
@Name("Split Characters")
@Description("Split a text with {{types|Number|number}} amount of characters. It is used to split the message in chat (the default is 60) and to item's lore.")
@Examples("set {_s::*} to split \"Hi, this text will be splitted in 3 lines\" by 10 characters")
@Since("1.6.8")
public class ExprSplitCharacter extends SimpleExpression<String>{
	static {
		Registry.newSimple(ExprSplitCharacter.class, "split %string% (with|by|using) %number% [char[acter][s]]", "%string% [split] (with|by|using) %number% [char[acter][s]]");
	}

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
