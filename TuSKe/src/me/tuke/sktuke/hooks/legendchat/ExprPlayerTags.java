package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class ExprPlayerTags extends SimpleExpression<String>{

	private Expression<OfflinePlayer> p;
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
		this.p = (Expression<OfflinePlayer>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "tags chat of " + this.p;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		return this.p.getSingle(e) == null ? TuSKe.getLegendConfig().getPlayerTags(this.p.getSingle(e)).keySet().toArray(new String[TuSKe.getLegendConfig().getPlayerTags(this.p.getSingle(e)).size()]) : null;
	}

}
