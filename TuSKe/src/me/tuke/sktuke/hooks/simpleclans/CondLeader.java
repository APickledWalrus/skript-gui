package me.tuke.sktuke.hooks.simpleclans;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class CondLeader extends Condition {
	private Expression<Player> p;
	private boolean neg = false;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		if (arg1 == 1)
			this.neg = true;
		this.p = (Expression<Player>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return this.p + "is[ not] leader of his clan";
	}

	@Override
	public boolean check(Event e) {
		Player p = this.p.getSingle(e);
		if (this.neg)
			return !(SimpleClans.getInstance().getClanManager().getClanPlayer(p) != null && SimpleClans.getInstance().getClanManager().getClanPlayer(p).isLeader());
		return (SimpleClans.getInstance().getClanManager().getClanPlayer(p) != null && SimpleClans.getInstance().getClanManager().getClanPlayer(p).isLeader());
	}

}
