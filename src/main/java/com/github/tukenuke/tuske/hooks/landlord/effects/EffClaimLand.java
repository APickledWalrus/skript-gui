package com.github.tukenuke.tuske.hooks.landlord.effects;

import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.jcdesimp.landlord.persistantData.LowOwnedLand;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffClaimLand extends Effect{
	static {
		Registry.newEffect(EffClaimLand.class, "claim land[lord] at %location/chunk% for %player%");
	}

	private Expression<Object> l;
	private Expression<Player> p;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Object>) arg[0];
		this.p = (Expression<Player>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "claim land at " + this.l + " for " + this.p;
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		Object o = this.l.getSingle(e);
		if (p == null || o == null)
			return;
		Location l;
		if (o instanceof Chunk)
			l = ((Chunk)o).getBlock(0, 50, 0).getLocation();
		else
			l = (Location)o;
		if (LowOwnedLand.getApplicableLand(l) == null){
			LowOwnedLand ol = LowOwnedLand.landFromProperties(p, l.getChunk());
			ol.save();
			//Landlord.getInstance().getDatabase().save(ol);
		}
			
		
	}

}
