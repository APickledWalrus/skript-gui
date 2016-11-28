package me.tuke.sktuke.hooks.landlord;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLandClaimAt extends SimpleExpression<LowOwnedLand>{

	private Expression<Object> l;
	@Override
	public Class<? extends LowOwnedLand> getReturnType() {
		return LowOwnedLand.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Object>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land[lord] claim at " + this.l;
	}

	@Override
	@Nullable
	protected LowOwnedLand[] get(Event e) {
		Object l = this.l.getSingle(e);
		if (l != null)
			if (l instanceof Location)
				return new LowOwnedLand[]{LowOwnedLand.getApplicableLand((Location)l)};
			else if (l instanceof Chunk)
				return new LowOwnedLand[]{LowOwnedLand.getApplicableLand(((Chunk)l).getBlock(0, 0, 0).getLocation())};
		return null;
	}

}
