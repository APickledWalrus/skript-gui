package me.tuke.sktuke.hooks.legendchat;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.tuke.sktuke.TuSKe;

public class ExprTagChat extends SimpleExpression<String>{

	private Expression<OfflinePlayer> p;
	private Expression<String> tag;
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
		if (arg1 == 0){
			p = (Expression<OfflinePlayer>) arg[1];
			tag = (Expression<String>) arg[0];
		} else {
			p = (Expression<OfflinePlayer>) arg[0];
			tag = (Expression<String>) arg[1];
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "tag chat " + this.tag + " of " + this.p;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		if (p.getSingle(e) == null || tag.getSingle(e) == null)
			return new String[0];
		return new String[] {TuSKe.getLegendConfig().getPlayerTag(p.getSingle(e), tag.getSingle(e))};
	}

	public void change(Event e, Object[] delta, ChangeMode mode){
		OfflinePlayer p = this.p.getSingle(e);
		String s = tag.getSingle(e);
		if (p == null)
			return;
		if (mode == ChangeMode.SET){
			if ((String) delta[0] != null)
				TuSKe.getLegendConfig().setPlayerTag(p, s, (String) delta[0]);
		} else
			TuSKe.getLegendConfig().clearTag(p, s);
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode){
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			return CollectionUtils.array(String.class);
		return null;
		
	}
}
