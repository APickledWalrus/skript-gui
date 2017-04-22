package me.tuke.sktuke.hooks.simpleclans.effects;

import javax.annotation.Nullable;

import me.tuke.sktuke.util.Registry;
import org.bukkit.event.Event;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.sacredlabyrinth.phaed.simpleclans.Clan;

public class EffVerifyClan extends Effect{
	static {
		Registry.newEffect(EffVerifyClan.class, "verify [clan] %clan%");
	}

	private Expression<Clan> c;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.c = (Expression<Clan>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "verify " + this.c;
	}

	@Override
	protected void execute(Event e) {
		Clan c = this.c.getSingle(e);
		if (c != null && !c.isVerified())
			c.verifyClan();
		
		
	}
}
