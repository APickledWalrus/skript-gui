package me.tuke.sktuke.hooks.legendchat.expressions;

import me.tuke.sktuke.hooks.legendchat.LegendchatConfig;
import me.tuke.sktuke.hooks.legendchat.LegendchatRegister;
import me.tuke.sktuke.util.Registry;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import java.util.Map;

public class ExprPlayerTags extends SimpleExpression<String>{
	static final LegendchatConfig config = LegendchatRegister.config;
	static {
		Registry.newProperty(ExprPlayerTags.class, "[chat] tags", "offlineplayer");
	}

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
		OfflinePlayer p = this.p.getSingle(e);
		if (p != null) {
			Map<String, String> tags = config.getPlayerTags(p);
			return tags.keySet().toArray(new String[tags.size()]);
		}
		return null;
	}

}
