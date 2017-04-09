package me.tuke.sktuke.hooks.simpleclans.expressions;

import me.tuke.sktuke.util.NewRegister;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class ExprClanFromTag extends SimpleExpression<Clan>{
	static {
		NewRegister.newSimple(ExprClanTag.class, "clan from tag %string%");
	}

	private Expression<String> s;
	@Override
	public Class<? extends Clan> getReturnType() {
		return Clan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.s = (Expression<String>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "clan from " + this.s;
	}

	@Override
	@Nullable
	protected Clan[] get(Event e) {
		String s = this.s.getSingle(e);
		Clan c = SimpleClans.getInstance().getClanManager().getClan(s.toLowerCase());
		if (c != null)
			return new Clan[] {c};
		return null;
	}

}
