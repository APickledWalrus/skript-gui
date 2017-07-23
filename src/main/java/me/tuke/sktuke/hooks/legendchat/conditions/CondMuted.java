package me.tuke.sktuke.hooks.legendchat.conditions;

import me.tuke.sktuke.util.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import br.com.devpaulo.legendchat.api.Legendchat;
import br.com.devpaulo.legendchat.mutes.MuteManager;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondMuted extends Condition{
	static {
		Registry.newCondition(CondMuted.class, "%player% is muted", "%player% is(n't| not) muted");
	}
	private Expression<Player> p;
	private int neg;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int neg, Kleenean arg2, ParseResult arg3) {
		p = (Expression<Player>) arg[0];
		this.neg = neg;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return this.p.toString(e, arg1) + (isNegated() ? " is(n't| not) " : " is ") + " muted";
		}

	@Override
	public boolean check(Event e) {
	    MuteManager mm = Legendchat.getMuteManager();
		Player p = (Player)this.p.getSingle(e);
		if (this.neg == 0){
			return mm.isPlayerMuted(p.getName());
		}
		return !mm.isPlayerMuted(p.getName());
	}

}
