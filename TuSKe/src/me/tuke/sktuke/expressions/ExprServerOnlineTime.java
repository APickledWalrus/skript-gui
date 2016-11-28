package me.tuke.sktuke.expressions;

import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import me.tuke.sktuke.TuSKe;

public class ExprServerOnlineTime extends SimpleExpression<Timespan>{

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public boolean init(Expression<?>[] arg0, int arg1, Kleenean arg2, ParseResult arg3) {
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "the online time of the server";
	}

	@Override
	@Nullable
	protected Timespan[] get(Event e) {
		return new Timespan[]{ new Timespan(System.currentTimeMillis() - TuSKe.getTime())};
	}

}
