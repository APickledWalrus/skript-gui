package me.tuke.sktuke.hooks.marriage;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.lenis0012.bukkit.marriage2.Marriage;
import com.lenis0012.bukkit.marriage2.MarriageAPI;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class CondMarried extends Condition{
	private Expression<Player> p;
	private int neg;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int neg, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		this.neg = neg;
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "married";
	}

	
	public boolean check(Event e) {
		Player p = this.p.getSingle(e);
		Marriage marry = (Marriage) MarriageAPI.getInstance();
		if (this.neg == 0)
			return (marry.getMPlayer(p.getUniqueId()).isMarried());
		return !(marry.getMPlayer(p.getUniqueId()).isMarried());
	}

}
