package me.tuke.sktuke.hooks.legendchat.expressions;

import me.tuke.sktuke.hooks.legendchat.LegendchatConfig;
import me.tuke.sktuke.hooks.legendchat.LegendchatRegister;
import me.tuke.sktuke.util.NewRegister;
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
	static final LegendchatConfig config = LegendchatRegister.config;
	static {
		NewRegister.newProperty(ExprTagChat.class, "[chat] tag %string%", "offlineplayer");
	}

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
		OfflinePlayer p = this.p.getSingle(e);
		String tag = this.tag.getSingle(e);
		if (p == null || tag == null)
			return new String[0];
		return new String[] {config.getPlayerTag(p, tag)};
	}

	public void change(Event e, Object[] delta, ChangeMode mode){
		OfflinePlayer p = this.p.getSingle(e);
		String s = tag.getSingle(e);
		if (p == null || s == null)
			return;
		if (mode == ChangeMode.SET){
			if (delta[0] != null)
				config.setPlayerTag(p, s, (String) delta[0]);
		} else
			config.clearTag(p, s);
	}
	@SuppressWarnings("unchecked")
	public Class<?>[] acceptChange(final Changer.ChangeMode mode){
		if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			return CollectionUtils.array(String.class);
		return null;
		
	}
}
