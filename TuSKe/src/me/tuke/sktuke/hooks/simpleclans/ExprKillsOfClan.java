package me.tuke.sktuke.hooks.simpleclans;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

public class ExprKillsOfClan extends SimpleExpression<Number>{

	private Expression<Clan> c;
	private int mark;
	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.c = (Expression<Clan>) arg[0];
		this.mark = arg3.mark;
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return null;
	}

	@Override
	@Nullable
	protected Number[] get(Event e) {
		Clan c = this.c.getSingle(e);
		if (c != null)
			switch (mark){
				case 1: return new Number[]{c.getTotalRival()};
				case 2: return new Number[]{c.getTotalNeutral()};
				case 3: return new Number[]{c.getTotalCivilian()};
				case 4: return new Number[]{c.getTotalKDR()};
				case 5: return new Number[]{c.getTotalDeaths()};
			}
		return null;
	}

}
